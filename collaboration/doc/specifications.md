# Spécifications Fonctionnelles et Opérationnelles - Parcauto

## 📌 Introduction & Architecture Générale
Ce document décrit de manière exhaustive les spécifications techniques et fonctionnelles pour l'implémentation modulaire de l'application FleetControl de Klem Technologies & Services. 

### Principes Directeurs pour l'IA (Claude Code)
1. **Implémentation incrémentale** : Codez une seule fonctionnalité à la fois, en complétant entièrement la couche Base de données ➔ Entités/Repositories ➔ Services métiers ➔ Contrôleurs REST ➔ Hooks Frontend ➔ Composants IHM avant de passer à la suivante.
2. **Gestion des erreurs** : Chaque écriture en base de données doit être sécurisée. Les exceptions métiers doivent être interceptées par un handler global et renvoyées sous le format standard suivant :
 json
   {
     "timestamp": "2026-06-21T23:00:00Z",
     "status": 400,
     "error": "BAD_REQUEST",
     "message": "Le véhicule est déjà affecté à une course active.",
     "path": "/api/v1/courses"
   }
   
   Zéro types any : Côté frontend, chaque payload d'API doit posséder son interface TypeScript stricte.

## Module 1 : Gestion du Parc, Cycle de Vie & Traçabilité (Pilier 1)
### 1.1 Modèle de Données (PostgreSQL)

CREATE TYPE enum_statut_vehicule AS ENUM ('DISPONIBLE', 'EN_COURSE', 'LOUE', 'EN_MAINTENANCE', 'REFORME');
CREATE TYPE enum_type_carburant AS ENUM ('ESSENCE', 'DIESEL', 'HYBRIDE', 'ELECTRIQUE');

CREATE TABLE vehicules (
    id SERIAL PRIMARY KEY,
    chassis VARCHAR(50) UNIQUE NOT NULL,
    immatriculation VARCHAR(20) UNIQUE NOT NULL,
    marque VARCHAR(50) NOT NULL,
    modele VARCHAR(50) NOT NULL,
    type_carburant enum_type_carburant NOT NULL,
    kilometrage_actuel INT NOT NULL DEFAULT 0,
    statut enum_statut_vehicule NOT NULL DEFAULT 'DISPONIBLE',
    date_expiration_assurance DATE NOT NULL,
    date_expiration_controle_technique DATE NOT NULL,
    date_expiration_vignette DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vehicules_immatriculation ON vehicules(immatriculation);
CREATE INDEX idx_vehicules_statut ON vehicules(statut);

### 1.2 Contrats d'API REST
- GET /api/v1/vehicules : Liste paginée avec filtres (statut, immatriculation).

- POST /api/v1/vehicules : Enregistrement d'un nouveau véhicule.

- PUT /api/v1/vehicules/{id}/statut : Transition manuelle ou automatique du statut.

### 1.3 Règles Métier & Machine d'États
- Règle #101 : Il est interdit de passer un véhicule au statut EN_COURSE ou LOUE si l'une de ses pièces administratives (assurance, contrôle technique, vignette) est expirée par rapport à la date du jour.

- Règle #102 : Le kilométrage actuel d'un véhicule ne peut jamais être mis à jour avec une valeur inférieure à sa valeur courante en base de données.

#### 1.4 Spécifications IHM (React & MUI)
- Vue Gestionnaire : Tableau de bord sous forme de grille MUI (Grid2). Chaque carte de véhicule affiche un badge de couleur MUI (success pour disponible, warning pour en course/loué, error pour maintenance).

- Composant Critique : Alerte de surbrillance rouge clignotante si une date d'échéance administrative est inférieure à J+15.

## 📁 Module 2 : Courses, VTC & Rémunération Chauffeur (Pilier 2)
### 2.1 Modèle de Données (PostgreSQL)

CREATE TYPE enum_statut_course AS ENUM ('EN_ATTENTE', 'EN_COURS', 'TERMINE', 'ANNULE');
CREATE TYPE enum_type_commission AS ENUM ('POURCENTAGE', 'FORFAIT_FIXE');

CREATE TABLE chauffeurs (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    salaire_base NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    type_commission enum_type_commission NOT NULL,
    valeur_commission NUMERIC(10, 2) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE courses (
    id SERIAL PRIMARY KEY,
    chauffeur_id INT REFERENCES chauffeurs(id) NOT NULL,
    vehicule_id INT REFERENCES vehicules(id) NOT NULL,
    client_nom VARCHAR(100) NOT NULL,
    client_telephone VARCHAR(20) NOT NULL,
    lieu_depart VARCHAR(255) NOT NULL,
    lieu_destination VARCHAR(255) NOT NULL,
    montant_client NUMERIC(12, 2) NOT NULL,
    commission_chauffeur_figee NUMERIC(12, 2) DEFAULT 0.00,
    statut enum_statut_course NOT NULL DEFAULT 'EN_ATTENTE',
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_courses_statut ON courses(statut);
CREATE INDEX idx_courses_chauffeur_mois ON courses(chauffeur_id, date_debut);

CREATE TYPE enum_statut_course AS ENUM ('EN_ATTENTE', 'EN_COURS', 'TERMINE', 'ANNULE');
CREATE TYPE enum_type_commission AS ENUM ('POURCENTAGE', 'FORFAIT_FIXE');

CREATE TABLE chauffeurs (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    salaire_base NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    type_commission enum_type_commission NOT NULL,
    valeur_commission NUMERIC(10, 2) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE courses (
    id SERIAL PRIMARY KEY,
    chauffeur_id INT REFERENCES chauffeurs(id) NOT NULL,
    vehicule_id INT REFERENCES vehicules(id) NOT NULL,
    client_nom VARCHAR(100) NOT NULL,
    client_telephone VARCHAR(20) NOT NULL,
    lieu_depart VARCHAR(255) NOT NULL,
    lieu_destination VARCHAR(255) NOT NULL,
    montant_client NUMERIC(12, 2) NOT NULL,
    commission_chauffeur_figee NUMERIC(12, 2) DEFAULT 0.00,
    statut enum_statut_course NOT NULL DEFAULT 'EN_ATTENTE',
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_courses_statut ON courses(statut);
CREATE INDEX idx_courses_chauffeur_mois ON courses(chauffeur_id, date_debut);

### 2.2 Intelligence Métier & Workflow de Dispatch (Intégration ai.koog)
- Orchestration de course : Lors d'un appel à POST /api/v1/courses/auto-dispatch, le service communique avec l'agent local ai.koog pour analyser les positions des chauffeurs actifs, croiser la disponibilité des véhicules dans le Pilier 1, et attribuer automatiquement la mission.

- Loi d'Immutabilité Financière : Lors de la transition de statut vers TERMINE, le @Service Spring Boot doit appliquer l'algorithme suivant au sein de la même transaction de base de données :


if (chauffeur.getTypeCommission() == enum_type_commission.POURCENTAGE) {
        commission = montantClient.multiply(chauffeur.getValeurCommission()).divide(new BigDecimal("100"));
    } else {
        commission = chauffeur.getValeurCommission();
    }
    course.setCommissionChauffeurFigee(commission);
    course.setStatut(enum_statut_course.TERMINE);
    vehicule.setStatut(enum_statut_vehicule.DISPONIBLE);
    ```

### 2.3 Spécifications IHM (Chauffeur Mobile-First)
*   **Écran Portefeuille Mobile** : Composant MUI `<Card>` épuré affichant en gros caractères : 
    1. La somme totale des commissions acquises sur le mois civil courant.
    2. Le nombre de courses réalisées.
    3. Une liste déroulante infinie des courses passées avec affichage vert clair du gain par course.

---

## 📁 Module 3 : Logistique & Livraisons (Pilier 3)

### 3.1 Modèle de Données (PostgreSQL)
sql
CREATE TYPE enum_statut_livraison AS ENUM ('RAMASSE', 'EN_ROUTAGE', 'ECHEC', 'LIVRE');
CREATE TYPE enum_nature_colis AS ENUM ('STANDARD', 'FRAGILE', 'PERISSABLE');

CREATE TABLE livraisons (
    id SERIAL PRIMARY KEY,
    code_suivi VARCHAR(50) UNIQUE NOT NULL,
    vehicule_id INT REFERENCES vehicules(id),
    chauffeur_id INT REFERENCES chauffeurs(id),
    expediteur_details JSONB NOT NULL,
    destinataire_details JSONB NOT NULL,
    nature_colis enum_nature_colis NOT NULL DEFAULT 'STANDARD',
    statut enum_statut_livraison NOT NULL DEFAULT 'RAMASSE',
    preuve_livraison_url VARCHAR(255),
    note_incident TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_livraisons_code_suivi ON livraisons(code_suivi);

### 3.2 Règles Opérationnelles
Preuve de livraison obligatoire : Le passage au statut LIVRE est rejeté par l'API si le champ preuve_livraison_url est vide ou nul.

Notification d'échec : En cas de passage au statut ECHEC, le champ note_incident devient requis pour expliquer la cause (ex: Destinataire injoignable).

### 3.3 Interface Utilisateur (Livreur)
Bouton d'action proéminent MUI branché sur la caméra du smartphone pour téléverser instantanément la photo du colis livré ou capturer la signature tactile sur l'écran.

## 📁 Module 4 : Locations Courte & Longue Durée (Pilier 4)
### 4.1 Modèle de Données (PostgreSQL)

CREATE TYPE enum_statut_location AS ENUM ('RESERVE', 'EN_COURS', 'TERMINE', 'LITIGE');
CREATE TYPE enum_mode_caution AS ENUM ('ESPECES', 'CHEQUE', 'PRE_AUTORISATION_CARTE');

CREATE TABLE clients_locataires (
    id SERIAL PRIMARY KEY,
    raison_sociale_ou_nom VARCHAR(150) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    piece_identite_numero VARCHAR(50) NOT NULL
);

CREATE TABLE contrats_location (
    id SERIAL PRIMARY KEY,
    client_id INT REFERENCES clients_locataires(id) NOT NULL,
    vehicule_id INT REFERENCES vehicules(id) NOT NULL,
    date_depart TIMESTAMP NOT NULL,
    date_retour_prevue TIMESTAMP NOT NULL,
    date_retour_reelle TIMESTAMP,
    tarif_journalier NUMERIC(12, 2) NOT NULL,
    montant_total_prevu NUMERIC(12, 2) NOT NULL,
    mode_caution enum_mode_caution NOT NULL,
    montant_caution NUMERIC(12, 2) NOT NULL,
    caution_encassee BOOLEAN NOT NULL DEFAULT false,
    statut enum_statut_location NOT NULL DEFAULT 'RESERVE'
);

CREATE TABLE etats_des_lieux (
    id SERIAL PRIMARY KEY,
    contrat_id INT REFERENCES contrats_location(id) NOT NULL,
    type_etat VARCHAR(10) NOT NULL,
    kilometrage INT NOT NULL,
    niveau_carburant INT NOT NULL,
    photos_urls TEXT[],
    observations TEXT
);

### 4.2 Logique d'Affaires & Gestion Financière des Risques
Calcul de tarification : Le montant total est calculé côté backend lors de la réservation : Nombre de jours * tarif_journalier.

Algorithme de restitution de caution : Lors du Check-In (Retour), si des dégradations sont consignées ou s'il manque du carburant, le gestionnaire saisit un montant de pénalité. L'API calcule le remboursement : montant_restitue = montant_caution - penalites.

## 📁 Module 5 : Maintenance Préventive & Corrective (Pilier 5)
### 5.1 Modèle de Données (PostgreSQL)

CREATE TYPE enum_type_maintenance AS ENUM ('PREVENTIVE', 'CORRECTIVE');
CREATE TYPE enum_statut_ordre AS ENUM ('PLANIFIE', 'EN_COURS', 'REALISE');

CREATE TABLE ordres_maintenance (
    id SERIAL PRIMARY KEY,
    vehicule_id INT REFERENCES vehicules(id) NOT NULL,
    type_maintenance enum_type_maintenance NOT NULL,
    description_panne_ou_tache TEXT NOT NULL,
    kilometrage_declenchement INT NOT NULL,
    cout_reparation NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    garage_nom VARCHAR(150),
    statut enum_statut_ordre NOT NULL DEFAULT 'PLANIFIE',
    date_entree DATE NOT NULL,
    date_sortie DATE
);

### 5.2 Moteur de Planification (Scheduler Spring Boot)
Un composant Spring @Scheduled(cron = "0 0 1 * * ?") s'exécute chaque nuit à 1h du matin.

Il compare le kilometrage_actuel de chaque véhicule avec le dernier kilométrage de vidange enregistré. Si la différence est supérieure ou égale à 5 000 km, le système crée automatiquement un ordre de maintenance préventive au statut PLANIFIE et bascule le véhicule sur un avertissement visuel.

## 📁 Module 6 : Finances, Paiements & Paie Mensuelle (Pilier 6)
### 6.1 Modèle de Données (PostgreSQL)
CREATE TYPE enum_canal_paiement AS ENUM ('ESPECES', 'VIREMENT', 'ORANGE_MONEY', 'MTN_MOMO', 'WAVE');
CREATE TYPE enum_statut_paiement AS ENUM ('EN_ATTENTE', 'VALIDE', 'ECHEC');

CREATE TABLE transactions_financieres (
    id SERIAL PRIMARY KEY,
    reference_unique VARCHAR(100) UNIQUE NOT NULL,
    montant NUMERIC(12, 2) NOT NULL,
    canal enum_canal_paiement NOT NULL,
    statut enum_statut_paiement NOT NULL DEFAULT 'EN_ATTENTE',
    module_provenance VARCHAR(50) NOT NULL,
    id_source_provenance INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE avances_sur_salaire (
    id SERIAL PRIMARY KEY,
    chauffeur_id INT REFERENCES chauffeurs(id) NOT NULL,
    montant NUMERIC(12, 2) NOT NULL,
    date_octroi DATE NOT NULL,
    deduite BOOLEAN NOT NULL DEFAULT false
);

### 6.2 Moteur Comptable de Clôture de Paie
Le service de paie calcule le salaire brut mensuel via une procédure stockée ou un service transactionnel isolé exécutant l'équation métier obligatoire suivante :

Salaire Net À Verser = Salaire de Base + Somme(Commissions Figées) - Somme(Avances Non Déduites)

@Transactional
public FichePaieDTO genererClotureMensuelle(Long chauffeurId, YearMonth mois) {
    Chauffeur chauffeur = chauffeurRepository.findById(chauffeurId).orElseThrow();
    
    BigDecimal base = chauffeur.getSalaireBase();
    BigDecimal totalCommissions = courseRepository.sumCommissionsByChauffeurAndMonth(chauffeurId, mois.getStartDate(), mois.getEndDate());
    BigDecimal totalAvances = avanceRepository.sumAvancesNonDeduites(chauffeurId);
    
    BigDecimal net = base.add(totalCommissions).subtract(totalAvances);
    
    // Marquer les avances comme déduites pour éviter le double prélèvement le mois suivant
    avanceRepository.marquerCommeDeduites(chauffeurId);
    
    return new FichePaieDTO(chauffeurId, base, totalCommissions, totalAvances, net);
}

### 6.3 Rapports KPIs & Tableaux de Bord (Dashboard Admin)
Calcul du TCO (Total Cost of Ownership) par Véhicule : Une vue PostgreSQL matérialisée doit agréger : Somme de la maintenance + Coûts de carburant déclaré.

Marge Nette de Rentabilité : Affichage dynamique du ratio : (Revenus Encaissés - TCO - Commissions Chauffeurs) / Revenus Encaissés * 100.

## Sécurité, Routage & Layout Global
### 1. Cookies Étanches
L'authentification ne doit laisser filtrer aucun token dans le stockage local (localStorage). Le backend Spring Security dépose un cookie après validation des identifiants :

HttpOnly = true

Secure = true

SameSite = Strict

### 2. Comportement du Layout React
La Sidebar de navigation gauche (MUI <Drawer>) doit utiliser la propriété variant="persistent". Lors de son ouverture sur desktop, elle applique une marge dynamique au conteneur de contenu principal (margin-left fluide), repoussant les tableaux et formulaires sans jamais cacher ou couper le texte. Sur mobile, le tiroir passe en mode temporary et vient se superposer de façon classique pour préserver l'espace de saisie des chauffeurs.