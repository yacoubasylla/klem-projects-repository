# Architecture Globale et Infrastructure - FleetControl

## 📌 1. Vision Générale de l'Architecture

L'application **FleetControl** repose sur une architecture découplée de type **3-Tier** enrichie d'un nœud d'intégration IA local. L'ensemble des composants est conteneurisé afin d'assurer une portabilité totale entre les environnements de développement, de staging et de production.

        [ Navigateur Client ] (React / MUI)
                │
                ▼ (HTTPS / Cookies Sécurisés via Nginx Reverse Proxy)
        [ API Gateway / Backend Nœud ] (Spring Boot Core)
                │
                ├──────────────────────────────┐
                ▼ (JDBC / PostgreSQL Driver)    ▼ (Internal REST/gRPC)
        [ Nœud de Données ] (PostgreSQL 16)   [ Nœud IA Local ] (ai.koog)


## 2. Topologie des Nœuds et Composants
### 2.1 Nœud Client (Frontend)
- **Technologie :** React.js (TypeScript) avec Material UI (MUI v5/v6).

- **Moteur de Build :** Vite.js (génération d'artefact SPA statique).

- **Serveur d'Hébergement :** Nginx (image Alpine).

- **Rôle en Prod :** Serveur de fichiers statiques (HTML, CSS, JS) avec gestion des routes en fallback (try_files $uri $uri/ /index.html) et compression Gzip activée.

### 2.2 Nœud Application (Backend API)
1. **Technologie :** Java 17 / Spring Boot 3.x.

2. **Gestionnaire de Dépendances :** Maven.

3. **Artefact :** Executable JAR isolé.

4. **Composants Internes Clés :**

        - ***Spring Security :*** Gestion du cycle de vie des sessions via cookies HTTP-Only.

        - ***Spring Data JPA :*** Abstraction de la couche d'accès aux données.

        - ***Spring Scheduling :*** Moteur de tâches planifiées (background workers pour le Pilier 5 - Maintenance).

        - ***Configuration de Production :** Exécution derrière le reverse proxy Nginx avec des limites de mémoire JVM explicites (-Xms512m -Xmx2g).

### 2.3 Nœud de Données (Database)
1. Technologie : PostgreSQL 16.

2. Rôle : Persistance transactionnelle stricte pour les 6 Piliers métiers.

3. Optimisations Production :

    - Utilisation d'un pool de connexions côté Spring Boot (HikariCP) configuré à un maximum de 20 connexions simultanées par instance backend.

    - Persistance des données via des volumes Docker nommés externes pour éviter toute perte de données lors du redémarrage du conteneur.

    - Indexation stratégique sur les colonnes de recherche fréquentes (immatriculation, statut, code_suivi).

### 2.4 Nœud d'Intégration IA (ai.koog)
1. Technologie : Agent IA local autonome.

2. Rôle : Calcul d'optimisation pour le dispatching automatique des courses et l'analyse prédictive de la logistique (Pilier 2 & 3).

3. Communication : Réseau interne Docker isolé. Les requêtes ne transitent jamais par l'Internet public.

## 🔒 3. Sécurité des Flux & Routage Réseau
### 3.1 Gestion des Sessions et Isolation des Tokens
Pour éliminer les risques de failles XSS (Cross-Site Scripting), aucun token de session (JWT ou Session ID) n'est stocké dans le localStorage ou le sessionStorage du navigateur.

- L'utilisateur s'authentifie via POST /api/v1/auth/login.

- Le serveur valide les accès et répond en injectant un cookie dans l'en-tête de la réponse HTTP.

- Le cookie possède obligatoirement les attributs : HttpOnly (inaccessible en JavaScript), Secure (transmis uniquement en HTTPS) et SameSite=Strict (protection contre les attaques CSRF).

### 3.2 Configuration du Reverse Proxy (Nginx)
Nginx agit comme l'unique point d'entrée (Ingress) de l'infrastructure de production. Il intercepte le trafic sur les ports 80 (redirection automatique) et 443 (HTTPS avec certificats SSL Let's Encrypt).

# Extrait de la configuration Nginx de Production

server {
    listen 443 ssl;
    server_name fleetcontrol.klem-ts.com;

    ssl_certificate /etc/letsencrypt/live/[fleetcontrol.klem-ts.com/fullchain.pem](https://fleetcontrol.klem-ts.com/fullchain.pem);
    ssl_certificate_key /etc/letsencrypt/live/[fleetcontrol.klem-ts.com/privkey.pem](https://fleetcontrol.klem-ts.com/privkey.pem);

    # Routage du Frontend Statique
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # Routage vers l'API Spring Boot
    location /api/ {
        proxy_pass http://fleetcontrol-backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

## 📦 4. Conteneurisation & Déploiement (Docker & Compose)
L'application est orchestrée en production à l'aide d'une configuration multi-conteneurs isolés au sein d'un réseau virtuel privé Docker nommé fleetcontrol-network.

### 4.1 Fichier de Déploiement : docker-compose.prod.yml

**version:** '3.8'

**networks:**
  fleetcontrol-network:
    driver: bridge

**volumes:**
  postgres_prod_data:
    driver: local
  nginx_ssl_certs:
    driver: local

**services:**
  ***fleetcontrol-db:***
    image: postgres:16-alpine
    container_name: fleetcontrol-db
    ***environment:***
      POSTGRES_DB: fleetcontrol
      POSTGRES_USER: klem_admin
      POSTGRES_PASSWORD: ${DB_PRODUCTION_PASSWORD}
    ***volumes:***
      - postgres_prod_data:/var/lib/postgresql/data
    ***networks:***
      - fleetcontrol-network
    restart: always
    ***healthcheck:***
      test: ["CMD-SHELL", "pg_isready -U klem_admin -d fleetcontrol"]
      interval: 10s
      timeout: 5s
      retries: 5

  **fleetcontrol-backend:**
    image: klemtechnologies/fleetcontrol-backend:latest
    container_name: fleetcontrol-backend
    ***environment:***
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://fleetcontrol-db:5032/fleetcontrol
      - SPRING_DATASOURCE_USERNAME=klem_admin
      - SPRING_DATASOURCE_PASSWORD=${DB_PRODUCTION_PASSWORD}
      - AI_KOOG_ENDPOINT=http://ai-koog-agent:9000
    ***depends_on:***
      fleetcontrol-db:
        condition: service_healthy
    ***networks:***
      - fleetcontrol-network
    restart: always

  **ai-koog-agent:**
    image: klemtechnologies/ai-koog-local:latest
    container_name: ai-koog-agent
    networks:
      - fleetcontrol-network
    restart: always

  **fleetcontrol-proxy:**
    image: nginx:alpine
    container_name: fleetcontrol-proxy
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - nginx_ssl_certs:/etc/letsencrypt:ro
    depends_on:
      - fleetcontrol-backend
    networks:
      - fleetcontrol-network
    restart: always


## 🚀 5. Stratégie de Mise en Production & Résilience
Pour garantir une disponibilité maximale et éviter les interruptions de service lors des mises à jour, les règles suivantes sont appliquées au pipeline de déploiement :

### 5.1 Pipeline CI/CD (GitHub Actions)

    1. Validation : Lancement automatique des tests unitaires et d'intégration à chaque push sur la branche main.

    2. Build de l'image : Compilation du code Java via Maven et build de l'application React via Vite. En cas de succès, génération des images Docker.

    3. Livraison : Push des nouvelles images étiquetées (v1.0.0, latest) sur le registre privé de Klem Technologies.

    4. Déploiement Continu : Connexion SSH sécurisée au serveur de production, exécution d'un docker compose pull suivi d'un redémarrage progressif des conteneurs.

### 5.2 Stratégie de Sauvegarde (Backups)
Un script de sauvegarde automatique (cronjob) est configuré sur l'hôte de production pour s'exécuter tous les jours à 02h00 du matin (juste après l'exécution du planificateur de maintenance du Pilier 5) :

    - Exécution d'un pg_dump compressé de la base de données PostgreSQL.

    - Chiffrement de l'archive de sauvegarde.

    - Exportation de l'archive vers un espace de stockage objet distant et sécurisé (S3 ou équivalent).

    - Rétention stricte des sauvegardes sur une période glissante de 30 jours.

### 5.3 Supervision & Health Checks
1. Backend : L'activation de Spring Boot Actuator expose un point d'accès de santé publique sur /api/actuator/health. Ce endpoint renvoie un statut UP uniquement si la connexion à PostgreSQL et les communications avec le nœud ai.koog sont pleinement opérationnelles.

2. Redémarrage automatique : La directive restart: always présente sur chaque composant du fichier Docker Compose assure une relance immédiate en cas de crash applicatif ou de redémarrage imprévu du serveur physique hébergeant la solution.

