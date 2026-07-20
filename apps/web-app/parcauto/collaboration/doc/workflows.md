# Workflows et Standards Opérationnels - Klem Technologies & Services

Ce document décrit les flux de travail standardisés pour tous les développeurs intervenant sur le monorepo. Le respect strict de ces procédures garantit la robustesse, la scalabilité et la maintenabilité de nos solutions (vitrines, pipelines data, et applications métiers comme FleetControl v2.0).

---

## 1. Flux de Gestion de Code (Git Flow)

Nous utilisons une approche basée sur le "Feature Branching" pour isoler les développements et faciliter les revues de code.

*   **`main`** : Branche de production. Le code doit y être parfaitement stable. Aucun commit direct n'est autorisé.
*   **`develop`** : Branche d'intégration principale. Reflète l'environnement de pré-production/staging.
*   **Branches de fonctionnalités** : `feat/nom-de-la-feature` (ex: `feat/fleet-tracking-ui`).
*   **Branches de correction** : `bugfix/description-du-bug` ou `hotfix/bug-critique-prod`.

### Convention des Commits
Tous les messages de commit doivent suivre la spécification **Conventional Commits** pour automatiser la génération des changelogs.
*   `feat(admin)` : Ajout d'une nouvelle fonctionnalité sur l'application admin.
*   `fix(api)` : Correction d'un bug sur le backend Spring Boot.
*   `docs(claude)` : Mise à jour des prompts ou de l'historique de l'IA.
*   `refactor(ui)` : Modification du code d'un composant MUI sans changer son comportement.

---

## 2. Flux de Développement Quotidien (Monorepo)

La gestion des dépendances et l'exécution des tâches sont orchestrées par **pnpm** et **Turborepo** pour des performances optimales.

### Initialisation (Nouveau Développeur ou Nouvelle Journée)
1.  Récupérer les dernières modifications : `git pull origin develop`
2.  Installer ou mettre à jour les dépendances à la racine : `pnpm install`
3.  Créer une branche de travail : `git checkout -b feat/ma-nouvelle-fonctionnalite`

### Exécution et Build
Grâce à Turborepo, les commandes sont parallélisées et mises en cache.
*   **Lancer tout l'environnement de dev** : `pnpm turbo run dev`
*   **Lancer un livrable spécifique** : `pnpm turbo run dev --filter=admin`
*   **Construire pour la production** : `pnpm turbo run build`
*   **Vérifier le typage et le formatage** : `pnpm turbo run lint format`

---

## 3. Workflow de Collaboration avec Claude Code

L'intégration de l'intelligence artificielle est au cœur de nos processus pour accélérer la livraison et maintenir des standards architecturaux élevés.

### Règle d'or : Contextualisation
Avant toute génération de code complexe (ex: architecture Big Data, CDC Oracle vers Kafka, ou UI avancée en React), le contexte doit être verrouillé.
*   **En local (Terminal)** : Assurez-vous que le fichier `CLAUDE.md` est à jour. Lancez simplement la commande `claude` à la racine.
*   **Interface Web** : Débutez toujours le prompt par l'inclusion du fichier `commands/startup.md`.

### Processus de Refactoring et de Création
1.  **Génération** : Demandez à Claude de générer le composant ou le service. (ex: *"Génère le service d'ingestion des logs de maintenance pour FleetControl v2.0 dans apps/api"*).
2.  **Audit Humain** : Le développeur vérifie systématiquement le typage strict (TypeScript/Java), la gestion des erreurs, et le respect du design system (Material UI).
3.  **Documentation** : Si une décision architecturale clé est prise (ex: modification du schéma de base de données ou création d'un nouveau pattern de composants partagés), le développeur doit la consigner dans un nouveau fichier daté sous `claude-collaboration/history/`.

---

## 4. Workflow de Déploiement et CI/CD

L'intégration continue est déclenchée automatiquement lors de la création d'une Pull Request (PR) vers `develop` ou `main`.

1.  **Pull Request** : Le développeur pousse sa branche et ouvre une PR.
2.  **Pipeline CI** : 
    *   Exécution du linting (`pnpm run lint`).
    *   Vérification des builds de toutes les applications impactées via le cache Turbo.
    *   Exécution des tests unitaires et d'intégration (Spring Boot & React).
3.  **Code Review** : Au moins un pair doit valider la PR. L'attention est portée sur l'optimisation, la sécurité et la séparation des responsabilités.
4.  **Merge** : Une fois validée, la PR est fusionnée ("Squash and Merge" recommandé pour garder un historique propre).


## 5. Clôture de Session
Avant de pousser le code final, le développeur exécute le prompt `commands/update` pour maintenir à jour les journaux de bord.