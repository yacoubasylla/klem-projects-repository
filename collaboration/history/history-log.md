# Workspace History

> Journal chronologique de toutes les sessions et décisions importantes.
> Le plus récent en haut. Mis à jour automatiquement par Claude.
>
> **Comment ça marche :** Quand je lance la commande `/update` après une session importante, ou quand je raconte un changement significatif, Claude ajoute une entrée ici automatiquement. Je n'ai pas à écrire ce fichier manuellement.

---

[VIDE INITIALEMENT - SE REMPLIRA AU FIL DES SESSIONS]

### [2026-07-24] - Unification workspace KLEM + scaffold services/infrastructure/docs + pont Labs->Projects
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés :** `services/README.md`, `infrastructure/{docker,terraform,kubernetes}/README.md`,
  `docs/README.md`, `pnpm-workspace.yaml`, `README.md`, `collaboration/history/adr/2026-07-24-scaffold-services-infra-docs-et-pont-labs-projects.md`
  (ce dépôt) ; `klem-labs-repository/platform-devsecops/scripts/klem_promote.py`,
  `klem-labs-repository/bin/klem-promote`, `klem-labs-repository/GLOBAL_README.md` (dépôt sibling).
- **Description :** Les deux dépôts `klem-labs-repository` et `klem-projects-repository` sont
  unifiés sous `~/Documents/klem-enterprise-workspace/` (dossier plat, historique et remotes
  git intacts, vérifié par diff de `git status`/`git log` avant/après). Ajout de `services/`,
  `infrastructure/`, `docs/` en scaffolding (aucun code existant déplacé -- voir l'ADR associé pour
  le raisonnement). Ajout de `klem_promote.py` : passerelle Labs -> Projects qui réutilise la
  détection de statut de `generate_dashboard_data.py` et ne promeut un projet que si la règle de
  gouvernance n°7 est satisfaite.

## [2026-06-21] - Implémentation Sécurité & Authentification

*   **Type** : 🚀 Feature & Architecture
*   **Pull Request** : [#14](https://github.com/klem-tech/fleetcontrol/pull/14)
*   **ADR Associé** : [ADR #004 : Structure de l'authentification JWT](./claude-collaboration/history/ADR-004-jwt.md)
*   **Développeur(s)** : [Nom du Dev]
*   **Impact Technique (Fichiers modifiés)** :
    *   `packages/database/prisma/schema.prisma` (Ajout des tokens)
    *   `apps/backend/src/main/java/com/klem/fleetcontrol/config/SecurityConfig.java`
    *   `apps/backend/src/main/java/com/klem/fleetcontrol/auth/JwtFilter.java`
    *   `apps/frontend/src/hooks/useAuth.ts` (Gestion du cycle de vie du token)

> **Note de session** : Implémentation validée conformément aux exigences de l'ADR #004. Les tokens sont stockés dans des cookies HttpOnly pour parer les failles XSS. Le rôle `CHAUFFEUR` est bridé aux endpoints de courses et livraisons.