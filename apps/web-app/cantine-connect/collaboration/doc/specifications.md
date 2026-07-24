# Spécifications Fonctionnelles et Opérationnelles — Cantine Connect

> Source : CONCEPTION.md (section 4), CLAUDE.md, SKILLS-FUNCTIONAL.md, SKILLS-TECHNICAL.md, decision-log.md.

---

## Principes Directeurs pour l'Implémentation

1. **Implémentation incrémentale** : Un module à la fois, en complétant entièrement `BD ➔ Entity/Repository ➔ Service ➔ Controller ➔ Hook ➔ Composant IHM` avant de passer au suivant.
2. **DTO obligatoire** : Interdiction formelle d'exposer les entités JPA directement à l'API. Tout échange utilise des DTOs (Request/Response).
3. **Zéro logique métier dans les contrôleurs** : Les contrôleurs valident les données entrantes et délèguent immédiatement aux services.
4. **Format d'erreur standard** :
   ```json
   {
     "timestamp": "2026-06-30T10:00:00Z",
     "status": 400,
     "error": "BAD_REQUEST",
     "message": "L'élève est déjà inscrit à la cantine pour cet établissement.",
     "path": "/api/v1/eleves"
   }
   ```

---

## Module 1 : Gestion Structurelle Multi-Établissements

### 1.1 Modèle de Données (PostgreSQL)

```sql
CREATE TABLE etablissements (
    id          BIGSERIAL PRIMARY KEY,
    nom         VARCHAR(150) NOT NULL,
    adresse     VARCHAR(255),
    ville       VARCHAR(100) NOT NULL DEFAULT 'Abidjan',
    telephone   VARCHAR(20),
    actif       BOOLEAN NOT NULL DEFAULT true,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE niveaux (
    id                BIGSERIAL PRIMARY KEY,
    etablissement_id  BIGINT NOT NULL REFERENCES etablissements(id),
    libelle           VARCHAR(50) NOT NULL,  -- ex: Primaire, Collège, Lycée
    ordre             INT NOT NULL DEFAULT 0
);

CREATE TABLE classes (
    id          BIGSERIAL PRIMARY KEY,
    niveau_id   BIGINT NOT NULL REFERENCES niveaux(id),
    libelle     VARCHAR(50) NOT NULL,  -- ex: CM2, 3ème A, Terminale C
    annee_scolaire VARCHAR(10) NOT NULL  -- ex: 2025-2026
);

CREATE TYPE enum_statut_acces AS ENUM ('EN_ATTENTE_PAIEMENT', 'AUTORISE', 'GRACE', 'SUSPENDU');
CREATE TYPE enum_regime_alimentaire AS ENUM ('STANDARD', 'SANS_PORC', 'VEGETARIEN', 'ALLERGIE_SPECIFIQUE');

CREATE TABLE eleves (
    id                  BIGSERIAL PRIMARY KEY,
    etablissement_id    BIGINT NOT NULL REFERENCES etablissements(id),
    classe_id           BIGINT NOT NULL REFERENCES classes(id),
    matricule           VARCHAR(30) UNIQUE NOT NULL,
    nom                 VARCHAR(100) NOT NULL,
    prenom              VARCHAR(100) NOT NULL,
    date_naissance      DATE,
    photo_url           VARCHAR(255),
    -- Cantine / Affectation
    qr_code_token       UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    statut_acces        enum_statut_acces NOT NULL DEFAULT 'EN_ATTENTE_PAIEMENT',
    date_fin_grace      DATE,
    est_boursier        BOOLEAN NOT NULL DEFAULT false,
    regime_alimentaire  enum_regime_alimentaire NOT NULL DEFAULT 'STANDARD',
    -- Contacts / Allergies
    parent_nom          VARCHAR(150) NOT NULL,
    parent_telephone    VARCHAR(20) NOT NULL,
    parent_email        VARCHAR(100),
    allergies           TEXT,
    notes_medicales     TEXT,
    -- Métadonnées
    actif               BOOLEAN NOT NULL DEFAULT true,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_eleves_etablissement ON eleves(etablissement_id);
CREATE INDEX idx_eleves_classe ON eleves(classe_id);
CREATE INDEX idx_eleves_qr_code_token ON eleves(qr_code_token);
CREATE INDEX idx_eleves_statut_acces ON eleves(statut_acces);
```

### 1.2 Contrats d'API REST — Élèves

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/v1/eleves` | Liste paginée avec filtres (etablissementId, classeId, statutAcces, search) |
| `POST` | `/api/v1/eleves` | Création d'un nouvel élève |
| `GET` | `/api/v1/eleves/{id}` | Détail d'un élève |
| `PUT` | `/api/v1/eleves/{id}` | Mise à jour complète du profil |
| `PATCH` | `/api/v1/eleves/{id}/statut` | Changement de statut d'accès uniquement |
| `DELETE` | `/api/v1/eleves/{id}` | Désactivation logique (actif = false) |
| `GET` | `/api/v1/eleves/{id}/qrcode` | Génération / récupération du QR Code PNG |
| `POST` | `/api/v1/eleves/import` | Import en masse via fichier CSV/Excel |

### 1.3 Règles Métier

- **Règle #101** : Le `qr_code_token` est généré automatiquement à la création de l'élève. Il ne peut être régénéré que par le `SUPER_ADMIN` ou le `GESTIONNAIRE` (action tracée dans `action_log`).
- **Règle #102** : La suppression d'un élève est toujours logique (`actif = false`), jamais physique. Les données sont conservées pour conformité ARTCI.
- **Règle #103** : La liste des élèves est obligatoirement paginée côté serveur (paramètres `page`, `size`, `sort`). Taille maximale par page : 50.
- **Règle #104** : Un gestionnaire ne peut créer ou consulter que les élèves de son `etablissement_id`.

### 1.4 Spécifications IHM (ADR-002)

- **Formulaire à 3 onglets MUI** (`<Tabs>` contrôlé) :
  - Onglet 1 — **Général** : Nom, prénom, date de naissance, matricule, photo, classe.
  - Onglet 2 — **Cantine/Affectation** : Statut d'accès, boursier, régime alimentaire, date de grâce.
  - Onglet 3 — **Contacts/Allergies** : Nom/téléphone/email du parent, allergies, notes médicales.
- **Tableau de liste** : Composant MUI `<DataGrid>` avec pagination serveur. Badges de couleur par statut (`success` / `warning` / `error`).
- **Alerte rouge** si `date_fin_grace` est inférieure à J+3.

---

## Module 2 : Moteur de Paiement Mobile Money

### 2.1 Modèle de Données (PostgreSQL)

```sql
CREATE TYPE enum_operateur_mm AS ENUM ('ORANGE_MONEY', 'MTN_MOMO', 'MOOV_MONEY', 'WAVE', 'VISA_MASTERCARD', 'VIREMENT');
CREATE TYPE enum_statut_transaction AS ENUM ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED');
CREATE TYPE enum_periodicite AS ENUM ('JOURNALIER', 'MENSUEL', 'TRIMESTRIEL', 'ANNUEL');

CREATE TABLE transactions_paiement (
    id                  BIGSERIAL PRIMARY KEY,
    eleve_id            BIGINT NOT NULL REFERENCES eleves(id),
    reference_interne   VARCHAR(100) UNIQUE NOT NULL,  -- Générée par notre backend
    operator_tx_id      VARCHAR(150),                   -- ID retourné par l'agrégateur
    montant             NUMERIC(12, 2) NOT NULL,
    operateur           enum_operateur_mm NOT NULL,
    periodicite         enum_periodicite NOT NULL DEFAULT 'MENSUEL',
    statut              enum_statut_transaction NOT NULL DEFAULT 'PENDING',
    agregateur          VARCHAR(50) NOT NULL,           -- 'CINETPAY' | 'PAYDUNYA'
    webhook_payload     JSONB,                          -- Payload brut du webhook reçu
    date_debut_periode  DATE NOT NULL,
    date_fin_periode    DATE NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_eleve ON transactions_paiement(eleve_id);
CREATE INDEX idx_transactions_operator_tx_id ON transactions_paiement(operator_tx_id);
CREATE INDEX idx_transactions_statut ON transactions_paiement(statut);
```

### 2.2 Contrats d'API REST — Paiements

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/v1/paiements/initier` | Initiation d'une transaction (retourne l'URL de paiement agrégateur) |
| `POST` | `/api/v1/paiements/webhook/cinetpay` | Réception webhook CinetPay (public, sécurisé par HMAC) |
| `POST` | `/api/v1/paiements/webhook/paydunya` | Réception webhook PayDunya (public, sécurisé par signature) |
| `GET` | `/api/v1/paiements/eleve/{eleveId}` | Historique des transactions d'un élève |
| `GET` | `/api/v1/paiements/{id}/recu` | Génération du reçu PDF |

### 2.3 Règles Métier

- **Règle #201** : Lors de la réception d'un webhook `SUCCESS`, le backend met à jour `statut_acces = AUTORISÉ` de l'élève dans la même transaction JPA. Action tracée dans `action_log`.
- **Règle #202** : Un webhook doit être vérifié par signature HMAC avant traitement. Tout webhook non vérifié est rejeté avec HTTP 401.
- **Règle #203** : La `reference_interne` est générée par le backend avant l'appel à l'agrégateur (idempotence — pas de double paiement).
- **Règle #204** : En cas de `FAILED`, le `statut_acces` de l'élève n'est pas modifié. Une notification est envoyée au parent.

---

## Module 3 : Contrôle d'Accès & Scan QR Code

### 3.1 Modèle de Données (PostgreSQL)

```sql
CREATE TYPE enum_resultat_scan AS ENUM ('AUTORISE', 'REFUSE', 'GRACE', 'ANTI_PASSBACK', 'HORS_LIGNE');

CREATE TABLE passages_refectoire (
    id              BIGSERIAL PRIMARY KEY,
    eleve_id        BIGINT NOT NULL REFERENCES eleves(id),
    qr_code_token   UUID NOT NULL,
    date_passage    DATE NOT NULL DEFAULT CURRENT_DATE,
    heure_passage   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resultat        enum_resultat_scan NOT NULL,
    agent_id        BIGINT REFERENCES utilisateurs(id),
    mode_hors_ligne BOOLEAN NOT NULL DEFAULT false,
    synchro_at      TIMESTAMP  -- NULL si enregistrement temps réel, sinon heure de synchro offline
);

CREATE INDEX idx_passages_eleve_date ON passages_refectoire(eleve_id, date_passage);
CREATE INDEX idx_passages_qr_token ON passages_refectoire(qr_code_token);
```

### 3.2 Contrats d'API REST — Scan

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/v1/scan/valider` | Validation d'un QR Code (retourne résultat + infos élève) |
| `GET` | `/api/v1/scan/snapshot` | Téléchargement du snapshot chiffré pour mode offline |
| `POST` | `/api/v1/scan/sync` | Synchronisation des passages enregistrés offline |
| `GET` | `/api/v1/passages` | Journal des passages du jour (filtré par établissement) |

### 3.3 Règles Métier

- **Règle #301** : La validation d'un QR Code doit retourner un résultat en moins d'une seconde (cache Redis recommandé en production, ou index sur `qr_code_token`).
- **Règle #302 — Anti-passback** : Si l'élève a déjà un passage `AUTORISÉ` ou `GRACE` pour la date du jour, le résultat est `ANTI_PASSBACK` (pas de second accès autorisé pour le même service).
- **Règle #303 — Période de grâce** : Si `statut_acces = GRACE` et `date_fin_grace >= CURRENT_DATE`, le résultat est `GRACE` (passage visuel orange — autorisé avec avertissement).
- **Règle #304** : Le snapshot offline est chiffré (AES-256), contient uniquement les données minimales nécessaires (`qr_code_token`, `statut_acces`, `date_fin_grace`, `nom`, `photo_url`), et a un TTL de 24 heures.

### 3.4 Spécifications IHM — Application de Scan

- Écran principal : grand affichage du résultat (VERT / ROUGE / ORANGE) avec nom et photo de l'élève.
- Lecture QR Code via caméra du smartphone (WebRTC ou API camera Android).
- Indicateur de statut offline visible en permanence.
- Bouton "Mode Manuel" pour validation superviseur avec saisie de motif obligatoire.

---

## Module 4 : Table de Trace & Audit (ActionLog) — ADR-003

### 4.1 Modèle de Données (PostgreSQL)

```sql
CREATE TYPE enum_type_action AS ENUM ('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'SCAN', 'PAIEMENT');

CREATE TABLE action_log (
    id              BIGSERIAL PRIMARY KEY,
    utilisateur_id  BIGINT,                    -- NULL si action système (webhook)
    utilisateur_nom VARCHAR(150),              -- Snapshot du nom au moment de l'action
    type_action     enum_type_action NOT NULL,
    entite_cible    VARCHAR(100) NOT NULL,     -- ex: 'Eleve', 'TransactionPaiement'
    entite_id       BIGINT,
    payload_avant   JSONB,                     -- État avant modification
    payload_apres   JSONB,                     -- État après modification
    adresse_ip      VARCHAR(45),
    user_agent      VARCHAR(255),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_action_log_entite ON action_log(entite_cible, entite_id);
CREATE INDEX idx_action_log_utilisateur ON action_log(utilisateur_id);
CREATE INDEX idx_action_log_date ON action_log(created_at);
```

### 4.2 Implémentation Spring AOP

```java
@Aspect
@Component
public class AuditAspect {

    @Around("@annotation(com.cantine.audit.Auditable)")
    public Object logAction(ProceedingJoinPoint pjp) throws Throwable {
        // 1. Capturer l'état avant
        // 2. Exécuter la méthode cible
        // 3. Capturer l'état après
        // 4. Persister dans action_log (appel asynchrone @Async)
    }
}
```

- L'annotation `@Auditable` est posée sur les méthodes de `@Service` qui effectuent des écritures CUD.
- La persistance dans `action_log` est asynchrone (`@Async`) pour ne pas impacter les performances.
- La table `action_log` est en **écriture seule** pour les services métier. Aucun `UPDATE` ou `DELETE` n'est autorisé sur cette table.

---

## Module 5 : Gestion des Utilisateurs & Authentification

### 5.1 Modèle de Données (PostgreSQL)

```sql
CREATE TYPE enum_role AS ENUM ('SUPER_ADMIN', 'GESTIONNAIRE', 'AGENT_SCAN', 'PARENT');

CREATE TABLE utilisateurs (
    id                  BIGSERIAL PRIMARY KEY,
    etablissement_id    BIGINT REFERENCES etablissements(id),  -- NULL pour SUPER_ADMIN
    email               VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe_hash   VARCHAR(255) NOT NULL,
    nom                 VARCHAR(100) NOT NULL,
    prenom              VARCHAR(100) NOT NULL,
    telephone           VARCHAR(20),
    role                enum_role NOT NULL,
    actif               BOOLEAN NOT NULL DEFAULT true,
    derniere_connexion  TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 5.2 Contrats d'API REST — Auth

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/v1/auth/login` | Authentification — retourne JWT |
| `POST` | `/api/v1/auth/logout` | Invalidation du token côté client |
| `POST` | `/api/v1/auth/refresh` | Renouvellement du JWT |
| `GET` | `/api/v1/auth/me` | Profil de l'utilisateur connecté |

### 5.3 Règles Métier

- **Règle #501** : Le JWT contient : `userId`, `role`, `etablissementId`, `exp`. Durée de validité : 24h.
- **Règle #502** : Un `GESTIONNAIRE` sans `etablissement_id` dans son token ne peut accéder à aucune ressource protégée.
- **Règle #503** : Les mots de passe sont hashés avec BCrypt (strength 12). Aucun mot de passe en clair ne transite ni n'est stocké.

---

## Matrice des Actions CUD & Traçabilité

| Action | Entité | Acteur autorisé | ActionLog |
|--------|--------|-----------------|-----------|
| Créer élève | `Eleve` | SUPER_ADMIN, GESTIONNAIRE | ✅ |
| Modifier élève | `Eleve` | SUPER_ADMIN, GESTIONNAIRE | ✅ |
| Supprimer élève (logique) | `Eleve` | SUPER_ADMIN | ✅ |
| Modifier statut d'accès | `Eleve` | SUPER_ADMIN, GESTIONNAIRE, Webhook | ✅ |
| Régénérer QR Code | `Eleve` | SUPER_ADMIN, GESTIONNAIRE | ✅ |
| Initier paiement | `TransactionPaiement` | PARENT, GESTIONNAIRE | ✅ |
| Valider paiement (webhook) | `TransactionPaiement` | Système | ✅ |
| Valider passage scan | `PassageRefectoire` | AGENT_SCAN | ✅ |
