# Workspace History

> Journal chronologique de toutes les sessions et décisions importantes.
> Le plus récent en haut. Mis à jour automatiquement par Claude.
>
> **Comment ça marche :** Quand je lance la commande `/update` après une session importante, ou quand je raconte un changement significatif, Claude ajoute une entrée ici automatiquement. Je n'ai pas à écrire ce fichier manuellement.

---

[VIDE INITIALEMENT - SE REMPLIRA AU FIL DES SESSIONS]


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