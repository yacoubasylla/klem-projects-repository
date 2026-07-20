# ADR 001 : Adoption du Monorepo pour KLEM Technologies & Services

- **Date :** 2026-06-18
- **Statut :** Accepté
- **Auteurs :** [Votre Nom], CEO KLEM Technologies

## Contexte
KLEM Technologies développe plusieurs projets simultanément (Gestion de parc, Data Plateforme, apps Web/Mobile). La duplication de code UI et la gestion complexe des dépendances entre les projets commençaient à ralentir notre vélocité.

## Décision
Adoption d'un Monorepo utilisant `pnpm workspaces` et `Turborepo`.
- Les applications sont isolées dans `apps/`.
- Les briques réutilisables (UI, Utils) sont centralisées dans `packages/`.
- La connaissance technique est portée par des fichiers `CLAUDE.md` locaux et globaux.

## Alternatives envisagées
- **Gestion par repos séparés (Multi-repo) :** Rejeté car trop complexe à maintenir en termes de versions et de mise à jour de composants transversaux.
- **Micro-services sans monorepo :** Rejeté pour éviter la surcharge de configuration CI/CD par projet.

## Conséquences
- **Avantages :** 
    - Code source unique pour tous les projets.
    - Partage de composants UI cohérent (design system KLEM).
    - Mise à jour facilitée des dépendances (React, TypeScript).
- **Risques :**
    - Nécessite une discipline rigoureuse sur la structure des `package.json` et des `ADR`.
    - L'équipe doit apprendre à utiliser `workspace:*` pour les imports locaux.