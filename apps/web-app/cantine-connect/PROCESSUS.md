# Processus de demarrage

## Étape 1 : Initialisation de l'environnement Claude Code & Git
Avant toute écriture de code, vous devez initialiser votre dépôt et structurer l'espace de travail pour que Claude Code puisse y collaborer efficacement.

1. Initialiser le dépôt Git principal
git init cantine-connect
cd cantine-connect

2. Créer les répertoires pour isoler le Front et le Back
mkdir client-frontend server-backend

3. Créer une branche dédiée à l'initialisation du projet (ADR 001)
git checkout -b feat/initial-architecture-setup

## Étape 2 : Initialisation du Front-end (React + Vite + MUI)
Exécutez ces commandes pour configurer l'environnement d'affichage optimisé à onglets requis pour l'application.

1. Naviguer dans le dossier client et initialiser le projet avec Vite (Template JavaScript)
cd client-frontend
npm create vite@latest . -- --template react

2. Installer les dépendances UI essentielles (Material UI, Icons, Axios pour les API REST)
npm install @mui/material @emotion/react @emotion/styled @mui/icons-material axios react-router-dom

3. Valider le démarrage du serveur de développement local (Vérification de l'environnement)
npm run dev

Une fois le serveur démarré sur http://localhost:5173, coupez le processus (Ctrl+C) pour laisser Claude Code générer l'arborescence des vues découpées dans docs/ARCHITECTURE.md.


## Étape 3 : Initialisation du Back-end (Spring Boot)
Vous pouvez générer la structure de base via start.spring.io ou utiliser Claude Code directement pour créer votre build Gradle/Maven avec les dépendances spécifiées (Spring Web, Spring Data JPA, Driver PostgreSQL/MySQL, Spring AOP).

Pour vérifier que l'environnement Java/Spring local est opérationnel, exécutez la commande suivante à la racine de votre dossier backend :

1. cd ../server-backend

2. Tester la compilation initiale et le packaging
./mvnw clean package -DskipTests

## Étape 4 : Lancement du processus de génération par Claude Code
Une fois les structures de projets initialisées, ouvrez Claude Code à la racine globale de votre projet pour lui déléguer le build itératif des premières fonctionnalités de notre MVP (Module 1 : Établissements et Formulaire Élève).

1. Dans votre terminal, lancez l'assistant :
claude

Une fois dans l'interface de Claude Code, passez-lui des instructions claires et cliniques en vous référant aux fichiers présents dans le dossier docs/ :

### Intention de prompt 1 (Génération de la structure de fichiers) :
"Prends connaissance de docs/CLAUDE.md et docs/ARCHITECTURE.md. Génère l'arborescence complète des dossiers pour l'application React sous client-frontend/src/ (components, views, hooks, services, context) et pour Spring Boot sous server-backend/src/main/java/com/cantine/connect/."

### Intention de prompt 2 (Build du premier composant contraint) :
"Réfère-toi à docs/SPECIFICATION.md et à l'ADR 002 de docs/DECISION-LOG.md. Génère le composant UI StudentForm.jsx sous client-frontend/src/views/students/. Ce formulaire doit obligatoirement utiliser un composant à onglets (@mui/material/Tabs) pour séparer les volets : 1. Informations Générales, 2. Affectation/Cantine, 3. Contacts Parents. Assure-toi qu'il n'y ait aucun scroll vertical pour optimiser l'affichage."

### Intention de prompt 3 (Build de la traçabilité AOP Backend) :
"Réfère-toi à docs/SPECIFICATION.md et à l'ADR 003 de docs/DECISION-LOG.md. Crée l'entité ActionLog ainsi que l'aspect Spring AOP (@Aspect) associé sous server-backend/src/main/java/com/cantine/connect/aspect/ActionLogAspect.java permettant de logger en base de données chaque appel de création, modification ou suppression (CUD) provenant des contrôleurs."

## Étape 5 : Procédure de Push et de Déploiement Continu (Vercel)
Dès que Claude Code a finalisé et testé localement une sous-fonctionnalité, appliquez la procédure décrite dans docs/WORKFLOW.md :

1. Vérifier l'état des fichiers générés
git status

2. Indexer et commiter les fichiers selon la convention (CLAUDE.md)
git add .
git commit -m "feat: implement student tabs form UI and backend ActionLog aspect"

3. Pousser la branche vers votre dépôt de code GitHub distant
git push origin feat/initial-architecture-setup


## Déploiement Continu & Test par le client :

1. Allez sur votre tableau de bord Vercel, liez votre projet GitHub et sélectionnez le sous-dossier client-frontend.

2. Vercel va automatiquement intercepter votre git push et générer une Preview URL pour cette branche de fonctionnalité.

3. Transmettez cette URL directement à la direction (M. Sylla) pour validation en direct sur écran ou smartphone avant de fusionner la Pull Request dans la branche main.