# 🧠 KLEM Technologies : Référentiel de Contexte Global (CONTEXT.md)

> **Directive système pour l'IA (Claude Code / Cursor) :**
> Ce document est ta boussole principale. Il définit l'ADN de KLEM Technologies & Services, nos règles d'ingénierie et la cartographie de notre monorepo. Tu dois consulter et respecter ces principes avant de générer du code, de proposer une architecture ou de modifier des fichiers.

---

## 🎯 1. Objectif Fondamental de KLEM Technologies
KLEM Technologies & Services a pour ambition de devenir l'intégrateur technologique de référence en Afrique. Notre objectif est de mener la transformation digitale des gouvernements et des grandes organisations privées en imposant la souveraineté technologique, la résilience des infrastructures et l'excellence logicielle locale. Nous construisons des systèmes conçus pour durer et pour scaler.

---

## 🚀 2. Nos Missions et Services
Nous opérons sur l'ensemble de la chaîne de valeur technologique pour résoudre des problématiques métiers complexes :
*   **Ingénierie Logicielle Sur-Mesure :** Développement de plateformes SaaS, d'ERP d'entreprise et d'outils de gestion métiers hautement sécurisés.
*   **Ingénierie de la Donnée (Big Data) :** Mise en place de plateformes de traitement de données en temps réel pour l'aide à la décision (ETL, Streaming, Analytics).
*   **Intégration d'Intelligence Artificielle :** Déploiement d'agents IA locaux (comme notre nœud `ai.koog`) pour l'automatisation experte, sans compromettre la confidentialité des données clients.
*   **Digitalisation Opérationnelle :** Création d'outils mobiles de terrain pour connecter les travailleurs hors-ligne aux systèmes d'information centraux.

---

## 🧩 3. Types de Projets et Fonctionnalités Cibles
Pour maintenir la flexibilité de notre monorepo, nos développements se classent selon les typologies suivantes, sans distinction stricte par nom de client :

1.  **Back-Offices & ERP Administrateurs (Web) :**
    *   *Fonctionnalités :* Tableaux de bord analytiques complexes, gestion des accès (RBAC), facturation, suivi de rentabilité et paramétrage système.
    *   *Stack :* React.js, TypeScript, Material UI (MUI).
2.  **Applications Mobiles "Terrain" (Mobile-First) :**
    *   *Fonctionnalités :* Saisie rapide de données, scans, signature électronique, fonctionnement en mode dégradé (offline-first), géolocalisation.
3.  **API Core et Moteurs de Règles (Backend) :**
    *   *Fonctionnalités :* Transactions financières ACID, machines d'états complexes, planification de tâches en arrière-plan (CRON), authentification par cookies sécurisés.
    *   *Stack :* Java Spring Boot 3.x, PostgreSQL.
4.  **Pipelines de Données et Temps Réel (Data) :**
    *   *Fonctionnalités :* Capture des changements de données (CDC), agrégation de flux lourds, nettoyage de données, alimentation de Data Warehouses.
    *   *Stack :* Apache Kafka, Kafka Connect, Apache Spark.

## 📂 4. Cartographie de quelques Projets en cours

Le monorepo centralise plusieurs initiatives stratégiques interconnectées :

### A. L'Écosystème "FleetControl" (Gestion de Parc Auto)
*   **apps/web-app** : Dashboard d'administration complet (Back-office ERP) développé en **React.js / Material UI (MUI)**. Destiné aux gestionnaires pour le suivi des coûts, de la maintenance, de la rentabilité et de l'affectation.
*   **apps/mobile-app** : Application mobile épurée dédiée aux chauffeurs et livreurs. Priorité à l'ergonomie terrain (saisie rapide des états des lieux, signalement d'incidents, suivi du portefeuille, mode déconnecté).
*   **apps/backend-api** : API Core en **Java Spring Boot 3.x**. Centralise la logique métier, gère la sécurité, l'authentification par cookies étanches, et communique avec le nœud IA.

### B. Projets Applicatifs Métiers
*   **Gestion de Cantine Scolaire** : Module de suivi des flux, des abonnements, des présences et de la facturation des repas.
*   **Gestion de Pharmacie** : Système de suivi des stocks critiques, gestion des périssables, interfaçage avec les mutuelles et facturation rapide.

### C. La Plateforme Data & Big Data
*   **Data Plateforme Spark-Kafka** : Pipeline d'ingestion et de traitement en temps réel.
    *   **Ingestion** : Capture des changements de données (CDC) depuis des bases de données de production (ex: Oracle/PostgreSQL).
    *   **Streaming & ETL** : Apache Kafka et Kafka Connect pour le transport des messages ; Apache Spark pour le traitement lourd, l'agrégation statistique et le nettoyage des données avant stockage dans notre Data Warehouse PostgreSQL.
    *   **Finalité** : Génération automatisée de statistiques commerciales, d'indicateurs de performance (KPI) et d'intrants pour les modèles prédictifs.

## 📐 4. Méthodologies et Bonnes Pratiques d'Ingénierie

En tant qu'assistant IA sur ce projet, ton code doit obéir aux standards industriels suivants :

*   **Loi de l'Isolation et du Partage (DRY) :** Ne duplique jamais un composant UI ou une fonction utilitaire. Si un élément sert plusieurs applications, place-le immédiatement dans `packages/ui/` ou `packages/utils/`.
*   **Sécurité by Design :** Zéro token JWT dans le `localStorage`. L'authentification repose exclusivement sur des cookies `HttpOnly`, `Secure` et `SameSite=Strict`.
*   **Typage Strict (Zéro `any`) :** Le TypeScript doit être intraitable. Les contrats d'API (DTOs) côté Java Spring Boot doivent être parfaitement synchronisés avec les interfaces Frontend.
*   **Développement Feature-Driven :** On ne code qu'une seule fonctionnalité à la fois. Un cycle complet comprend : *Migration DB ➔ Entité Backend ➔ Service & Controller ➔ Hook Frontend ➔ UI Component ➔ Mise à jour du fichier HISTORY.md*.
*   **Traçabilité des Décisions (ADR) :** Aucune nouvelle librairie majeure ou changement d'architecture ne doit être implémenté sans avoir d'abord généré un document ADR dans `collaboration/history/adr/`.
* **Réalités du Marché Africain** : Les applications mobiles et web doivent être optimisées pour des connexions parfois instables (mise en cache agressive, requêtes légères, gestion de l'état offline).


## 📂 5. Exploitation Intelligente de l'Espace de Travail

Voici comment naviguer et exploiter notre architecture Turborepo. **Ne crée jamais de fichiers au mauvais endroit.**

*   📍 **Si tu dois créer une application complète déployable :** Va dans `apps/`.
*   📍 **Si tu dois créer un composant bouton, un tableau ou configurer ESLint :** Va dans `packages/`.
*   📍 **Si tu as besoin de comprendre les règles de l'IA ou d'exécuter un script custom :** Regarde dans `.claude/` et `.cursorrules`.
*   📍 **Si tu dois lire une documentation technique ou le schéma d'une base de données :** Consulte `collaboration/doc/`.
*   📍 **Si tu as fini une tâche et que tu dois journaliser le travail :** Mets à jour `collaboration/history/history-log.md`.
*   📍 **Si tu dois analyser un PDF, un export ou une documentation externe fournie par un client :** Va chercher dans `knowledges/raw/`.

Lorsque tu travailles dans ce dépôt :
*   📍 **Pense Écosystème** : N'oublie jamais que le backend Spring Boot sert à la fois l'application Web d'administration et l'application mobile terrain.
*   📍 **Respecte la mémoire historique** : Avant de modifier une structure de table ou un flux Kafka, vérifie les enregistrements d'arbitrage dans `collaboration/history/adr/`.
*   📍 **Sois force de proposition** : Si tu détectes une opportunité d'optimiser un pipeline de données Spark ou de sécuriser un cookie d'authentification dans l'API, propose-le d'abord en t'appuyant sur les standards de KLEM.


*Fin du contexte système. L'agent doit confirmer la bonne assimilation de ces règles avant de procéder à la première tâche.*