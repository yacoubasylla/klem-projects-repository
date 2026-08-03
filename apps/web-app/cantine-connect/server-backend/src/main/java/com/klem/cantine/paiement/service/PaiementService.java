package com.klem.cantine.paiement.service;

import com.klem.cantine.actionlog.annotation.Traceable;
import com.klem.cantine.actionlog.entity.TypeAction;
import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;
import com.klem.cantine.paiement.dto.ModifierPaiementRequestDTO;
import com.klem.cantine.paiement.dto.PaiementResponseDTO;
import com.klem.cantine.paiement.entity.OperateurMobileMoney;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.entity.TransactionPaiement;
import com.klem.cantine.paiement.provider.PaymentProvider;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.parametrage.service.ConfigurationService;
import com.klem.cantine.parent.repository.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaiementService {

    private final TransactionPaiementRepository transactionRepository;
    private final EleveRepository eleveRepository;
    private final ParentRepository parentRepository;
    private final WebhookService webhookService;
    private final List<PaymentProvider> paymentProviders;
    private final ConfigurationService configurationService;

    @Traceable(action = TypeAction.CREATE, entite = "TransactionPaiement")
    @Transactional
    public PaiementResponseDTO initierPaiement(InitierPaiementRequestDTO dto, Utilisateur principal) {
        if (principal.getRole() == Role.PARENT && !enfantIds(principal).contains(dto.eleveId())) {
            throw new AccessDeniedException("Vous ne pouvez initier un paiement que pour vos propres enfants");
        }

        var eleve = eleveRepository.findByIdActive(dto.eleveId())
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable : " + dto.eleveId()));

        String referenceInterne = UUID.randomUUID().toString();

        TransactionPaiement transaction = TransactionPaiement.builder()
                .eleve(eleve)
                .referenceInterne(referenceInterne)
                .operateur(dto.operateur())
                .montant(dto.montant())
                .telephonePayeur(dto.telephonePayeur())
                .build();

        transaction = transactionRepository.save(transaction);

        String paymentUrl = resolveProviderActif().initierPaiement(referenceInterne, dto);
        log.info("Paiement initié : ref={} operateur={} montant={} XOF",
                referenceInterne, dto.operateur(), dto.montant());

        return PaiementResponseDTO.from(transaction, paymentUrl);
    }

    public Page<PaiementResponseDTO> lister(Long eleveId, StatutPaiement statut, OperateurMobileMoney operateur,
                                             LocalDate dateDebut, LocalDate dateFin, String search,
                                             Pageable pageable, Utilisateur principal) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String statutParam = statut != null ? statut.name() : null;
        String operateurParam = operateur != null ? operateur.name() : null;
        // Les requêtes natives ci-dessous fixent déjà leur propre ORDER BY (t.date_creation) ;
        // repasser le Sort du Pageable ferait que Spring Data l'ajoute tel quel en SQL
        // (ex. "dateCreation" au lieu de "date_creation") et casse la requête.
        Pageable unsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        if (principal.getRole() == Role.PARENT) {
            List<Long> enfantIds = enfantIds(principal);
            if (eleveId != null && !enfantIds.contains(eleveId)) {
                throw new AccessDeniedException("Accès refusé à cet élève");
            }
            if (enfantIds.isEmpty()) {
                return Page.empty(pageable);
            }
            return transactionRepository.findAllWithFiltersForEleves(
                            enfantIds, eleveId, statutParam, operateurParam, dateDebut, dateFin, searchParam, unsorted)
                    .map(PaiementResponseDTO::from);
        }
        return transactionRepository.findAllWithFilters(
                        eleveId, statutParam, operateurParam, dateDebut, dateFin, searchParam, unsorted)
                .map(PaiementResponseDTO::from);
    }

    public PaiementResponseDTO getById(Long id, Utilisateur principal) {
        TransactionPaiement t = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable : " + id));
        if (principal.getRole() == Role.PARENT && !enfantIds(principal).contains(t.getEleve().getId())) {
            throw new EntityNotFoundException("Transaction introuvable : " + id);
        }
        return PaiementResponseDTO.from(t);
    }

    private List<Long> enfantIds(Utilisateur principal) {
        return parentRepository.findEnfantIdsByUtilisateurId(principal.getId());
    }

    @Traceable(action = TypeAction.UPDATE, entite = "TransactionPaiement")
    @Transactional
    public PaiementResponseDTO modifier(Long id, ModifierPaiementRequestDTO dto) {
        TransactionPaiement t = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable : " + id));
        StatutPaiement ancienStatut = t.getStatut();
        if (dto.statut() != null) t.setStatut(dto.statut());
        if (dto.montant() != null) t.setMontant(dto.montant());
        if (dto.operateur() != null) t.setOperateur(dto.operateur());
        if (dto.telephonePayeur() != null) t.setTelephonePayeur(dto.telephonePayeur());
        TransactionPaiement saved = transactionRepository.save(t);

        // Confirmation manuelle (ex. paiement en espèces) : mêmes effets qu'un webhook accepté
        if (saved.getStatut() == StatutPaiement.ACCEPTE && ancienStatut != StatutPaiement.ACCEPTE) {
            webhookService.appliquerPaiementAccepte(saved);
        }

        return PaiementResponseDTO.from(saved);
    }

    @Traceable(action = TypeAction.DELETE, entite = "TransactionPaiement")
    @Transactional
    public void supprimer(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction introuvable : " + id);
        }
        transactionRepository.deleteById(id);
    }

    // ── Résolution du provider de paiement actif ──────────────────────────────

    /**
     * Sélectionne le {@link PaymentProvider} désigné par la configuration
     * `PAIEMENT_PROVIDER_ACTIF` (ex. CINETPAY, PAYDUNYA, ORANGE_MONEY_DIRECT...).
     * Repli sur CinetPay si la config est absente/invalide.
     */
    private PaymentProvider resolveProviderActif() {
        String code = configurationService.getValeur("PAIEMENT_PROVIDER_ACTIF");
        Map<String, PaymentProvider> byCode = paymentProviders.stream()
                .collect(java.util.stream.Collectors.toMap(PaymentProvider::getCode, Function.identity()));
        return byCode.getOrDefault(code, byCode.get("CINETPAY"));
    }
}
