# ADR-020 : Connexion parent par OTP (sans compte à la volée) et gestion complète des enfants

**Date :** 2026-08-18
**Statut :** Accepté
**Décideur :** Yacouba SYLLA (avec Claude Code)

---

## Contexte

Un mandat demandait un « wizard d'accès parent par OTP » (WhatsApp/SMS/Email, vérification d'un
numéro suffisant à obtenir un jeton de session et gérer des enfants) et un module de gestion des
enfants (ajout/modification/suppression + matricule auto-généré `E|ANNÉE|RANG`).

Deux points du mandat entraient en conflit direct avec l'existant :
1. Un flux d'accès parent existe déjà : **demande d'accès** publique → **validation ADMIN**
   obligatoire → création du compte. Une décision antérieure (`decision-log.md`) avait
   explicitement **rejeté** l'activation immédiate sans contrôle admin, pour cause de risque de
   faux comptes en contexte scolaire. Le wizard OTP tel que décrit (numéro vérifié = accès direct)
   réintroduisait exactement ce risque.
2. Le mandat visait par erreur `apps/03_cantine_connect/`/`services/core-api/`/package
   `ci.klem.core.student` — un service KLEM DataSphere sans rapport, comme pour un précédent
   mandat paiement déjà corrigé de la même façon (voir ADR-019 côté paiement pour le précédent).

Ajout d'enfant self-service (`POST /parents/moi/enfants`), profil `Parent`/`Utilisateur` et
génération de QR code existaient déjà et fonctionnent en production — non-régression requise.

## Décision Retenue

> **L'OTP authentifie un compte PARENT déjà approuvé (recherché par téléphone), il ne crée
> jamais de compte ni d'enfant. Un numéro sans compte reçoit une erreur explicite invitant à
> soumettre la demande d'accès existante.** Une fois vérifié, l'OTP délivre exactement le même
> jeton qu'une connexion par mot de passe (`AuthResponseDTO`/`JwtService`), donnant accès aux
> endpoints parent déjà existants — pas de duplication de logique métier.

Implémentation :
- `paiement`-style Strategy déjà en place pour les canaux (`NotificationSender`) réutilisée :
  nouveau `WhatsAppNotificationSender` (Twilio, même compte que SMS) + entrée `WHATSAPP` dans
  `NotificationDispatcher` — aucune ligne des canaux existants modifiée.
- `ParentOtpService`/`ParentOtpController` (`POST /api/v1/parents/otp/{send,verify}`, publics) —
  nouveau package `parent.otp`, `OtpStore` (interface) + `InMemoryOtpStore` (5 min, 5 tentatives,
  usage unique ; remplaçable par Redis sans impact sur les appelants si l'app passe un jour
  multi-instance).
- Gestion complète des enfants côté parent, en étendant `EleveController`/`ParentController`
  existants plutôt qu'un nouveau contrôleur `Student` parallèle : `PUT`/`DELETE
  /parents/moi/enfants/{id}` (nouveaux), réutilisant `EleveService`/`TransactionPaiement`/
  `Parent` existants — vérification de propriété (`AccessDeniedException` si l'enfant n'appartient
  pas au parent).
- Matricule (`E<ANNÉE><RANG 4 chiffres>`) généré une seule fois à la création via
  `MatriculeGenerator` (upsert atomique PostgreSQL `ON CONFLICT ... RETURNING`, thread-safe et
  sûr en multi-instance sans verrou applicatif), immuable ensuite. Confirmé par le porteur du
  projet que Cantine Connect n'a jamais eu de matricule scolaire officiel à faire coïncider — la
  saisie manuelle du champ est retirée des formulaires de création (`EleveRequestDTO`,
  `AjoutEnfantRequestDTO`), jamais du formulaire de modification (immuable).
- `ParentResponseDTO.EnfantDTO` étendu (etablissementId, classeId, sexe, dateNaissance, ville,
  commune, quartier) pour permettre au frontend de pré-remplir le formulaire de modification sans
  endpoint de lecture supplémentaire (le seul existant, `GET /eleves/{id}`, est interdit au rôle
  PARENT) — et filtré sur `actif` pour que désactiver un enfant le retire bien de la liste.
- Frontend : nouvelle page `ParentOtpAccessPage.jsx` (`/acces-otp`, wizard 2 étapes MUI Stepper),
  réutilisant `useAuth().login()` — le parent atterrit ensuite sur la page existante
  `MesEnfantsPage.jsx` (`/mes-enfants`), étendue avec Modifier/Désactiver/QR Code par carte (pas
  de nouveau wizard de tableau de bord : celui-ci existait déjà).

## Alternatives envisagées

- **OTP = accès instantané sans compte préalable** (tel que décrit littéralement dans le mandat) —
  rejetée : réintroduit un risque de faux comptes déjà explicitement écarté par une décision
  antérieure documentée.
- **Nouveau module `Student`/`ci.klem.core.student` parallèle à `Eleve`/`Parent`** — rejetée :
  aurait dupliqué le modèle de données et la logique métier (paiement, scan, notifications) déjà
  branchés sur `Eleve`, pour un gain nul.
- **Stockage OTP dans Redis dès cette itération** — écarté pour l'instant (topologie mono-instance
  actuelle) au profit d'un stockage en mémoire derrière l'interface `OtpStore`, remplaçable sans
  impact si l'application passe en plusieurs instances.
- **Conserver le matricule en saisie manuelle** — écarté après confirmation du porteur du projet
  qu'aucun matricule scolaire officiel n'existe à faire coïncider dans ce système.

## Conséquences et Impacts

### ✅ Impacts Positifs (Gains)
- Connexion parent nettement plus rapide (pas de mot de passe à retenir), sans affaiblir le
  contrôle admin déjà en place sur la création de comptes.
- Gestion des enfants désormais complète côté parent (ajout déjà existant + modification/
  désactivation/QR code nouveaux), sans nouveau modèle de données ni duplication de logique.
- 66/66 tests backend verts (55 existants inchangés + 11 nouveaux : `MatriculeGeneratorTest`,
  `InMemoryOtpStoreTest`, `ParentOtpServiceTest`, ownership `EleveServiceTest`).

### ⚠️ Impacts Négatifs ou Risques (Compromis acceptés)
- `InMemoryOtpStore` ne survit pas à un redémarrage et n'est pas partagé entre plusieurs instances
  — acceptable tant que le déploiement reste mono-instance (voir alternatives).
- Le format de signature du webhook Orange Money (module paiement, ADR-019) n'est pas concerné ici,
  mais le canal WhatsApp (Twilio) partage la même limitation de vérification de signature IPN —
  sans objet pour l'envoi d'OTP (canal sortant uniquement, pas de webhook entrant).
- `PaymentProviderType`/`OperateurMobileMoney` (ADR-019) restent un sujet distinct, sans lien avec
  cet ADR.

---
## Suivi et Validation
- [x] Code mis à jour selon l'ADR (`parent.otp.*`, `MatriculeGenerator`, extensions
      `EleveService`/`ParentController`/`ParentResponseDTO`, `ParentOtpAccessPage.jsx`,
      `MesEnfantsPage.jsx`).
- [x] Fichier `history-log.md` mis à jour après implémentation.
