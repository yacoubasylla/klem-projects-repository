# Guide d'installation et de démarrage : KLEM-REPO

Bienvenue dans l'espace de travail central de KLEM Technologies. Ce monorepo contient l'ensemble de nos briques logicielles, allant des applications métiers aux outils de traitement de données (Big Data).

## Structure du Workspace 

Ce monorepo est orchestré par **Turborepo** et structuré pour isoler strictement les applications clientes, la logique métier partagée, la documentation d'architecture et les configurations d'automatisation pour les agents IA.

### Arborescence du Répertoire

KLEM-REPO/
├── .github/                     # Workflows d'intégration et de déploiement continus (CI/CD)
├── .claude/                     # Configuration avancée et extensions pour Claude Code
│   ├── commands/                # Macros et scripts de commandes personnalisées
│   │   ├── startup.md           # Commande /startup : Initialisation et alignement du contexte
│   │   ├── update.md            # Commande /update : Resynchronisation du contexte en cours de session
│   │   └── install.md           # Commande /morning : Routine de démarrage de journée des développeurs
│   ├── agents/                  # Macros et scripts de commandes personnalisées
│   │   └── security-scanner.md  # Agent /secu : Initialisation et alignement du contexte
│   │   
│   ├── skills/                  # Capacités et outils spécifiques exécutés par l'IA
│   │    └── recherche-actualites/# Outil autonome de veille technologique et légale
│   ├── hooks/                   # lifecycle hook scripts
│   │   └── security-check.sh
│   │   
│   │   
│   └── settings.json            # project permissions
├── apps/                        # Applications autonomes et déployables (Exécutables)
│   ├── backend-api/             # API Core Spring Boot 3.x & Logique métier du parc automobile
│   ├── mobile-app/              # Application mobile multiplateforme (Chauffeurs / Livreurs)
│   ├── showcase-website/        # Site vitrine et infographies de KLEM Technologies
│   └── web-app/                 # Dashboard d'administration Enterprise ERP (React / MUI)
├── collaboration/               # Base de connaissances projet partagée humain/IA
│   ├── context/
│   │   └── CONTEXT.md           # Vision produit, alignement métier et règles globales KLEM
│   ├── doc/
│   │   ├── architectures.md     # Topologie des nœuds, sécurité des flux et infrastructure prod
│   │   ├── specifications.md    # Schémas de base de données PostgreSQL et spécifications API
│   │   └── workflows.md         # Diagrammes d'états et cycles de vie opérationnels du parc
│   └── history/
│       ├── decision-log.md      # Registre historique des arbitrages stratégiques
│       ├── history-log.md       # Journal chronologique des livraisons et des tâches accomplies
│       └── adr/                 # Architecture Decision Records (Format standardisé)
│           ├── 2026-06-18-adoption-monorepo.md
│           └── 2026-06-19-choix-strategie-securite.md
├── knowledges/                  # Renseignements externes et documentation brute
│   ├── output/                  # Rapports générés, logs consolidés et données nettoyées
│   ├── raw/                     # Documents bruts non structurés (PDFs, exports comptables, décrets)
│   └── wiki/                    # Base de connaissances interne et procédures de l'entreprise
├── packages/                    # Modules partagés et réutilisables au sein du monorepo
│   ├── config/                  # Configurations transverses partagées (ESLint, TSConfig, Prettier)
│   ├── ui/                      # Design System KLEM (Composants MUI packagés : KlemTable, KlemButton)
│   └── utils/                   # Librairies de fonctions utilitaires, validateurs et helpers communs
├── scripts/                     # Scripts d'automatisation de l'infrastructure locale
│   ├── README.md                # Guide d'utilisation des outils en ligne de commande
│   └── create-adr.sh            # Script Bash de génération automatisée de fichier ADR
├── templates/                   # Squelettes de code et modèles de fichiers standardisés
├── .cursorrules                 # Instructions de contextualisation pour l'éditeur Cursor
├── .turbo.json                  # Configuration du cache et des pipelines de build Turborepo
├── CLAUDE.md                    # Guide de survie et règles de développement strictes pour l'IA
└── package.json                 # Racine de configuration des workspaces pnpm


### Matrice d'Utilité des Dossiers

| **`Composant / Dossier`**  | **`Rôle Fonctionnel & Technique`**  |
| :--- | :--- |
| **`apps/web-app/`** | Application web de gestion d'entreprise (ERP/Back-office). Permet aux gestionnaires de flottes de suivre le parc à l'aide d'une interface desktop MUI riche. |
| **`apps/backend-api/`** | Cœur du système. Centralise la logique métier, l'accès sécurisé à PostgreSQL, l'authentification par cookies étanches, et l'intégration avec le nœud IA `ai.koog`. |
| **`apps/mobile-app/`** | Interface terrain épurée "Mobile-First" dédiée aux chauffeurs et livreurs pour la saisie des états des lieux, des incidents, et le suivi du portefeuille. |
| **`apps/showcase-website/`** | Site internet institutionnel de KLEM Technologies servant de vitrine commerciale et de support marketing. |
| **`collaboration/context/`** | Fichiers d'alignement stratégique. Contient la vision de l'entreprise et la culture métier indispensables à la bonne compréhension globale de l'IA. |
| **`collaboration/doc/`** | Documentation technique et fonctionnelle d'ingénierie (Spécifications de tables, contrats d'API, architecture réseau). |
| **`collaboration/history/`** | Traçabilité de l'évolution du projet. Stocke l'historique des modifications, les commits de clôture de tâches, et les dossiers d'arbitrage d'architecture (ADR). |
| **`knowledges/`** | Espace de stockage des intrants externes bruts (lois de finances, documentations d'API externes) à indexer et analyser pour enrichir le système. |
| **`packages/`** | Bibliothèque interne au monorepo permettant de mutualiser le code (le design system UI partagé, les types stricts TypeScript et les configurations de build). |
| **`scripts/`** | Outils de commodité pour les développeurs (génération de squelettes, automatisation de tâches Docker, sauvegardes DB). |
| **`templates/`** | Modèles de structures de fichiers imposés pour conserver une uniformité parfaite lors de l'ajout de nouvelles fonctionnalités. |
| **`.claude/`** | Couche logicielle d'extension de Claude Code (commandes de session `/startup`, `/morning`, outils d'automatisation spécifiques au projet). |


### Les 4 fichiers clés à comprendre

- **`README.md`**
  **Le guide d'accueil des humains** : C'est le point d'entrée technique du projet pour les développeurs. Il explique comment cloner le monorepo, installer les dépendances (via pnpm), lancer le serveur de développement local avec Turborepo, et comprendre l'organisation globale de l'espace de travail.

- **`CLAUDE.md`**
  **L'âme et le guide de survie de l'IA** : Ce fichier est lu automatiquement par l'agent Claude Code à l'ouverture de chaque session de travail. Il contient les instructions de formatage de code, les commandes système autorisées, les normes de développement strictes de l'entreprise (ex: typage strict TypeScript, architecture Spring Boot) et la routine de validation avant de livrer une tâche.

- **`/collaboration/context/CONTEXT.md`**
  **La carte d'identité métier de l'entreprise** : Ce document centralise la vision stratégique de KLEM Technologies & Services. Il décrit la culture métier, les objectifs des applications (comme l'ERP de gestion de flotte *FleetControl*), l'écosystème ciblé et les contraintes opérationnelles locales. Plus ce fichier est précis, plus les réponses de l'IA sont pertinentes face à vos enjeux réels.

- **`/collaboration/history/HISTORY.md`** (ou `history-log.md`)
  **Le journal de bord vivant du projet** : C'est la mémoire à long terme des sessions de développement. Il recense de manière chronologique toutes les avancées majeures, les tâches clôturées et l'état actuel du workspace. Complété rigoureusement à la fin de chaque sprint, il permet à l'IA de reprendre le travail exactement là où la session précédente s'était arrêtée, sans perte de contexte.


## Démarrage Rapide (Onboarding)
1. **Prérequis :** Installer [pnpm](https://pnpm.io/) (gestionnaire de paquets).
Avant de commencer, assurez-vous d'avoir les outils suivants :

- Node.js : Version 18.0.0 ou supérieure.
- pnpm : Notre gestionnaire de paquets haute performance.
- Installation : npm install -g pnpm
- VS Code : Recommandé avec les extensions ESLint et Prettier

2. **Installer toutes les dépendances du monorepo :**
   Bash 
      pnpm install

3. **Vérifier que tout est correctement lié :**
  Bash
      pnpm turbo run build
    Si cette commande se termine sans erreur, votre environnement est parfaitement configuré.

4. **Guide de développement quotidien :**
     **A. Lancer les projets**
      Pour démarrer tous vos projets simultanément en mode développement :

      Bash
      pnpm dev

     **B. Ajouter une nouvelle fonctionnalité (Workflow)**
      Si vous développez une nouvelle brique :

      Créer un ADR : Avant de coder, documentez votre choix.

      Bash
      ./scripts/create-adr.sh <nom-de-votre-feature>
      Créer votre package/app : Utilisez les templates situés dans /templates/.

Lier les dépendances : Si vous ajoutez un nouveau package, assurez-vous qu'il est bien déclaré dans package.json de votre application avec "nom-du-package": "workspace:*".


## Qualité et Standards de Développement
- Le .cursorrules : Ce fichier à la racine guide notre IA. Si vous utilisez Cursor ou Claude Code, il est automatiquement pris en compte pour respecter les standards KLEM.
- Monorepo : Nous utilisons Turborepo. Chaque nouvelle fonctionnalité doit privilégier la réutilisation de code via packages/.
- Gouvernance : Toute modification majeure de l'architecture doit être documentée par un ADR. Utilisez ./scripts/create-adr.sh <nom-du-sujet> pour créer un nouveau document.
- Pipeline CI/CD : À chaque git push, le système vérifie :
      La conformité du code (Linting).
      La présence d'un fichier ADR pour toute nouvelle structure.
      Rejette automatiquement tout code ne respectant pas les règles de linting ou sans ADR.
- Commande de vérification locale : Avant de soumettre votre travail, lancez ceci pour éviter les erreurs sur le serveur :
    Bash
    pnpm turbo run lint build


## Collaboration
- IA Assistant : Ce projet est optimisé pour être utilisé avec Claude Code et VS Code. Veuillez respecter les directives définies dans le fichier .cursorrules à la racine.
- Workflow : feature/* -> develop -> main.

## Bonnes pratiques

- **Démarrez chaque session avec `/startup`** pour que votre Jarvis charge votre contexte complet
- **Soyez honnête dans vos réponses** lors de l'installation initiale
- **Mettez à jour régulièrement** vos fichiers quand votre situation évolue
- **Utilisez le dossier `knowledges/raw/`** pour déposer des documents externes que Claude doit analyser (PDFs, exports, etc.)
- **Validez toujours** les modifications que Claude propose sur vos fichiers

## Projets Actifs
- Gestion de Parc Auto
- Gestion de cantine scolaire
- Gestion de Pharmacie
- Data Plateforme Spark-Kafka


## 📌 En Résumé : L'Essentiel pour Démarrer

Pour l'équipe de développement et les assistants IA, voici ce qu'il faut retenir en un coup d'œil :

*   **L'Écosystème** : Un monorepo unique (propulsé par **Turborepo** et géré via **pnpm**) qui centralise toutes les applications de KLEM (Backend Spring Boot, Web React/MUI, Mobile, et pipelines Big Data Spark/Kafka).
*   **La Règle d'Or Technique** : Typage stricts **TypeScript** côté frontend, architecture découplée, et **zéro modification d'architecture sans document ADR** (généré via `./scripts/create-adr.sh`).
*   **Le Dual Humain-IA** : Un espace de travail optimisé pour la collaboration avec **Claude Code** et **Cursor**. L'IA s'aligne sur vos standards grâce au duo `CLAUDE.md` / `CONTEXT.md` et documente ses livraisons dans `history-log.md`.
*   **Démarrage Éclair** : 

    pnpm install && pnpm dev
    
*   **Objectif Final** : Chaque brique de code produite ici doit servir notre mission : bâtir des solutions robustes, souveraines et prêtes pour le terrain afin de moderniser les infrastructures publiques et privées en Afrique.

## Pour toute question, contactez le Lead Développeur ou consultez le CLAUDE.md à la racine.

### Contact technique
- Lead Développeur: Yacouba SYLLZ
- email/WhatsApp: ciyasyl@gmail/+225 0554025100