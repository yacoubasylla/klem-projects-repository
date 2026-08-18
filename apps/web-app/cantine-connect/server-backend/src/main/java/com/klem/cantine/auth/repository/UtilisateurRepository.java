package com.klem.cantine.auth.repository;

import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmailAndActifTrue(String email);
    Optional<Utilisateur> findByTelephoneAndRoleAndActifTrue(String telephone, Role role);
    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
    long countByRoleAndActifTrue(Role role);

    // Requête native — voir ADR-007 : Hibernate 6 + PostgreSQL échouent à inférer le type
    // d'un paramètre JPQL nullable utilisé dans LOWER()/LIKE ("operator does not exist: lower(bytea)").
    // Le CAST explicite (CAST(:param AS ...)) contourne le problème, y compris pour actif (boolean)
    // et les dates (voir ADR-010/013 : même pattern étendu à tous les filtres optionnels).
    @Query(value = """
            SELECT * FROM utilisateurs u
            WHERE (CAST(:role AS varchar) IS NULL OR u.role = CAST(:role AS varchar))
              AND (CAST(:actif AS boolean) IS NULL OR u.actif = CAST(:actif AS boolean))
              AND (CAST(:dateDebut AS date) IS NULL OR u.created_at >= CAST(:dateDebut AS date))
              AND (CAST(:dateFin AS date) IS NULL OR u.created_at < CAST(:dateFin AS date) + INTERVAL '1 day')
              AND (CAST(:search AS varchar) IS NULL
                   OR LOWER(u.nom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR u.telephone LIKE CONCAT('%', CAST(:search AS varchar), '%'))
            """,
           countQuery = """
            SELECT COUNT(*) FROM utilisateurs u
            WHERE (CAST(:role AS varchar) IS NULL OR u.role = CAST(:role AS varchar))
              AND (CAST(:actif AS boolean) IS NULL OR u.actif = CAST(:actif AS boolean))
              AND (CAST(:dateDebut AS date) IS NULL OR u.created_at >= CAST(:dateDebut AS date))
              AND (CAST(:dateFin AS date) IS NULL OR u.created_at < CAST(:dateFin AS date) + INTERVAL '1 day')
              AND (CAST(:search AS varchar) IS NULL
                   OR LOWER(u.nom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR u.telephone LIKE CONCAT('%', CAST(:search AS varchar), '%'))
            """,
           nativeQuery = true)
    Page<Utilisateur> findAllFiltered(
            @Param("role") String role, @Param("actif") Boolean actif,
            @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin,
            @Param("search") String search, Pageable pageable);
}
