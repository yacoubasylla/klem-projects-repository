# ADR-022 : Canal OTP parent paramétrable (WhatsApp par défaut, bascule SMS)

**Date :** 2026-08-19
**Statut :** Accepté
**Décideur :** Yacouba SYLLA (avec Claude Code)

---

## Contexte

Demande explicite : recevoir le code OTP par WhatsApp par défaut, avec un paramétrage permettant
de basculer vers SMS. Jusqu'ici, `ParentOtpService` déléguait l'envoi à `NotificationDispatcher`,
qui diffuse sur **tous** les canaux actifs simultanément (EMAIL + SMS + WHATSAPP si les trois sont
activés via `NOTIFICATIONS_*_ENABLED`) — pas de notion de « un seul canal téléphone, au choix ».

## Décision Retenue

> **`ParentOtpService` sélectionne directement le `NotificationSender` du canal téléphone
> configuré via la nouvelle clé `PARENT_OTP_CANAL_TELEPHONE` (`WHATSAPP` par défaut, `SMS` en
> alternative), au lieu de passer par `NotificationDispatcher`. L'email est envoyé en parallèle,
> indépendamment de ce choix.**

Implémentation :
- Migration `V17__parent_otp_canal_telephone.sql` : seed `PARENT_OTP_CANAL_TELEPHONE = 'WHATSAPP'`.
- `ParentOtpService` injecte `List<NotificationSender>` + `ConfigurationService` (plus
  `NotificationDispatcher`) ; `resoudreCanalTelephone()` lit la config (SMS si explicitement
  configuré, WhatsApp sinon) et `envoyerSurCanal(canal, destinataire, sujet, corps)` invoque
  directement le sender correspondant à ce canal.
- **Ne consulte pas `NOTIFICATIONS_SMS_ENABLED`/`NOTIFICATIONS_WHATSAPP_ENABLED`** : ces bascules
  gouvernent les notifications optionnelles (paiement confirmé, passage cantine) ailleurs dans
  l'app ; l'OTP est une étape fonctionnelle de connexion, elle doit partir quel que soit l'état de
  ces bascules générales.
- Admin : nouveau bloc dans `ConfigurationPage.jsx` (catégorie « Notifications »), même patron
  visuel que le sélecteur `MODE_PAIEMENT` déjà existant.

## Alternatives envisagées

- **Réutiliser `NotificationDispatcher` tel quel** (diffusion sur tous les canaux actifs) —
  écartée : ne permet pas de choisir *un seul* canal téléphone, contradictoire avec la demande
  d'un choix exclusif WhatsApp/SMS.
- **Gate par `NOTIFICATIONS_WHATSAPP_ENABLED`/`NOTIFICATIONS_SMS_ENABLED`** — écartée : ces
  bascules existent pour des notifications informatives optionnelles, pas pour une étape
  d'authentification qui doit fonctionner par défaut.

## Conséquences et Impacts

### ✅ Impacts Positifs (Gains)
- Choix explicite et immédiat (WhatsApp par défaut, un ADMIN peut basculer vers SMS sans
  redéploiement). 68/68 tests backend verts (62 existants inchangés + nouveaux cas de canal).

### ⚠️ Impacts Négatifs ou Risques (Compromis acceptés)
- Deux mécanismes de sélection de canal coexistent désormais dans l'app : le `NotificationDispatcher`
  générique (diffusion multi-canal pour les autres notifications) et cette sélection directe
  propre à l'OTP — documenté ici pour éviter toute confusion future.

---
## Suivi et Validation
- [x] Code mis à jour selon l'ADR.
- [x] Fichier `history-log.md` mis à jour après implémentation.
