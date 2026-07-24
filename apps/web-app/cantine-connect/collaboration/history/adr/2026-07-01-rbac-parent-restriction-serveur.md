# ADR-011 : RBAC Serveur pour le Rôle PARENT (Périmètre Restreint aux Enfants)

**Date :** 2026-07-01
**Statut :** Accepté
**Décideur :** Yacouba SYLLA

---

## Contexte

Le rôle `PARENT` a été introduit pour permettre aux parents de suivre et payer la cantine de leurs propres enfants. Sans restriction, un compte PARENT authentifié pouvait interroger les mêmes endpoints que le personnel (`/eleves`, `/etablissements`, `/scan/*`, `/paiements`, `/passages`) et voir les données de **tous** les élèves, pas seulement les siens — une fuite de données personnelles inacceptable dans un contexte multi-familles.

---

## Décision

**Appliquer la restriction côté serveur, pas uniquement côté interface :**

1. **Fonctionnalités entièrement bloquées pour PARENT** : `EleveController` (GET liste + détail), `EtablissementController` (GET liste + détail + classes + niveaux), `ScanController.scanner()` et `.cache()` — via `@PreAuthorize("!hasRole('PARENT')")`.
2. **Fonctionnalités restreintes au périmètre du parent** : `PaiementService` (`lister`, `getById`, `initierPaiement`) et `ScanService.listerPassages` résolvent les enfants du parent via `ParentRepository.findEnfantIdsByUtilisateurId()` et filtrent/rejettent en conséquence :
   - Liste : filtrage `IN (enfantIds)`.
   - Accès à un élève hors périmètre : `AccessDeniedException` (403) sur une action (initier un paiement), `EntityNotFoundException` (404) sur une lecture (éviter de confirmer l'existence d'une ressource appartenant à un tiers).
3. **Côté frontend** (défense en profondeur, pas la protection principale) : éléments de menu masqués (`MainLayout` filtre par rôle), nouveau composant `StaffRoute` redirigeant vers `/dashboard` si un PARENT navigue directement vers une URL protégée.

---

## Options Envisagées

- **Filtrage uniquement côté frontend** (masquer les menus) — rejeté : un parent techniquement averti peut appeler l'API directement ; aucune garantie de sécurité réelle.
- **Rôle PARENT avec accès complet en lecture seule** — rejeté : ne répond pas à l'exigence de confidentialité entre familles.
- **Table de permissions dynamique (ACL)** — rejeté : sur-ingénierie pour 4 rôles fixes et un seul cas de restriction par propriété (enfants du parent) ; `@PreAuthorize` + filtrage repository suffit et reste lisible.

---

## Conséquences

### ✅ Positives
- Aucune fuite de données inter-familles possible même via appel API direct (vérifié manuellement : `/eleves`, `/etablissements`, `/scan/cache` → 403 ; paiement/passage d'un autre enfant → 403/404).
- Pattern réutilisable : `ParentRepository.findEnfantIdsByUtilisateurId()` centralise la résolution du périmètre, appelable depuis n'importe quel service qui doit restreindre un PARENT.

### ⚠️ Négatives / Compromis acceptés
- Chaque nouvelle fonctionnalité consommée potentiellement par un PARENT doit explicitement penser à cette restriction — pas de garde-fou automatique au niveau framework. À vérifier systématiquement en revue de code.
- Duplique légèrement la logique de filtrage entre `PaiementService` et `ScanService` (deux implémentations similaires plutôt qu'une abstraction commune) — jugé acceptable vu le nombre restreint de cas (2) à ce stade.

---
## Suivi et Validation
- [x] Code mis à jour selon l'ADR (`PaiementService`, `ScanService`, `EleveController`, `EtablissementController`, `ScanController`, `StaffRoute.jsx`, `MainLayout.jsx`).
- [x] Vérification manuelle bout-en-bout via API réelle (compte PARENT de test).
- [x] `history-log.md` mis à jour.
