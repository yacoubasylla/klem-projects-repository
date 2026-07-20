Nous allons configurer un pipeline CI/CD (Intégration et Déploiement Continus) automatisé avec GitHub Actions et Docker.

# Phase 1 : L'Environnement Local (Le Starter Pack)
Pour que vos deux développeurs travaillent exactement dans les mêmes conditions, tout est conteneurisé. À la racine du monorepo, créez les fichiers suivants :

# 1. docker-compose.yml (La Base de Données Locale)
Ce fichier permet de lancer PostgreSQL en une seule commande sans installation locale polluante.

YAML
version: '3.8'

services:
  postgres-db:
    image: postgres:15-alpine
    container_name: fleetcontrol-db-local
    environment:
      POSTGRES_USER: klem_admin
      POSTGRES_PASSWORD: klem_secure_password
      POSTGRES_DB: fleetcontrol_db
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - fleet-network

volumes:
  postgres_data:

networks:
  fleet-network:
    driver: bridge

# 2. .gitignore (À la racine)
Pour éviter de pousser des fichiers temporaires ou des secrets sur GitHub.

Plaintext
# Node / Frontend
node_modules/
.dist/
.env
.env.local

# Java / Backend
target/
.gradle/
build/
*.class

# IDEs
.idea/
.vscode/
*.suo
*.ntvs*

# OS
.DS_Store
Thumbs.db

# Phase 2 : Les Garde-Fous GitHub (Collaboration)
Avec deux développeurs, la branche develop est votre mine d'or. Personne ne doit casser cette branche.

# 1. Règle de protection de branche (À configurer sur GitHub)
Allez dans Settings > Branches sur votre repo GitHub et ajoutez une règle sur develop et main :

Require a pull request before merging : Coché (1 approbation requise).

Require status checks to pass before merging : Coché (le build CI doit être vert).

Require conversation resolution before merging : Coché (tous les commentaires d'évaluation doivent être résolus).

# 2. Le Template de Pull Request
Créez un fichier .github/PULL_REQUEST_TEMPLATE.md pour forcer les développeurs à documenter ce qu'ils poussent.

Markdown
## Description
*Quels changements apporte cette PR ? Quel module est impacté (Location, Course, Admin, API) ?*

## Type de changement
- [ ] Mises à jour de la base de données (Scripts SQL inclus)
- [ ] Nouvelle fonctionnalité frontend / backend
- [ ] Correction de bug (Hotfix/Bugfix)
- [ ] Refactoring ou documentation

## Checklist de validation
- [ ] Mon code respecte le typage strict (TypeScript / Java 17).
- [ ] J'ai lancé le build localement et il passe sans erreur.
- [ ] Les composants graphiques MUI respectent le layout responsive (pas de chevauchement).
- [ ] J'ai mis à jour le fichier `history-log.md` ou exécuté la commande `/update`.
Phase 3 : L'Intégration Continue (CI) avec GitHub Actions
Ce pipeline va s'exécuter automatiquement à chaque fois qu'un développeur ouvrira ou mettra à jour une Pull Request vers la branche develop. Si le code ne compile pas ou si les tests échouent, le bouton "Merge" est bloqué.

Créez le fichier .github/workflows/ci.yml :

YAML
name: FleetControl CI Pipeline

on:
  pull_request:
    branches: [ develop, main ]
  push:
    branches: [ develop ]

jobs:
  # 1. Validation du Backend Spring Boot
  backend-build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout du code
        uses: actions/checkout@v3

      - name: Configuration de Java 17
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'maven' # ou 'gradle' selon votre choix final

      - name: Build et Tests Unitaires Spring Boot
        run: |
          cd apps/backend-api
          ./mvnw clean test package

  # 2. Validation du Frontend React
  frontend-build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout du code
        uses: actions/checkout@v3

      - name: Configuration de Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'

      - name: Installation et Build React
        run: |
          cd apps/web-app
          npm install
          npm run build
Phase 4 : Le Déploiement Continu (CD)
Pour une structure comme Klem Technologies & Services, l'approche la plus agile et économique pour le MVP consiste à utiliser un VPS Linux (Ubuntu) configuré avec Docker. Le pipeline GitHub Actions va empaqueter l'application et la déployer via SSH.

Le flux automatisé du Déploiement (lors du merge sur main ou develop) :
Dockerisation : GitHub Actions construit deux images Docker isolées (une pour l'API Spring Boot, une pour le front React servi par Nginx).

Registre : Les images sont poussées de manière sécurisée sur le registre privé de votre projet (GitHub Packages - GHCR).

Déclenchement SSH : GitHub Actions se connecte en SSH à votre serveur de staging/production et exécute un script :

Bash
docker compose pull
docker compose up -d --remove-orphans
Votre Routine d'Équipe au jour le jour
Pour résumer visuellement, voici comment vos deux développeurs travaillent désormais :

Plaintext
[Dev 1 ou Dev 2 en Local]
       │
       ├──> Lance `docker-compose up` (Postgres local)
       ├──> Tape `claude "/startup"` (Alignement de l'IA)
       ├──> Code sa feature (React / Spring Boot)
       ├──> Tape `claude "/update"` (Mise à jour des logs de bord)
       │
[Push vers GitHub]
       │
       ├──> Ouverture d'une Pull Request (PR)
       ├──> Le pipeline CI démarre (Test automatique du code)
       ├──> L'autre Développeur relit, commente et valide la PR
       │
[Merge sur Develop / Main]
       │
       └──> Le pipeline CD se déclenche ──> Déploiement auto sur votre serveur