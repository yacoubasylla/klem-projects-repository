# Methodologie de travail à suivre

Pour propulser FleetControl de la théorie au code sans que l'IA ne se perde en cours de route, nous allons appliquer une méthodologie Feature-Driven AI Development (FDAID).

Le secret de la réussite tient en une règle d'or : Interdiction de coder deux fonctionnalités en même temps. On ne passe à la ligne suivante que lorsque la précédente est testée, validée et journalisée.

## La Méthodologie : L'Attaque par Composant Isolé
Pour chaque sous-fonctionnalité (ex: Créer un véhicule), Claude Code doit suivre un cycle de développement en "V" ultra-court et localisé :

- DB & Migration : Écriture du script Flyway/Liquibase + validation du schéma PostgreSQL.

- Backend Core : Entité JPA ➔ Repository ➔ Service Métier (@Transactional) ➔ DTO & Validations.

- API Exposed : Contrôleur REST + Gestion des exceptions.

- Frontend Hook : Types TypeScript stricts + Custom Hook Axios/React Query (Zéro logique dans l'UI).

- IHM Premium : Layout MUI fluide, responsive, sans chevauchement.

- Clôture : Mise à jour automatique de history-log.md.

## 📋 La Feuille de Route : Séquencement des Phases
Voici l'ordre logique de déploiement pour maximiser la vélocité de l'équipe :

### Phase 0 : Les Fondations (Jour 1)

Mise en place de la sécurité (Cookies HttpOnly, JWT, Spring Security).

Création des composants UI globaux partagés dans packages/ui (<KlemTable />, <KlemButton />).

### Phase 1 : Le Cœur Opérationnel (Module 1 : Véhicules)

CRUD des véhicules, machine d'états, alertes d'expiration administrative.

### Phase 2 : Le Moteur de Revenus (Module 2 : Chauffeurs & Courses)

Profils chauffeurs, dispatch, immutabilité des gains à la clôture (calcul de commission).

### Phase 3 : Diversification Métier (Module 4 : Locations & Module 3 : Livraisons)

Contrats, états des lieux (Checkout/Checkin), gestion des colis et preuves de livraison.

### Phase 4 : Robustesse & Finances (Module 5 : Maintenance & Module 6 : Paie)

Scheduler de vidanges (background tasks), génération de la paie nette mensuelle et tableaux de bord.

## Les Prompts Maîtres pour Piloter Claude Code
Voici les prompts exacts que vous devez copier-coller à Claude au début et pendant le développement de chaque tâche.

### Prompt 1 : L'Initialisation de la Tâche (À lancer en premier)
DÉVELOPPEUR : > ```text
**/startup**

Nous démarrons l'implémentation de la première brique de la Phase 1 : "Création de la table PostgreSQL et de l'entité Java pour la gestion des Véhicules".

Base-toi strictement sur les fichiers claude-collaboration/docs/specifications.md (Module 1) et CLAUDE.md pour les standards de code. Ne génère aucun code pour l'instant. Propose-moi d'abord le plan technique détaillé des fichiers que tu t'apprêtes à créer ou modifier pour validation.

### Prompt 2 : Le Codage de la Couche Backend (Une fois le plan validé)
DÉVELOPPEUR : > ```text
            Le plan est validé. Écris maintenant le code de la couche Backend pour cette tâche.

            Rappels stricts de nos standards (CLAUDE.md) :

            Génère le script de migration SQL incrémental pour PostgreSQL.

            Crée l'entité Java 17 avec validations Spring Boot (@NotNull, @Size, etc.).

            Utilise @Transactional(readOnly = true) sur les lectures dans le Service.

            Assure-toi qu'aucune logique métier ne se trouve dans le Controller.

            Gère les exceptions via notre handler global.

            Lance le build global avec pnpm run build pour vérifier que tout compile parfaitement avant de t'arrêter.

### Prompt 3 : Le Codage de la Couche Frontend (Une fois le backend OK)
DÉVELOPPEUR : > ```text
            Le backend est opérationnel et compile. Passe maintenant à l'implémentation de la couche Frontend pour cette même fonctionnalité.

            Rappels stricts de nos standards (CLAUDE.md) :

            Crée les interfaces TypeScript strictes correspondantes (Interdiction d'utiliser 'any').

            Isole la logique d'appel API dans un Hook personnalisé (Custom Hook).

            Pour l'IHM, utilise Material UI avec un layout responsive fluide (Grid/Stack).

            La vue doit s'intégrer proprement dans le layout principal (le Drawer persistant doit décaler le contenu sans chevauchement).

### Prompt 4 : La Clôture de Session et Journalisation
DÉVELOPPEUR : > ```text
            La fonctionnalité est entièrement codée et fonctionnelle.

            Exécute les commandes de linting et de formatage spécifiées dans CLAUDE.md. Ensuite, mets à jour le fichier claude-collaboration/history/history-log.md en y ajoutant un nouveau bloc chronologique détaillant la tâche livrée, les fichiers modifiés et le statut actuel du workspace. Formate ton message de commit selon la convention Conventional Commits.

Par quel module ou fonctionnalité spécifique de la Phase 0 ou Phase 1 souhaitez-vous que nous préparions le premier sprint de codage ?