PROPOSITION 
Solution Digitale de Gestion des Paiements
et Contrôle d'Accès Cantine Scolaire
Présentée à : CANTINE — Restauration scolaire multi-établissements

Date : Juin 2026/Proposition initiale

⚠ Document confidentiel — Usage exclusivement interne à CANTINE
 SOMMAIRE
1. Contexte et enjeux
2. Compréhension des besoins
3. Solution proposée — Architecture générale
4. Description détaillée des modules
5. Choix techniques et stratégiques
6. Planning de déploiement
7. Proposition financière
8. Conditions contractuelles et SLA
9. Avantages
 1. CONTEXTE ET ENJEUX
La Cantine opère dans un secteur en pleine transformation numérique. La gestion manuelle des paiements de cantine et l'absence de contrôle d'accès automatisé exposent votre organisation à des risques opérationnels, financiers et d'image.
Problème identifié	Impact actuel	Risque si non traité
Processus de paiement 100% manuel	Temps administratif élevé, erreurs de saisie	Pertes financières, litiges parents
Absence de traçabilité centralisée	Impossibilité d'audit fiable par élève	Non-conformité comptable
Aucun contrôle d'accès temps réel	Accès non autorisés à la cantine	Risque sanitaire, fraude
Gestion multi-sites non unifiée	Données fragmentées, pilotage difficile	Décisions mal informées
Communication parents réactive	Parents peu informés des impayés	Dégradation de la relation client

Notre solution répond à l'ensemble de ces enjeux par une plateforme digitale intégrée, conçue pour le contexte africain et adaptée à vos contraintes opérationnelles.
 2. COMPRÉHENSION DES BESOINS
Sur la base du briefing transmis par M. Sylla, j’ai identifié cinq axes de besoins structurants :

Axe de besoin	Ce que nous attendons
Digitalisation complète	Automatiser inscription, paiement et reporting sans rupture de service
Accessibilité parents	Interface web/mobile simple, disponible 24h/24, compatible Mobile Money
Sécurisation des accès	Contrôle physique en temps réel à l'entrée de chaque cantine
Pilotage centralisé	Tableau de bord unifié sur tous nos établissements
Évolutivité	Solution adaptable à la croissance du réseau CANTINE

Engagement à imposer : livrer une solution MVP opérationnelle en 7 semaines maximum, évolutive vers des fonctionnalités avancées selon notre rythme de croissance.
 3. SOLUTION PROPOSÉE AVEC ARCHITECTURE GÉNÉRALE
Nous proposons une plateforme unifiée articulée autour de trois composantes interdépendantes :

Module	Description synthétique	Utilisateurs cibles
Portail Parents	Inscription, paiement Mobile Money, historique, notifications	Parents d'élèves
Back-Office Administration	Gestion élèves, suivi paiements, statistiques, alertes	Équipe CANTINE
Contrôle d'Accès Cantine	Badge QR Code, scan smartphone, validation temps réel	Personnel de restauration
Moteur de Paiement	Agrégateur Mobile Money (CinetPay/PayDunya), sécurisation	Système (backend)
API & Intégrations	Connexion SI écoles, export comptable, extensions futures	Partenaires / IT CANTINE

3.1 Principes architecturaux
✓	Architecture cloud-native avec hébergement local/africain (De préférence Côte d'Ivoire)
✓	Application web responsive — aucune installation requise côté parents
✓	Base de données centralisée multi-établissements avec isolation par site
✓	Chiffrement des données en transit (TLS 1.3) et au repos (AES-256)
✓	API RESTful documentée pour intégrations futures avec les SI des écoles
✓	SLA garanti à 99,5% hors maintenance planifiée
 4. DESCRIPTION DÉTAILLÉE DES MODULES
4.1 Portail Parents (Web sécurisé — Mobile First)
Gestion du compte et des profils :
✓	Accès via lien sécurisé envoyé par la CANTINE ou l'établissement scolaire
✓	Authentification par email + mot de passe (avec option OTP SMS)
✓	Profil par enfant : nom, classe, établissement, allergies, régime alimentaire spécial
✓	Tableau de bord personnalisé : statut d'accès, solde, prochaine échéance
Paiement en ligne:
✓	Fréquences disponibles : journalière, trimestrielle, annuelle
✓	Mobile Money: Orange Money, MTN Mobile Money, Wave, Moov Money (tous opérateurs majeurs)
✓	Carte bancaire Visa/Mastercard via agrégateur sécurisé (frais : +1% à la charge du parent)
✓	Virement bancaire (confirmation manuelle par la CANTINE)
✓	Paiement en ligne 24h/24 — Confirmation instantanée par SMS et E-mail
Historique et justificatifs:
✓	Consultation de l'historique complet des paiements par enfant
✓	Téléchargement des reçus et factures au format PDF
✓	Rappels automatiques 7 jours, 3 jours et 1 jour avant l'échéance
✓	Notification immédiate en cas de suspension d'accès
4.2 Back-Office Administration (CANTINE)
Gestion des inscriptions et des élèves :
✓	Base de données centralisée de tous les élèves par établissement et par classe
✓	Import en masse via fichiers Excel/CSV (modèle fourni)
✓	Gestion des profils spéciaux : boursiers, régimes alimentaires, situations exceptionnelles
✓	Historique complet des modifications par administrateur
Suivi financier et reporting :
✓	Tableau de bord temps réel : inscrits, payants, en retard, suspendus
✓	Statistiques par établissement, par classe, par tranche d'âge, par période
✓	Alertes automatiques en cas de retard de paiement (configurable : J+1, J+3, J+7)
✓	Rapports financiers exportables : comptabilité, trésorerie, réconciliation
✓	Module de rapprochement bancaire automatique avec les agrégateurs de paiement
Communication avec les parents :
✓	Envoi de notifications push, SMS et email groupés ou ciblés
✓	Gestion de campagnes : rentrée scolaire, nouveaux tarifs, événements
✓	Modèles de messages personnalisables


4.3 Système de Contrôle d'Accès Cantine
Equipment et identification :
✓	Badge PVC personnalisé avec QR Code unique par élève (résistant, facilement remplaçable)
✓	Alternative : NFC si upgrade souhaité ultérieurement
✓	Application mobile Android pour le personnel de restauration (scan via smartphone)
Verification en temps réel:
✓	Scan du QR Code → vérification instantanée du statut de paiement en base
✓	Affichage visuel clair : VERT (accès autorisé) / ROUGE (accès refusé)
✓	Temps de validation < 1 seconde par élève
✓	Mode hors-ligne de secours : cache local de 24h mis à jour à chaque connexion
Gestion des exceptions:
✓	Procédure de grâce configurable : tolérance de X jours (recommandé : 7 jours) avant restriction
✓	Passage en mode manuel par le personnel (validation superviseur) en cas de litige
✓	Journalisation complète de tous les passages (heure, statut, opérateur)
✓	Gestion des cas particuliers : boursiers, élèves en période de grâce, invités journaliers
 5. CHOIX TECHNIQUES ET STRATÉGIQUES
Ces choix techniques ont été arrêtés pour optimiser le rapport coût/performance tout en garantissant la fiabilité sur le terrain ivoirien après échanges avec M. Sylla. 

Choix technique	Option retenue	Raison / Avantage
Identification élèves	QR Code (PVC)	Coût ~200× inférieur au NFC ; lecture fiable avec tout smartphone Android
Terminal de scan	Smartphone Android existant	Pas d'investissement en terminal dédié ; logiciel installable en 5 min
Plateforme côté parents	Web app responsive	Aucune installation ; compatible tous téléphones ; mise à jour transparente
Paiements	CinetPay / PayDunya	Couverture maximale opérateurs CI ; API robuste ; KYC intégré
Hébergement	Cloud local africain (AWS Lagos / Infomaniak)	Latence réduite ; conformité RGPD ; souveraineté des données 
Stack technique	Front-end(React ), backend (spring-boot), bd( PostgreSQL)	Robustesse prouvée ; open-source ; communauté locale disponible
Architecture	Microservices modulaires	Déploiement progressif ; scalabilité linéaire ; maintenance simplifiée
 6. PLANNING DE DÉPLOIEMENT PROPOSE 
Déploiement en 4 phases proposé sur 8 semaines pour une mise en production progressive et sécurisée. 

Phase	Durée	Livrables clés	Validation
Phase 1 — Cadrage & Architecture	1 semaine	Spécifications détaillées, maquettes UI, choix infra validés	Réunion de validation client
Phase 2 — Développement MVP	3 à 5 semaines	Portail parents, back-office, module contrôle d'accès, intégration paiements	Recette interne + UAT Cantine
Phase 3 — Déploiement pilote	1 semaine	Installation sur 1 établissement pilote, formation équipes, ajustements	Validation terrain Cantine
Phase 4 — Généralisation	1 semaine 	Déploiement tous établissements, formation étendue, go-live complet	Signature PV de recette

Mise en production du premier établissement pilote sous 8 semaines à compter de la signature du contrat avec le prestataire.
6.1 Livrables contractuels
✓	Documentation technique complète (architecture, API, base de données)
✓	Guide utilisateur parents (PDF illustré + vidéo tutoriel)
✓	Guide administrateur CANTINE (PDF + sessions de formation en présentiel)
✓	Guide opérateur cantine (guide de poche plastifié + formation sur site)
✓	Code source hébergé sur dépôt privé accessible à CANTINE. 
 7. PROPOSITION FINANCIÈRE
⚠ Tarification indicative — À affiner lors de la phase de cadrage selon le nombre d'établissements, d'élèves et les fonctionnalités retenues.
7.1 Investissement initial (One-shot)
Poste	Description	Coût estimé (FCFA HT)
Développement plateforme complète	Portail parents + back-office + contrôle d'accès + API paiements	
Conception UI/UX	Maquettes, design système, guide de style	Inclus
Intégration agrégateur de paiements	CinetPay ou PayDunya — paramétrage compte marchand	
Infrastructure initiale (12 mois)	Hébergement cloud, noms de domaine, SSL, monitoring	
Impression badges PVC QR Code	Prix unitaire ~1.000 FCFA (selon volume, ex. 600 élèves)	
Formation & déploiement	Formation admin, cantine, parents — déplacements inclus	
Documentation complète	Guides PDF, vidéos tutoriels, API docs	Inclus

Total investissement initial estimé : 1 500 000 – 4 000 000 FCFA HT
7.2 Redevances récurrentes (Abonnement annuel)
Poste	Description	Coût estimé (FCFA HT/an)
Maintenance & support	Corrections bugs, mises à jour de sécurité, hotline 5j/7	
Hébergement cloud	Renouvellement serveurs, bandes passantes, sauvegardes	
Évolutions fonctionnelles	1 à 2 nouvelles fonctionnalités majeures par an	Sur devis
Licences tiers (agrégateur, SMS)	Variable selon volume de transactions et SMS envoyés	À l'usage

Abonnement annuel estimé : 100 000 – 300 000 FCFA HT/an
7.3 Frais variables à l'usage
Service	Mode de facturation	Tarif indicatif
Transactions Mobile Money	% prélevé sur chaque transaction parent	1% (supporté parent) + 0,5% (Cantine)
SMS notifications	À l'unité	~25 FCFA / SMS
Badges PVC supplémentaires	À l'unité (remplacement)	500 – 1 000 FCFA / badge
Modules complémentaires	Sur devis selon développement	Variable
7.4 Estimation ROI — Retour sur investissement
Sur la base d'un réseau de 5 établissements et 600 élèves inscrits (hypothèse de travail) :

Indicateur	Avant digitalisation	Après digitalisation	Gain estimé
Temps admin. paiements / mois	~40 heures	~5 heures	−87,5%
Taux de recouvrement	~70%	~92%	+22 pts
Accès non autorisés / mois	~15–20 cas	~0–2 cas	−95%
Retards de paiement > 30j	~25% des élèves	~8% des élèves	−68%
Coût de gestion / élève / an	~12 000 FCFA	~4 500 FCFA	−62,5%
 8. CONDITIONS CONTRACTUELLES ET SLA
8.1 Engagements de service (SLA)
Critère	Engagement
Disponibilité plateforme	99,5% minimum hors maintenance planifiée
Temps de réponse API paiements	< 2 secondes (P95)
Temps de validation cantine	< 1 seconde par élève
Délai réponse support (incidents P1)	< 2 heures ouvrables
Délai réponse support (incidents P2)	< 8 heures ouvrables
Sauvegarde des données	Backup automatique toutes les 24h, rétention 30 jours
Maintenance planifiée	Notifiée 72h à l'avance, hors heures scolaires
8.2 Protection des données (Chapitre élaboré avec le concours de M. Sylla)
✓	Conformité RGPD et législation locale sur la protection des données des mineurs
✓	Données hébergées sur territoire africain (En Côte d’Ivoire ou région Afrique sub-saharienne)
✓	Accès aux données personnelles strictement limité aux administrateurs de la CANTINE habilités
✓	Droit à l'effacement et à la portabilité des données sur demande
✓	Audit de sécurité annuel et rapport de conformité fourni à CANTINE
8.3 Propriété intellectuelle (Chapitre élaboré avec le concours de M. Sylla)
✓	Le code source développé spécifiquement pour CANTINE est la propriété de la CANTINE
✓	Les composants génériques (framework, bibliothèques) restent sous licences open-source respectives
✓	Dépôt du code source sur référentiel privé accessible en permanence à CANTINE
8.4 Garanties et pénalités (Chapitre élaboré avec le concours de M. Sylla)
✓	Garantie de 3 mois post-recette : correction des anomalies sans surcoût
✓	Pénalités contractuelles en cas de non-respect du SLA de disponibilité (à négocier)
✓	Clause de réversibilité : export complet des données dans un format standard (JSON/CSV) à tout moment
 9. AVANTAGES


Avantage différenciant	Ce que cela signifie pour Cantine
Expertise marché local	Solution pensée pour l'Afrique de l'Ouest : Mobile Money natif, connectivité dégradée gérée, langues locales
Architecture MVP évolutive	Vous démarrez avec l'essentiel et ajoutez des fonctionnalités sans réécriture
Coût total maîtrisé	QR Code vs NFC : économie de 80% sur l'équipement ; smartphones existants valorisés
Déploiement rapide	Pilote opérationnel en 6 semaines ; pas de tunnel de 8 mois, au Maxi 10 semaines
Support dédié	Interlocuteur unique, disponible en français, connaissance de votre contexte
Propriété des données	Vous êtes propriétaire de vos données et de votre code source

Recommandation : Proposer d’organiser une Réunion de cadrage (1h) pour affiner le périmètre, valider le nombre d'établissements et d'élèves, et présenter une offre ferme sous 5 jours ouvrables.


Fait à Abidjan, Juin 2026
