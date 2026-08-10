package com.klem.coreapi.identity.infrastructure.persistence;

import com.klem.coreapi.identity.application.port.UserRepository;
import com.klem.coreapi.identity.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface UserJpaRepository extends JpaRepository<User, UUID>, UserRepository {

    @Override
    Optional<User> findByKeycloakSubject(String keycloakSubject);

    @Override
    Optional<User> findByEmail(String email);
}
