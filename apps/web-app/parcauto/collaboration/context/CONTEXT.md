# Projet : Gestion de Parc Auto - KLEM Technologies

# Contexte Métier et Vision Produit 

## 🎯 Objectif Général
Développer une solution d'entreprise de classe "Enterprise Resource Planning" (ERP) robuste, modulaire et scalable pour centraliser, automatiser et optimiser la gestion opérationnelle, logistique et financière d'un parc automobile multi-usages (courses urbaines, locations de courte/longue durée, livraisons de marchandises).

## Périmètre Fonctionnel (Les 6 Piliers)
1. **Gestion du Parc & Suivi** : Fiche d'identité des véhicules (Immatriculation, carte grise, assurance, vignette, statut en temps réel : Disponible, En Course, Loué, En Maintenance).
2. **Courses & VTC** : Planification et attribution des trajets pour les chauffeurs, suivi du statut de la course.
3. **Livraisons** : Suivi des colis, affectation aux véhicules utilitaires, validation de la livraison.
4. **Locations** : Gestion des contrats clients, caution, états des lieux (départ/retour), tarification dynamique.
5. **Maintenance** : Alertes automatiques (vidange, pneumatiques, contrôles techniques) basées sur le kilométrage et calendrier. Gestion des pannes et coûts de réparation.
6. **Revenus & Paiements** : Suivi des encaissements (espèces, virements, Mobile Money), édition des factures, calcul de la rentabilité par véhicule.

## 👥 Acteurs du Système & Matrice des Droits

* **Super Admin / Propriétaire (Klem Management)** : Accès absolu. Vision globale des revenus consolidés, audits financiers, rentabilité par véhicule, arbitrages RH (salaires, blocage de comptes) et configuration des taux de commission.
* **Gestionnaire de Flotte (Back-Office)** : Responsable opérationnel ou Opérateur quotidien. Gère l'inventaire des véhicules, planifie les maintenances, affecte les chauffeurs, valide les contrats de location, traite les alertes et supervise les flux de courses/livraisons.
* **Chauffeurs / Livreurs (Mobile-First UI)** : Interface terrain épurée. Visualisent leur planning, acceptent/clôturent les courses ou livraisons, déclarent les incidents, effectuent les états des lieux numériques et suivent en temps réel l'historique de leurs commissions acquises.


## 🏗️ Le Périmètre Fonctionnel (Les 6 Piliers)

### Pilier 1 : Gestion du Parc, Cycle de Vie & Traçabilité
Ce module régit l'existence physique et administrative de chaque composant de la flotte.
* **Fiche d'identité Digitale** : Numéro de châssis, immatriculation, marque, modèle, type de carburant, kilométrage initial et actuel (compteur).
* **Gouvernance Administrative** : Suivi des dates d'expiration et alertes de renouvellement pour : Assurance, Contrôle Technique, Vignette, Carte Grise.
* **Machine d'États du Véhicule** : Un véhicule doit obligatoirement répondre à un et un seul statut à l'instant T :
  `DISPONIBLE` ➔ `EN_COURSE` ➔ `LOUE` ➔ `EN_MAINTENANCE` ➔ `REFORME` (Hors service définitif).

### Pilier 2 : Courses, VTC & Gestion des Chauffeurs
Gestion des flux de transport de personnes à la demande ou planifiés.
* **Dispatching Intelligent** : Affectation d'un chauffeur salarié à un véhicule disponible selon la proximité ou le planning.
* **Immutabilité des Gains à l'Acte** : Dès qu'une course passe au statut `TERMINE`, le système fige instantanément dans la table `courses` :
    * Le montant total payé par le client.
    * La commission exacte due au chauffeur (calculée selon son contrat à cet instant précis : pourcentage ou forfait fixe).
* **Portefeuille Chauffeur** : Écran mobile transparent permettant au chauffeur de suivre ses gains cumulés sur le mois en cours.

### Pilier 3 : Logistique & Livraisons
Gestion de la chaîne de transport de marchandises et de colis.
* **Suivi des Colis** : Numéro de suivi unique, nature du colis (standard, fragile, périssable), expéditeur et destinataire.
* **Cycle de Livraison** : Prise en charge ➔ En cours de routage ➔ Tentative de livraison ➔ Livré (avec preuve numérique : signature client ou photo du dépôt).
* **Optimisation de Capacité** : Assignation des colis aux véhicules utilitaires selon leur volume et leur zone de destination.

### Pilier 4 : Locations de Courte & Longue Durée
Gestion des contrats clients tiers (particuliers ou entreprises).
* **Grille Tarifaire Dynamique** : Calcul automatique du coût selon la durée, la catégorie du véhicule et les options (avec/sans chauffeur Klem).
* **Sécurisation des Cautions** : Enregistrement du mode de garantie (Espèces encaissées, chèque bloqué ou empreinte monétique). Traitement des retenues financières sur caution en cas de dégradation constatée au retour.
* **États des Lieux Numériques (Checkout/Checkin)** : Formulaire obligatoire au départ et au retour du véhicule (relevé du niveau de carburant, kilométrage, et photos des 4 faces du véhicule).

### Pilier 5 : Maintenance Préventive & Corrective
Garantie de la sécurité des agents et de la longévité des actifs de Klem.
* **Maintenance Préventive (Automatique)** : Alertes programmées basées sur un double déclencheur : Calendrier (ex: tous les 6 mois) OU Kilométrage (ex: vidange tous les 5 000 km).
* **Maintenance Corrective (Pannes)** : Saisie des rapports d'incidents par les chauffeurs ➔ Immobilisation immédiate du véhicule ➔ Saisie de la facture du garage et des pièces changées pour alimenter le coût total de possession (TCO).

### Pilier 6 : Finances, Paiements & Paie
Le poumon économique de la plateforme.
* **Omnicanalité des Encaissements** : Passerelle de paiement intégrée supportant les Espèces, les Virements bancaires et le Mobile Money (Orange, MTN, Wave).
* **Rapports de Rentabilité** : Calcul en temps réel du ratio `Revenus (Location + Courses + Livraisons) - Coûts (Maintenance + Carburant + Commission Chauffeur) = Marge Nette par Véhicule`.
* **Moteur de Paie Salariale** : Génération automatique de l'état pré-comptable mensuel :
  `Salaire Brut = Salaire de Base + Somme(Commissions des courses validées) - Avances déduites`

---

## 🔄 Les Grandes Lignes de Gestion (Workflows Opérationnels)

Pour guider les développeurs sur les parcours utilisateurs (User Journeys), l'application s'articule autour de 4 grands cycles de vie :

### A. Le Cycle d'une Course / Livraison

[CRÉATION] ➔ Saisie de la demande (Client/Destination/Tarif)
   │
[AFFECTATION] ➔ Choix du couple Véhicule/Chauffeur ➔ Statut véhicule devient 'EN_COURSE'
   │
[EN COURS] ➔ Le chauffeur démarre le trajet (Saisie kilométrage de départ)
   │
[CLÔTURE] ➔ Arrivée à destination ➔ Paiement validé
   │
[CALCUL PAIE] ➔ Calcul immédiat et gel de la commission ➔ Statut véhicule repasse à 'DISPONIBLE'


### B. Le Cycle d'un Contrat de Location

[RÉSERVATION] Saisie des dates, choix du véhicule, calcul du devis.
   │
[VALIDATION] Paiement de l'acompte + Enregistrement de la caution.
   │
[CHECKOUT] État des lieux de départ (Compteur, Carburant, Photos) ➔ Statut véhicule devient 'LOUE'.
   │
[CHECKIN] Retour du véhicule ➔ Constat des écarts (Carburant manquant, rayures).
   │
[CLÔTURE] Restitution (totale ou partielle) de la caution ➔ Facturation finale ➔ Statut véhicule devient 'DISPONIBLE'.


### C. Le Cycle de la Maintenance

[ALERTE/PANNE] Déclenchement automatique par kilométrage OU signalement d'anomalie par un chauffeur.
   │
[IMMOBILISATION] Le véhicule passe au statut 'EN_MAINTENANCE' (Indisponible pour les courses et locations).
   │
[RÉPARATION] Saisie de l'atelier, description des travaux et enregistrement des coûts (factures).
   │
[SORTIE] Validation technique par le Gestionnaire ➔ Saisie du nouveau kilométrage de référence ➔ Retour au statut 'DISPONIBLE'.

### D. Le Cycle Mensuel de Clôture RH & Paie

[JOUR 1 À 30] Cumul des commissions au fil de l'eau + Enregistrement des demandes d'avances sur salaire.
   │
[JOUR 25] Saisie des acomptes ou blocage de salaire si litige en cours.
   │
[FIN DE MOIS] Génération automatique de la fiche de paie consolidée (Salaire de base + Commissions - Avances).
   │
[PAIEMENT] Validation du virement ou transfert Mobile Money de masse ➔ Remise à zéro du compteur mensuel de commissions.

