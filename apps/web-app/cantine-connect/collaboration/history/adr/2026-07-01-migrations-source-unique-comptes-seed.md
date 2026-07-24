# ADR-012 : Migrations Flyway comme Source Unique des Comptes de Seed (Suppression de DataInitializer)

**Date :** 2026-07-01
**Statut :** Accepté
**Décideur :** Yacouba SYLLA

---

## Contexte

Depuis le début du projet, un `DataInitializer` (`ApplicationRunner` Spring Boot) créait un compte ADMIN par défaut au premier démarrage, si `utilisateurRepository.count() == 0`. Ce mécanisme fonctionnait tant qu'aucune donnée de seed structurée n'existait ailleurs.

Deux évolutions ont rendu ce mécanisme obsolète et potentiellement trompeur :
1. Le champ `telephone` est devenu obligatoire et unique (V5) — `DataInitializer` ne le renseignait pas, ce qui aurait fait échouer la création sur une base neuve.
2. Une demande de réinitialisation des comptes (un compte de référence par rôle : ADMIN, GESTIONNAIRE, CAISSIER, PARENT) a nécessité une migration Flyway (V6) qui vide et repeuple `utilisateurs` de façon déterministe, y compris en production (Railway).

Une fois V6 appliquée, `utilisateurRepository.count()` ne vaut plus jamais 0 sur aucun environnement traversé par les migrations Flyway (toujours activées, `flyway.enabled: true` sans condition d'environnement) — le garde-fou de `DataInitializer` ne se déclenche donc plus jamais, et son mot de passe câblé en dur (`Admin123!`) devient incohérent avec celui réellement en base (`admin@123`).

---

## Décision

**Supprimer `DataInitializer.java`.** Les comptes de seed (un par rôle, identifiants documentés dans `manuel-utilisateur.md`) sont désormais définis **exclusivement** par la migration Flyway `V6__reset_comptes_un_par_role.sql`, qui constitue la seule source de vérité pour l'état initial de la table `utilisateurs`.

---

## Options Envisagées

- **Mettre à jour `DataInitializer` avec les nouvelles valeurs** (mot de passe, téléphone) — rejeté : deux mécanismes de seed (ApplicationRunner + migration SQL) créeraient un risque de divergence silencieuse si l'un est modifié sans l'autre ; le `count()==0` ne se déclenchant plus jamais de toute façon, le code serait mort dès sa mise à jour.
- **Conserver `DataInitializer` comme filet de sécurité si Flyway est désactivé** — rejeté : `flyway.enabled` n'est jamais conditionnel dans ce projet (aucun profil ne le désactive) ; maintenir du code pour un scénario qui n'existe pas dans la configuration actuelle ajoute de la confusion sans bénéfice réel.

---

## Conséquences

### ✅ Positives
- Source unique de vérité pour les comptes de seed : toute relecture future du schéma initial se fait dans `db/migration/`, pas dans deux endroits distincts.
- La réinitialisation des comptes est reproductible sur n'importe quel environnement (local, Railway) simplement en laissant Flyway s'exécuter au démarrage — pas d'étape manuelle.

### ⚠️ Négatives / Compromis acceptés
- **Action destructive et irréversible** : la migration V6 supprime tous les comptes utilisateurs existants (et les liaisons `parents`/`parents_eleves` associées) au profit des 4 comptes de référence. Confirmé explicitement par le décideur avant application, y compris en production.
- Toute création manuelle de compte via l'application après le déploiement de V6 reste possible normalement (V6 ne s'exécute qu'une fois, comme toute migration Flyway) — ce n'est pas un mécanisme de réinitialisation permanent.

---
## Suivi et Validation
- [x] `DataInitializer.java` supprimé.
- [x] Migration V6 appliquée et vérifiée en local (connexion réussie pour les 4 comptes).
- [x] `history-log.md` mis à jour.
- [x] Utilisateur informé du caractère irréversible avant le push déclenchant le déploiement production.
