# SKILLS-TECHNICAL.md - Compétences Techniques Full-Stack (React & Spring Boot)

Ce fichier dresse la matrice des compétences d'ingénierie logicielle senior que Claude Code doit appliquer lors de l'écriture du code de la plateforme.

## ⚛️ 1. Front-End : Expertise React.js (Vite & Material UI)
- **Architecture Globale & State** : Utilisation exclusive des Hooks React (`useState`, `useEffect`, `useContext` pour la session et les thèmes, `useReducer` pour les formulaires complexes).
- **Rendu & UI Compacte (MUI)** :
  - Maîtrise avancée des composants `@mui/material` (en particulier `<Tabs>`, `<Tab>`, `<Card>`, `<DataGrid>`).
  - Strict respect de la contrainte **Zéro défilement vertical** sur l'écran d'enregistrement : éclatement de la saisie à travers un composant à onglets contrôlé.
- **Performance de Navigation** : Implémentation du Lazy Loading avec `React.lazy()` et `Suspense` pour charger les modules (Admin, Parents, Scan) séparément et alléger le bundle initial sur réseaux mobiles.
- **Consommation API & Intercepteurs** : Configuration d'un client unique Axios avec intercepteurs pour injecter automatiquement le token JWT et intercepter les erreurs globales (ex: Redirection vers login sur code 401).

## 🍃 2. Back-End : Expertise Spring Boot 3.x & REST API
- **Modularité & Injection** : Découpage chirurgical par packages (`controller`, `dto`, `service`, `repository`, `entity`). Inversion de contrôle stricte via `@Autowired` sur constructeurs.
- **Persistance Avancée (Spring Data JPA)** :
  - Optimisation des requêtes SQL générées via l'utilisation de requêtes dérivées, de `@Query` personnalisées et de jointures explicites (`FETCH JOIN`) pour éradiquer le problème N+1 Select.
  - Indexation stratégique des tables sous PostgreSQL/MySQL (notamment sur `student_id`, `qr_code_token` et `operator_tx_id`).
- **Sécurisation & Filtres Stateless** : Implémentation de Spring Security couplé à un filtre JWT personnalisé pour authentifier les requêtes API sans état (Stateless).
- **Programmation Orientée Aspect (AOP)** : Utilisation de Spring AOP (`@Aspect`, `@Around`) pour intercepter de manière transparente les méthodes d'écriture des contrôleurs afin d'alimenter la table de trace globale `ActionLog`.

## 🔄 3. DevOps, CI/CD & Environnement
- **Pipeline Gitflow/Feature Branch** : Connaissance avancée des commandes Git pour manipuler, nettoyer et structurer les branches de fonctionnalités avant les Pull Requests.
- **Déploiement Découplé** : Configuration des fichiers de build pour Vercel (Front) et configuration du build Maven pour conteneurisation ou exécution cloud (Back).
[ZoneTransfer]
ReferrerUrl=https://gemini.google.com/
