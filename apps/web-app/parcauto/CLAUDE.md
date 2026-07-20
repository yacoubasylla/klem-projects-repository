# Projet : Gestion de Parc Auto - KLEM Technologies

# Instructions de Développement 
## 📋 Présentation du Projet
Application d'entreprise critique de gestion intégrale de parc automobile (Missions, courses, locations, livraisons, maintenance préventive/corrective, suivi de carburant, comptabilité des revenus, planning de chauffeurs et gestion de la paie des chauffeurs salariés).

## 🛠️ Commandes du Projet (Monorepo Turborepo)
Toutes les commandes doivent être lancées depuis la racine du monorepo.

*   **Build Global** : `pnpm run build` ou `pnpm turbo run build`
*   **Mode Développement Global** : `pnpm dev` ou `pnpm turbo run dev`
*   **Dev Frontend Séparé** : `pnpm --filter web-app dev`
*   **Dev Backend Séparé** : `pnpm --filter backend-api bootRun` *(ou `./mvnw spring-boot:run` dans le dossier)*
*   **Infrastructure Locale (Base de données)** : `docker-compose up -d postgres-db`
*   **Tests Unitaires** : `pnpm run test` ou `pnpm turbo run test`
*   **Linting & Formattage** : `pnpm run lint` && `pnpm run format`

---

## 🏗️ Stack Technique Stricte

### 1. Frontend
*   **Framework & Runtime** : React.js 18+ propulsé par **Vite** et configuré en Monorepo Node.
*   **Langage** : TypeScript en mode ultra-strict (`strict: true` dans le `tsconfig`). **Interdiction absolue d'utiliser le type `any`**. Tout doit être explicitement typé (Interfaces, Types, Generics).
*   **Design System & UI** : **Material UI (MUI) v5+**. Utilisation des hooks de style natifs et du système de grille responsive (`Grid2`, `Stack`, `Box`).

### 2. Backend
*   **Framework** : **Java 17** + **Spring Boot 3.x** (Architecture en couches imperméables : Controller -> Service -> Repository -> Entity).
*   **Sécurité** : Spring Security, Stateful JWT stockés exclusivement dans des cookies sécurisés `HttpOnly` et `SameSite=Strict`.
*   **Persistance & ORM** : Spring Data JPA (Hibernate). Utilisation stricte de `@Transactional(readOnly = true)` sur toutes les méthodes de lecture pour optimiser les performances d'accès.
*   **Gestion des Migrations SQL** : **Flyway** ou **Liquibase**. Aucun schéma ne doit être modifié via Hibernate en production (`ddl-auto: validate`). Les scripts SQL incrémentaux doivent se trouver dans `apps/backend-api/src/main/resources/db/migration/`.

### 3. Base de Données
*   **Moteur** : **PostgreSQL 15+**. Indexation obligatoire sur les clés étrangères (`_id`) et sur les champs à forte fréquence de recherche textuelle ou opérationnelle (ex: `immatriculation`, `statut_vehicule`, `token_suivi`).

### 4. Couche Intelligente (IA Agent)
*   **Moteur d'automatisation** : Intégration de la solution **`ai.koog`** pour l'orchestration, le dispatch automatique des missions complexes et l'analyse sémantique des rapports d'incidents terrains rédigés par les chauffeurs.

---

## 📐 Directives d'Architecture & Code Style

### Backend (Spring Boot)
*   **Zéro logique métier dans les contrôleurs** : Les contrôleurs interceptent les requêtes, valident les DTO (`@Valid`) et délèguent immédiatement aux services.
*   **Gestion des erreurs** : Centralisée via un `@ControllerAdvice` et un `GlobalExceptionHandler` renvoyant des réponses d'erreur standardisées au format JSON.
*   **Dossiers** : Respecter le partitionnement par domaine métier (`com.klem.fleetcontrol.modules.[vehicule|course|location|maintenance|finance]`).

### Frontend (React)
*   **Zéro logique métier / appels API dans les composants graphiques** : Isolation complète dans des hooks personnalisés (custom hooks) utilisant React Query ou Axios.
*   **Composants Partagés Globaux** : Réutilisation systématique des composants d'entreprise Klem définis dans le package partagé `packages/ui` :
    *   `<KlemTable />` : Pour l'affichage, la pagination et le filtrage des flottes, contrats et gains.
    *   `<KlemButton />` : Pour les actions standards, soumissions de formulaires et déclenchements de statuts.
*   **IHM Premium Mobile-First** : Les barres de navigation et les sidebars d'administration doivent modifier dynamiquement le conteneur principal (`main layout content`) par un décalage fluide. Aucun chevauchement visuel, chevauchement de boutons ou texte tronqué n'est toléré sur les résolutions mobiles (utilisées par les chauffeurs).

### Best practices
- **Séparation des responsabilités** : Pas de logique métier dans les composants React (utiliser des hooks personnalisés) ni dans les contrôleurs Spring (tout reste dans les `@Service`).
- **IHM Premium** : Les conteneurs MUI doivent utiliser des layouts fluides (Grid/Flexbox). Les sidebars et barres de navigation doivent repousser le contenu dynamiquement sans créer de chevauchement visuel.
- **Gestion des transactions** : Utilisation stricte de `@Transactional(readOnly = true)` sur les services de lecture pour optimiser les performances PostgreSQL.
- **Conventions de Nommage Commits** : Format Conventional Commits obligatoire (`feat(maintenance): ...`, `fix(payment): ...`).

## 🚦 Règles Métier & Contraintes Spécifiques au Projet

1.  **Validation Temporelle des Missions** : Le backend doit intégrer un validateur strict pour toute entité ou objet `Mission`. Il est formellement interdit d'avoir une date de fin de mission inférieure ou égale à la date de début. Les chevauchements de plannings pour un même véhicule ou un même chauffeur sur une même plage horaire doivent lever une exception levée au niveau du `@Service`.
2.  **Gestion des Traces Opérationnelles (Logs)** : Tous les journaux d'activité, événements de dispatching gérés par `ai.koog` et historiques critiques de changement de statut des véhicules doivent être sérialisés et stockés de manière persistante dans le répertoire dédié : `knowledge/outputs/logs/` pour des audits et analyses ultérieurs.
3.  **Gestion Git & Commits** : Règle stricte des **Conventional Commits** pour l'équipe de développement :
    *   `feat(location): ajout de l'état des lieux de départ`
    *   `fix(finance): correction du calcul de la commission chauffeur`
    *   `chore(deps): mise à jour de la bibliothèque ai.koog`

---

## 👥 Gouvernance & Contacts

*   **Lead Architecte / Responsable de projet** : Yacouba SYLLA
*   **Email de contact** : ciyasyl@gmail.com
*   **Gouvernance Technique (ADR)** : Avant toute modification de structure ou déviation technique, consultez obligatoirement la documentation d'architecture principale située dans : `claude-collaboration/history/decision-log.md` (ou l'ADR de référence historique : `docs/adr/gestion-parc-v1.md`).