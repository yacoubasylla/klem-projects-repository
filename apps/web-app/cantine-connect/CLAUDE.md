# Projet : Cantine Connect - Système Multi-Établissements (CANTINE)[cite: 1, 9]

# Instructions de Développement (Claude Code & Senior Staff)[cite: 8]
## 📋 Présentation du Projet
Application d'entreprise critique de gestion intégrale de restauration scolaire multi-sites (Enregistrement structurel des élèves par établissement et classe, liste des élèves, inscription à la cantine, suivi en temps réel des passages au réfectoire par scan de QR Code, et moteur de paiement Mobile Money via Orange, MTN, Moov, et Wave)[cite: 1, 5, 9].

## 🛠️ Commandes du Projet (Structure Découplée)[cite: 6]
Toutes les commandes doivent être exécutées dans leurs dossiers respectifs[cite: 8].

### ⚛️ Front-end (`client-frontend/`)[cite: 6]
*   **Installation des dépendances** : `npm install`[cite: 8]
*   **Mode Développement Local** : `npm run dev`[cite: 8]
*   **Build de Production** : `npm run build`[cite: 8]
*   **Linting & Formatage** : `npm run lint`[cite: 8]

### 🍃 Back-end (`server-backend/`)[cite: 6]
*   **Compilation & Tests Unitaires** : `./mvnw clean test`[cite: 8]
*   **Exécution en Local** : `./mvnw spring-boot:run`[cite: 8]
*   **Packaging JAR de Production** : `./mvnw clean package -DskipTests`[cite: 8]

---

## 🏗️ Stack Technique Stricte[cite: 9]

### 1. Front-end[cite: 9]
*   **Framework & Runtime** : React.js 18+ propulsé par **Vite** (JavaScript pur, `.js`, `.jsx`).
*   **Design System & UI** : **Material UI (MUI) v5+**[cite: 8]. Utilisation stricte des composants de structure (`Box`, `Stack`, `Card`) et des composants de mise en page condensée (`Tabs`, `Tab`, `TabPanel`)[cite: 8, 10].
*   **Appels API** : Client Axios unique situé dans `services/` disposant d'intercepteurs pour l'injection du token JWT et la capture des erreurs réseau[cite: 6, 10].

### 2. Back-end[cite: 9]
*   **Framework** : **Java 17+** + **Spring Boot 3.x**[cite: 9]. Architecture en couches imperméables : `Controller` ➔ `Service` ➔ `Repository` ➔ `Entity`.
*   **Sécurité** : Spring Security + Filtre Stateless d'échange de jetons JWT injectés via les en-têtes HTTP Authorizations[cite: 6, 8, 10].
*   **Persistance & ORM** : Spring Data JPA (Hibernate)[cite: 8, 10]. Utilisation obligatoire de jointures explicites (`FETCH JOIN`) ou de requêtes dérivées optimisées pour éradiquer le problème de performance *N+1 Select* lors des affectations structurelles (Élève ➔ Classe ➔ Établissement)[cite: 6, 10].

### 3. Base de Données & Traces[cite: 9]
*   **Moteur** : **PostgreSQL** ou **MySQL**[cite: 9]. Indexation stratégique obligatoire sur les clés de recherche opérationnelle (`student_id`, `qr_code_token`, `operator_tx_id`)[cite: 6, 10].
*   **Table de Trace** : Alimentation asynchrone et transparente de la table `ActionLog` via la programmation orientée aspect (**Spring AOP**)[cite: 2, 5, 6, 10].

---

## 📐 Directives d'Architecture & Code Style[cite: 8]

### Back-end (Spring Boot)
*   **Zéro logique métier dans les contrôleurs** : Les contrôleurs interceptent les requêtes, valident les données entrantes et délèguent immédiatement aux services[cite: 8].
*   **Couche de Transfert (DTO) obligatoire** : Interdiction formelle d'exposer ou de retourner les entités JPA directement aux contrôleurs ou à l'API REST. Tout échange doit être encapsulé dans un DTO (Request/Response)[cite: 6, 8, 10].
*   **Gestion des erreurs** : Centralisée via un `@ControllerAdvice` et un gestionnaire global renvoyant des réponses d'erreur standardisées au format JSON[cite: 6, 8].

### Front-end (React)
*   **Zéro logique métier / appels API directs dans les vues** : Découpage obligatoire. Les requêtes API doivent être isolées dans des services dédiés ou des hooks personnalisés (*custom hooks*)[cite: 6, 10].
*   **Contrainte d'Écran Majeure (Zéro Défilement Vertical)** : Les gestionnaires sur site travaillant sur des écrans compacts, l'interface de création/modification d'élèves doit impérativement exploiter un **formulaire à onglets contrôlé** (`@mui/material/Tabs`) scindant les données en 3 volets logiques clairs (Général, Cantine/Affectation, Contacts/Allergies)[cite: 5, 7, 8, 10].
*   **Pagination Obligatoire** : La liste des élèves doit utiliser une pagination stricte côté serveur via l'API pour supporter une volumétrie de plus de 10 000 lignes sans ralentir le navigateur[cite: 5].

---

## 🚦 Règles Métier & Contraintes Spécifiques au Projet[cite: 1]

1.  **Traçabilité Inaltérable (Table de Trace / Logs) :** Toutes les opérations d'écriture et d'altération de données (Ajout, Modification/Update, Suppression) de l'application doivent générer un enregistrement automatique et inaltérable dans la table `ActionLog` via AOP, capturant l'auteur, l'heure, le type d'action et le payload[cite: 2, 5, 6, 10].
2.  **Moteur de Paiement & Webhooks :** Traitement asynchrone sécurisé des notifications de paiement (webhooks) en provenance de CinetPay ou PayDunya pour mettre à jour instantanément le statut d'accès de l'élève (Orange Money, MTN, Moov, Wave)[cite: 1, 11].
3.  **Résilience du Contrôle d'Accès :** Validation du QR Code en réfectoire en moins d'une seconde[cite: 1, 5]. L'application doit implémenter un cache local de secours de 24h mis à jour à chaque connexion pour continuer à fonctionner en mode dégradé (hors-ligne)[cite: 1, 5, 7, 11].
4.  **Gestion Git & Commits :** Règle stricte des **Conventional Commits** pour l'équipe de développement et Claude Code[cite: 4, 8] :
    *   `feat(students): implement layout-optimized tabs form for pupil creation`[cite: 4]
    *   `fix(payments): correct wave webhook signature verification payload`[cite: 4]
    *   `docs(architecture): update relational schema for access logs`[cite: 4]
5.  **Manuel Utilisateur — Mise à jour obligatoire à chaque livraison de fonctionnalité :** Depuis la réorganisation documentaire du 2026-08-04, les livrables client (`manuel-utilisateur.md`/`.docx`/`.pdf`, `cahier-de-recette.md`/`.docx`, `offre-financiere-cantine-connect.md`/`.pdf`, captures d'écran) ne vivent plus dans ce dépôt : ils sont édités dans `klem-labs-repository/projects/03_cantine_connect/livraison/`, qui en est la source de vérité (voir `klem-labs-repository/README.md` §1). Ce dépôt ne garde qu'une copie en lecture seule sous `docs/from-labs/livraison/`. Les trois fichiers `manuel-utilisateur.*` partagent volontairement le même nom de base (pas de nom alternatif type `user-guide` — source .md et livrables .docx/.pdf identifiés d'un coup d'œil). Avant de clore toute tâche qui change un comportement visible par l'utilisateur (nouveau module, nouvelle règle métier, nouveau champ/filtre, changement de permission par rôle) :
    *   Éditer directement `klem-labs-repository/projects/03_cantine_connect/livraison/manuel-utilisateur.md` (texte **et** captures d'écran si l'IHM a changé — captures via Playwright headless, cohérentes avec celles déjà en place, déposées dans `livraison/assets/`).
    *   Vérifier la cohérence des rôles/permissions annoncés avec le code réel (source de vérité : `klem-labs-repository/projects/03_cantine_connect/specifications_fonctionnelles.md` et `collaboration/history/decision-log.md` de ce dépôt) avant de committer.
    *   Régénérer `manuel-utilisateur.docx`/`.pdf` depuis le `.md` mis à jour. Sans `pandoc` disponible : convertir le `.md` en HTML (`npx marked manuel-utilisateur.md -o /tmp/corps.html`), l'insérer dans un gabarit HTML avec styles + `@page { size: 21cm 29.7cm; margin: 2cm; }` (le mot-clé CSS `A4` seul est ignoré par le filtre d'import de LibreOffice — utiliser les dimensions explicites), puis `soffice --headless --convert-to 'docx:MS Word 2007 XML' fichier.html` pour le `.docx` et `soffice --headless --convert-to 'pdf:writer_pdf_Export' fichier.html` **directement depuis le HTML** pour le PDF (repartir du `.docx` pour le PDF réintroduit le format Letter par défaut).
    *   Commit + push **dans `klem-labs-repository`** (`docs(livraison): ...`).
    *   Rafraîchir la copie locale : `python3 platform-devsecops/scripts/klem_promote.py 03_cantine_connect` depuis `klem-labs-repository`, puis commit + push **dans ce dépôt** (`docs(from-labs): refresh copie livraison`).

---

## 👥 Gouvernance & Contacts[cite: 1, 2]

*   **Lead Architecte / Responsable de projet** : Yacouba SYLLA[cite: 1]
*   **Email de contact** : ciyasyl@gmail.com[cite: 1]
*   **Gouvernance Technique (ADR)** : Avant toute modification de structure ou déviation technique, consultez obligatoirement le registre officiel d'architecture situé dans : `docs/DECISION-LOG.md`[cite: 2].