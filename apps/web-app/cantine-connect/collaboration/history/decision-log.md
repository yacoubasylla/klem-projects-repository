# DECISION-LOG.md — Registre des Décisions Architecturales (Cantine Connect)

> Ce registre liste les choix technologiques et méthodologiques structurants arbitrés sur le projet.
> Chaque décision majeure dispose d'un ADR détaillé dans `collaboration/history/adr/`.

---

### ADR-001 · Choix de la Stack Technique Front-end (React + Vite + MUI)
- **Statut** : Accepté — 2026-06-20
- **Décision** : React.js 18 propulsé par Vite, avec Material UI v9 comme design system unique.
- **Contexte** : Interface web hautement responsive pour agents sur site (écrans compacts 15 pouces). Interdiction de CSS externe — uniquement `sx={{ }}` MUI.
- **Alternatives rejetées** : Next.js (SSR inutile pour un dashboard SPA interne), Tailwind CSS (non cohérent avec le design system MUI).
- **Fichier ADR** : `adr/2026-06-30-stack-technique-frontend.md`

---

### ADR-002 · Formulaire Élèves en 3 Onglets MUI (Contrainte Ergonomique)
- **Statut** : Accepté — 2026-06-30
- **Décision** : Composant `Tabs`/`TabPanel` MUI divisant les champs en 3 volets : Général / Cantine+Affectation / Contacts+Allergies.
- **Contexte** : Contrainte explicite du cahier des charges — zéro scroll vertical sur les formulaires. Les gestionnaires saisissent jusqu'à 50 élèves par session sur des moniteurs 15 pouces.
- **Conséquences** : La validation doit indiquer l'onglet concerné par l'erreur (feedback UX obligatoire).

---

### ADR-003 · Traçabilité via Table `action_logs` Alimentée par Spring AOP
- **Statut** : Accepté — 2026-06-30 | Implémentation prévue : B-05
- **Décision** : Aspect Spring AOP (`@Around`) sur les méthodes de service annotées, alimentant la table `action_logs` de façon asynchrone.
- **Contexte** : Exigence de conformité du cahier des charges. Toute opération d'écriture (CREATE/UPDATE/DELETE) doit être tracée avec auteur, horodatage et payload avant/après.
- **Alternative rejetée** : Logging fichiers — non structuré, non interrogeable SQL.
- **Fichier ADR** : À créer lors de l'implémentation de B-05.

---

### ADR-004 · Authentification JWT Stateless (Spring Security 6 + jjwt 0.12.3)
- **Statut** : Accepté — 2026-06-30
- **Décision** : JWT stateless HMAC-SHA512 via en-tête `Authorization: Bearer`. Pas de session HTTP serveur.
- **Contexte** : Architecture multi-établissements avec potentiel scaling horizontal. Les sessions serveur compliqueraient le déploiement multi-instances.
- **Alternative rejetée** : Sessions HTTP cookies — incompatibles avec le scaling horizontal et l'architecture mobile-first.
- **Fichier ADR** : `adr/2026-06-30-jwt-stateless-authentication.md`

---

### ADR-005 · Extraction PasswordEncoder dans PasswordEncoderConfig (Anti-Circular-Dependency)
- **Statut** : Accepté — 2026-06-30
- **Décision** : Bean `PasswordEncoder` dans une classe `PasswordEncoderConfig.java` indépendante, sans dépendances Spring Security.
- **Contexte** : Dépendance circulaire au démarrage — `SecurityConfig` → `JwtAuthFilter` → `AuthService` → `PasswordEncoder` ← `SecurityConfig`. Spring Boot refuse de démarrer.
- **Alternative rejetée** : `@Lazy` sur le bean — masque le problème sans le résoudre proprement.
- **Fichier ADR** : `adr/2026-06-30-resolution-dependance-circulaire.md`

---

### ADR-006 · Port 8081 pour Spring Boot (Conflit Port 8080)
- **Statut** : Accepté — 2026-06-30
- **Décision** : Spring Boot écoute sur le port `8081` en développement local.
- **Contexte** : Port 8080 occupé sur la machine de développement de M. Sylla. La configuration est dans `application.yml` (`server.port: 8081`).
- **Impact** : Le client Axios pointe sur `http://localhost:8081/api/v1`. À documenter dans le README de l'équipe.

---

### ADR-007 · Requête Native PostgreSQL pour Bug Hibernate 6 + JPQL Nullable
- **Statut** : Accepté — 2026-06-30
- **Décision** : `@Query(value="...", nativeQuery=true)` avec `CAST(:param AS varchar)` pour les filtres optionnels (nullables) de la liste des élèves.
- **Contexte** : Hibernate 6 + driver PostgreSQL échouent à inférer le type SQL d'un paramètre JPQL `null`, générant l'erreur `operator does not exist: lower(bytea)`. Bug connu Hibernate 6.
- **Alternative rejetée** : Specification API JPA — overhead architectural disproportionné pour 3-4 filtres simples.

---

### ADR-008 · Stratégie de Déploiement Production : Vercel (Frontend) + Railway (Backend)
- **Statut** : Accepté — 2026-07-01
- **Décision** : Déploiement découplé — frontend React/Vite sur Vercel, backend Spring Boot + PostgreSQL sur Railway.
- **Contexte** : Architecture SPA (Single Page App) + API REST. Vercel est optimisé pour les assets statiques avec CDN global. Railway simplifie le déploiement Docker + base de données managée sans gestion d'infrastructure.
- **Configurations clés** :
  - `client-frontend/vercel.json` — rewrites `/(.*) → /index.html` pour React Router
  - `server-backend/Dockerfile` — multi-stage `eclipse-temurin:17-jdk-alpine` → `eclipse-temurin:17-jre-alpine`
  - `server-backend/railway.toml` — `healthcheckPath: /actuator/health`, `healthcheckTimeout: 120`
  - `server.port: ${PORT:8081}` — Railway injecte `$PORT` dynamiquement
- **Alternatives rejetées** : Render.com (cold start 30s sur tier gratuit), Fly.io (complexité réseau VPN), Heroku (coût).
- **Fichier ADR** : `adr/2026-07-01-strategie-deploiement-production.md`

---

### ADR-010 · JPA Specifications (Criteria API) pour les Filtres Dynamiques Multi-Critères
- **Statut** : Accepté — 2026-07-01 (révision ADR-007)
- **Décision** : Utiliser `JpaSpecificationExecutor` + classe `PassageSpecification` (Criteria API) pour la requête de l'historique des passages, au lieu d'un `@Query` JPQL avec paramètres optionnels.
- **Contexte** : ADR-007 avait rejeté les Specifications "overhead disproportionné pour 3-4 filtres simples". Ce jugement s'appliquait aux élèves (4 filtres, requête native possible). Pour les passages, le JPQL avec `(:param IS NULL OR ...)` a généré des 500 persistants en production : Hibernate 6 ne résout pas fiablement les types null pour les enums (`ResultatScan`) et les combinaisons LIKE+OR dans le pattern `(:search IS NULL OR LOWER(e.nom) LIKE ...)`. Deux versions corrigées ont échoué successivement (double ORDER BY, JOIN FETCH pagination). La Criteria API, elle, ajoute chaque prédicat conditionnellement en Java — Hibernate ne reçoit jamais de paramètre null ambigu.
- **Règle résultante** : Utiliser `@Query` JPQL pour les requêtes à paramètres fixes ou peu optionnels. Utiliser `JpaSpecificationExecutor` + Specification dès qu'une requête a ≥ 3 filtres optionnels ou implique des enums/types complexes nullables.
- **Conséquences** : `PassageRefectoireRepository` étend `JpaSpecificationExecutor<PassageRefectoire>`. Le sort Pageable est résolu nativement sans conflit ORDER BY. Performance identique — le SQL généré par Criteria est équivalent au JPQL corrigé.

---

### ADR-009 · Construction Manuelle de l'URL JDBC sur Railway (Incompatibilité DATABASE_URL)
- **Statut** : Accepté — 2026-07-01
- **Décision** : Construire `SPRING_DATASOURCE_URL` manuellement via les variables atomiques du plugin PostgreSQL Railway : `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}`
- **Contexte** : Railway fournit `${{Postgres.DATABASE_URL}}` au format `postgresql://...` (standard PostgreSQL). Spring Boot / HikariCP exige le format JDBC : `jdbc:postgresql://...`. L'erreur `IllegalArgumentException: URL must start with 'jdbc'` se produit au démarrage si on utilise `DATABASE_URL` directement. Railway ne fournit pas de variable `JDBC_URL` prête à l'emploi.
- **Conséquence** : `SPRING_DATASOURCE_USERNAME` = `${{Postgres.PGUSER}}` et `SPRING_DATASOURCE_PASSWORD` = `${{Postgres.PGPASSWORD}}` doivent être définis séparément.

---

### ADR-011 · RBAC Serveur pour le Rôle PARENT (Périmètre Restreint aux Enfants)
- **Statut** : Accepté — 2026-07-01
- **Décision** : Restriction appliquée côté serveur (pas seulement UI) — `EleveController`, `EtablissementController` et `ScanController.scanner()`/`.cache()` bloqués pour PARENT (`@PreAuthorize("!hasRole('PARENT')")`) ; `PaiementService` et `ScanService.listerPassages` filtrent/rejettent selon les enfants réels du parent (`ParentRepository.findEnfantIdsByUtilisateurId()`).
- **Contexte** : Sans restriction serveur, un compte PARENT authentifié pouvait interroger les données de tous les élèves, pas seulement les siens — fuite de données personnelles entre familles.
- **Alternative rejetée** : Filtrage uniquement côté frontend (masquage de menus) — aucune garantie de sécurité réelle face à un appel API direct.
- **Fichier ADR** : `adr/2026-07-01-rbac-parent-restriction-serveur.md`

---

### ADR-012 · Migrations Flyway comme Source Unique des Comptes de Seed
- **Statut** : Accepté — 2026-07-01
- **Décision** : Suppression de `DataInitializer.java` (ApplicationRunner). Les comptes de seed (un par rôle) sont désormais définis exclusivement par la migration `V6__reset_comptes_un_par_role.sql`.
- **Contexte** : Le champ `telephone` obligatoire/unique (V5) et la demande de réinitialisation des comptes (un par rôle) rendaient `DataInitializer` incompatible (pas de téléphone renseigné) et son garde-fou `count()==0` définitivement mort une fois V6 appliquée.
- **Alternative rejetée** : Mettre à jour `DataInitializer` en parallèle de la migration — risque de divergence silencieuse entre deux mécanismes de seed pour un code qui ne s'exécuterait de toute façon plus jamais.
- **Fichier ADR** : `adr/2026-07-01-migrations-source-unique-comptes-seed.md`

---

### ADR-013 · Incident Production — Récidive du Bug JPQL Nullable + LIKE (ADR-007) sur Paiements/Utilisateurs
- **Statut** : Accepté — 2026-07-01
- **Décision** : Conversion des requêtes de recherche `UtilisateurRepository`/`TransactionPaiementRepository` en `@Query(nativeQuery = true)` + `CAST(:param AS ...)`, comme déjà établi par l'ADR-007. Correction en cascade de `statsAcceptesPeriode` (`Object[]` → `List<Object[]>`, `ClassCastException` dans `DashboardService`) et d'un double `ORDER BY` introduit par le `Sort` du `Pageable` sur les nouvelles requêtes natives. `GlobalExceptionHandler.handleGeneric` journalise désormais la stack trace complète.
- **Contexte** : Les endpoints `GET /api/v1/utilisateurs`, `GET /api/v1/paiements` et `GET /api/v1/dashboard/stats` renvoyaient 500 en production — écran Utilisateurs vide alors que les 4 comptes de la V6 existaient réellement en base. La règle de l'ADR-007 (CAST explicite pour paramètre JPQL nullable) n'avait pas été appliquée aux nouvelles requêtes de recherche ajoutées dans la session précédente.
- **Leçon retenue** : tout test manuel d'un endpoint de recherche doit couvrir explicitement le cas sans filtre (`search=null`), pas seulement le cas filtré — c'est le chemin le plus emprunté et celui qui a échappé à la vérification précédente.
- **Fichier ADR** : `adr/2026-07-01-incident-jpql-null-bytea-paiements-utilisateurs.md`

---

### ADR-014 · Latence Production Anormale — Logging TRACE Hibernate Non Désactivé en Profil `prod`
- **Statut** : Accepté — 2026-07-01
- **Décision** : Ajout de `org.hibernate.orm.jdbc.bind: WARN` dans le bloc `logging.level` du profil `prod` de `application.yml`.
- **Contexte** : Le bloc de configuration de base active `org.hibernate.orm.jdbc.bind: TRACE` (utile en dev), mais le profil `prod` ne le surchargeait pas (il ne surchargeait que `com.klem.cantine` et `org.hibernate.SQL`). Résultat : chaque requête HTTP en production écrivait une ligne de log par paramètre SQL lié, sur un conteneur Railway aux ressources limitées — mesuré via `railway logs` + `curl` : `/actuator/health` (aucune logique métier) mettait jusqu'à 24s à répondre, `/dashboard/stats` jusqu'à 44s. Ce n'était donc pas un problème de requêtes non optimisées, mais un volume d'I/O de logging excessif dégradant tous les endpoints uniformément.
- **Alternative rejetée** : Retirer `TRACE` du bloc de base — resterait utile en dev local (a permis de diagnostiquer l'ADR-007/013) ; le vrai problème était l'absence de surcharge en prod, pas la présence en dev.
- **Fichier ADR** : `adr/2026-07-01-fix-latence-production-trace-logging.md`
- **Suite** : correctif nécessaire mais insuffisant — cause dominante identifiée et traitée dans l'ADR-015.

---

### ADR-015 · Latence Production (Suite ADR-014) — Dépassement Mémoire du Conteneur Railway
- **Statut** : Accepté — 2026-07-01
- **Décision** : Bornes mémoire explicites — `Dockerfile` : `-Xmx400m -Xms256m -XX:MaxMetaspaceSize=160m -Xss512k` (valeurs absolues — une première tentative avec `-XX:MaxRAMPercentage=60.0` mesurée après déploiement montrait toujours la mémoire max au-dessus de la limite, la détection cgroup s'étant révélée peu fiable sur ce conteneur) ; `application.yml` (profil `prod`) : `hikari.maximum-pool-size` 20→10, `spring.jpa.open-in-view: false`, `server.tomcat.threads.max: 50` (au lieu de 200 par défaut).
- **Contexte** : Après le correctif ADR-014, la latence restait sévère (12-42s). `railway metrics --json` a montré un CPU quasiment inutilisé (0%) mais une mémoire maximale (1099 Mo) dépassant la limite du conteneur (1024 Mo), avec des P50/P90/P95/P99 HTTP uniformément à ~13,9s — signature d'une pression mémoire extrême, pas d'un problème CPU ou de requêtes. Aucune borne explicite n'existait pour le tas JVM, le Metaspace (croissance non bornée par défaut) ou les piles de threads Tomcat (200 threads × ~1 Mo par défaut).
- **Alternative envisagée** : Augmenter le plan Railway — décision budgétaire de l'utilisateur, pas un correctif de code ; recommandée comme prochaine étape si la latence persiste après ce tuning.
- **Fichier ADR** : `adr/2026-07-01-fix-memoire-conteneur-railway.md`

---

### ADR-016 · Module Rapports — Génération Côté Navigateur, `exceljs` au lieu de `xlsx`/SheetJS
- **Statut** : Accepté — 2026-07-03
- **Décision** : Première version du module Rapports (GESTIONNAIRE/CAISSIER/ADMIN, exclu pour PARENT) générée entièrement côté navigateur — Excel via `exceljs`, PDF via `window.print()` scoppé à une zone imprimable. Aucun nouvel endpoint backend ; réutilisation de `GET /paiements` et `GET /passages` déjà sécurisés, avec pagination automatique côté client (garde-fou 10 000 lignes).
- **Contexte** : Aucune infrastructure PDF/Excel n'existait dans le projet. `xlsx` (SheetJS) — bibliothèque JS de référence pour Excel — n'a plus été patché sur le registre npm depuis la version `0.18.5`, qui contient deux vulnérabilités connues sans correctif disponible (Prototype Pollution, ReDoS) ; les versions corrigées de SheetJS ne sont distribuées que via leur propre CDN, hors npm.
- **Alternative rejetée** : `xlsx`/SheetJS — écarté uniquement pour la raison de sécurité ci-dessus, malgré sa popularité ; génération côté serveur (OpenPDF/Apache POI) — écartée pour cette v1 exploratoire, plus coûteuse à livrer et nécessitant de paramétrer en dates les agrégations aujourd'hui figées sur « aujourd'hui »/« ce mois » dans `DashboardService`.
- **Fichier ADR** : `adr/2026-07-03-module-rapports-generation-navigateur-exceljs.md`

---

### ADR-017 · Refonte Premium — Fondations + UI + Parcours Parent Self-Service (Phase 1 & 2)
- **Statut** : **Livré** — 2026-08-04 (commits `6305229`, `6d1f2c3`, `2f932b5` ; poussé sur `main`)
- **Décision** :
  1. Workflow de demande d'accès parent en file d'attente (`demandes_acces`, statut `EN_ATTENTE/VALIDEE/REJETEE`) plutôt qu'activation immédiate — le compte `Utilisateur`/`Parent` n'est créé qu'à la validation par un ADMIN. **Phase 2 livrée** : écran admin de validation/rejet (`/demandes-acces`), génération d'identifiants + mot de passe temporaire, notification (email/SMS selon coordonnées fournies), changement de mot de passe forcé à la première connexion (`Utilisateur.doitChangerMotDePasse`).
  2. Délai de grâce piloté par configuration globale (`DELAI_GRACE_JOURS_DEFAUT`) + surcharge optionnelle par établissement, plutôt qu'un champ unique codé en dur.
  3. Contrainte allergie ⇒ certificat médical appliquée en code service (`IllegalArgumentException` → 400), pas en contrainte SQL, pour un message métier explicite via `GlobalExceptionHandler`.
  4. Le mode `CREDITS` (portefeuille prépayé) est conservé en coexistence avec les nouveaux abonnements trimestriel/annuel — la règle « pas de mensualisation » s'applique uniquement au mode `ABONNEMENT`.
  5. Introduction d'interfaces `PaymentProvider` / `NotificationSender` pour découpler les rails de paiement/canaux de notification — **complétée en ADR-018** par une intégration réelle CinetPay/PayDunya/Twilio (au-delà des placeholders initialement prévus pour cette phase).
  6. **Ajout d'enfant self-service** : le parent authentifié ajoute lui-même ses enfants (`POST /parents/moi/enfants`) avec cascade établissement→niveau→classe ; les coordonnées parent (nom/téléphone/email) sont dérivées du compte connecté, jamais saisies dans le formulaire (évite qu'un parent renseigne les coordonnées d'un tiers).
  7. Thème visuel : nouveau thème par défaut « Premium » (chaleureux, orange/vert), avec réintroduction ultérieure du sélecteur multi-thèmes (Premium/Corporatif/Moderne) pour ne pas retirer une fonctionnalité appréciée — voir commit `c3ae1c9`.
- **Contexte** : refonte visuelle et fonctionnelle demandée pour rendre l'application plus premium/crédible pour le contexte scolaire ivoirien (page d'accueil, connexion, inscription parent) + règles métier manquantes (délai de grâce non paramétrable, allergies sans justificatif, recherche parent limitée à l'email, désormais étendue au téléphone).
- **Alternatives rejetées** :
  - Activation immédiate du compte parent à la soumission — rejetée : aucun contrôle admin avant l'accès, risque de faux comptes en contexte scolaire.
  - Remplacement total du mode `CREDITS` par l'abonnement — rejeté : fonctionnalité existante utilisée (repas ponctuels hors abonnement), aucune demande de suppression.
  - Email obligatoire pour tout parent — rejetée : le cahier des charges exige l'email facultatif ; un email synthétique (`p<telephone>@parent.cantine-connect.ci`) est généré comme identifiant de connexion interne lorsque le parent n'en fournit pas, sans jamais l'exposer comme un email réel ni tenter d'y envoyer de courrier.
- **Bugs découverts et corrigés pendant la vérification** :
  - `eleves.sexe` créé en `VARCHAR(1)` (migration V13) alors qu'Hibernate 6 valide le schéma en attendant `CHAR(1)` pour une colonne `@Column(length=1)` sur un enum — empêchait le démarrage du backend (`SchemaManagementException`). Corrigé par la migration `V14` (élargissement à `VARCHAR(10)`).
  - Endpoints `GET /etablissements` et `.../classes` portaient `@PreAuthorize("!hasRole('PARENT')")`, héritage de l'époque où seul le staff consultait ces données — bloquait en 403 la cascade établissement→classe du formulaire d'ajout d'enfant self-service. Restriction retirée sur les endpoints de lecture (écritures toujours réservées à l'ADMIN).
- **Fichier ADR** : voir `adr/` (à créer via `./scripts/create-adr.sh` si une fiche détaillée séparée est requise pour l'archivage formel).

### ADR-018 · Intégration réelle des paiements (CinetPay/PayDunya) et des notifications SMS (Twilio)
- **Statut** : Livré — 2026-08-03 (commit `bb887ab`)
- **Décision** :
  1. `CinetPayProvider`/`PayDunyaProvider` appellent réellement les API de checkout des agrégateurs (`POST /v2/payment`, `POST /checkout-invoice/create`) via `java.net.http.HttpClient`, au lieu de construire une URL de paiement à la main — retenu plutôt qu'une intégration directe Orange/MTN/Moov (contrats marchands séparés non disponibles à ce stade).
  2. Signature webhook PayDunya finalisée (comparaison `hash` reçu vs `sha512(clé privée)`), en complément de la vérification HMAC déjà en place pour CinetPay.
  3. SMS : fournisseur Twilio retenu pour démarrer (compte d'essai disponible, API REST simple), derrière l'interface `NotificationSender` déjà posée en ADR-017 — un fournisseur local ivoirien pourra être substitué sans impact sur le code appelant.
  4. Comportement de repli : en l'absence de clés réelles (CinetPay/PayDunya/Twilio), les appels échouent proprement (`409`/message clair côté paiement, bascule en mode journal côté SMS) plutôt que de faire planter l'application — vérifié en conditions réelles (aucune clé renseignée à ce stade).
- **Contexte** : demande explicite du client de disposer d'une intégration paiement et notification *effective*, au-delà de l'architecture préparée en ADR-017.
- **Alternatives rejetées** :
  - Attendre l'obtention de vraies clés avant de coder l'appel API réel — rejetée : le code doit être prêt à fonctionner dès que les identifiants sont fournis, sans nouveau cycle de développement.
  - SDK Twilio officiel — écarté au profit d'un appel HTTP direct (Basic Auth) pour rester cohérent avec l'approche déjà retenue pour CinetPay/PayDunya (pas de dépendance SDK supplémentaire) et garder l'abstraction facilement substituable.
- **Non inclus dans cette phase** : commissions de transaction CinetPay/PayDunya et frais de consommation SMS réels (contractualisés séparément par le client avec les fournisseurs — voir l'offre financière, section Exclusions).

---

### ADR-019 · Contrat de paiement unifié multi-providers (Strategy Pattern) et intégration Orange Money
- **Statut** : Accepté — 2026-08-18
- **Décision** : Nouveau contrat `PaymentStrategy` (`paiement.strategy`) — initiation, webhook, vérification de statut, validation de signature — additif à l'ancien `PaymentProvider` : `CinetPayProvider` implémente les deux, sans qu'aucune de ses méthodes existantes ne soit modifiée. `PaymentStrategyFactory` indexe les stratégies par `PaymentProviderType` (repli sur `klem.payment.default-provider`). Nouveau service façade `CanteenPaymentServiceImpl`/`CanteenPaymentController` sous `/api/v2/canteen-payments/**`, coexistant avec `/api/v1/paiements` et partageant la même table `transactions_paiement`. `OrangeMoneyPaymentStrategy` : intégration OAuth2 client_credentials réelle via `WebClient` (`spring-boot-starter-webflux`, utilisé uniquement comme client HTTP — l'app reste Spring MVC).
- **Contexte** : accès marchand Orange Money obtenu, nécessitant un contrat plus riche que `PaymentProvider` (webhook, statut, signature). Ajouter un fournisseur devient un ajout de classe pur (principe Ouvert/Fermé), sans modifier la factory ni le service façade.
- **Alternatives rejetées** :
  - Réécrire `CinetPayProvider`/`PayDunyaProvider` en `WebClient` pour homogénéiser — rejetée : intégrations de paiement réelles en production, réécriture sans nécessité fonctionnelle = risque de régression pour un gain cosmétique.
  - Remplacer `PaymentProvider` par `PaymentStrategy` (rename en masse) — rejetée, contraire à la règle de non-régression du `CLAUDE.md` racine (§2.4).
- **Fichier ADR** : `adr/2026-08-18-payment-strategy-multi-providers-orange-money.md`

---

### ADR-020 · Connexion parent par OTP (sans compte à la volée) et gestion complète des enfants
- **Statut** : Accepté — 2026-08-18
- **Décision** : L'OTP (WhatsApp/SMS/Email) authentifie un compte PARENT déjà approuvé (recherché par téléphone) — il ne crée jamais de compte ni d'enfant à la volée. Un numéro sans compte reçoit une erreur explicite invitant à soumettre la demande d'accès existante (`/demande-acces`, inchangée). Une fois vérifié, délivre le même jeton qu'une connexion par mot de passe, donnant accès aux endpoints parent déjà existants. Gestion des enfants étendue côté parent (`PUT`/`DELETE /parents/moi/enfants/{id}`, nouveaux) en réutilisant `EleveService`/`Parent` existants — vérification de propriété. Matricule (`E<ANNÉE><RANG>`) généré automatiquement à la création (upsert atomique PostgreSQL), immuable ensuite, saisie manuelle retirée.
- **Contexte** : mandat d'accès parent par OTP + gestion enfants ; le wizard OTP tel que décrit littéralement (numéro vérifié = accès direct) réintroduisait un risque de faux comptes déjà explicitement écarté par une décision antérieure (activation immédiate sans contrôle admin, rejetée pour risque de faux comptes en contexte scolaire).
- **Alternatives rejetées** :
  - OTP = accès instantané sans compte préalable (mandat initial) — rejetée, risque déjà écarté.
  - Nouveau module `Student` parallèle à `Eleve`/`Parent` — rejetée, aurait dupliqué le modèle de données et la logique métier déjà branchés sur `Eleve` (paiement, scan, notifications).
  - Redis pour le stockage OTP dès cette itération — écarté (topologie mono-instance actuelle), stockage en mémoire derrière l'interface `OtpStore`, remplaçable sans impact.
- **Fichier ADR** : `adr/2026-08-18-connexion-parent-otp-gestion-enfants.md`

---

### ADR-021 · L'accès OTP remplace le formulaire "Demande d'accès" avec validation admin
- **Statut** : Accepté — 2026-08-18
- **Décision** : Instruction explicite et directe du porteur du projet ("pour des questions de facilité") — le formulaire public "Demande d'accès" (`/demande-acces`, ex-stepper Identité/Contact/Résidence) est remplacé par le flux OTP à deux pages : numéro WhatsApp + email → code → vérification, qui crée le compte PARENT à la volée si ce numéro n'en avait pas encore (aucun nom/prénom collecté, valeurs génériques) puis affiche directement la gestion des enfants. Remplace explicitement le contrôle admin préalable acté en ADR-020 (même journée) et dans une décision antérieure.
- **Contexte** : capture d'écran fournie pointant explicitement `DemandeAccesPage.jsx` comme le formulaire à modifier, avec consigne de ne garder que numéro + email.
- **Alternatives rejetées** :
  - Garder le contrôle admin, juste ajouter le champ email au flux OTP existant — rejetée, contredit l'instruction reçue.
  - Conserver les deux formulaires en parallèle (`/demande-acces` avec validation ET `/acces-otp` sans) — rejetée, source de confusion et contredit l'instruction pointant explicitement `DemandeAccesPage.jsx`.
- **Risque assumé explicitement** : perte du contrôle anti-fraude admin (ADR-020/décision antérieure) — décision du porteur du projet, pas une régression silencieuse. Le back-office de validation (`DemandeAccesService`/`AccesController`/`DemandesAccesPage.jsx`) n'est pas supprimé mais devient orphelin (plus appelé par le frontend public).
- **Fichier ADR** : `adr/2026-08-18-otp-remplace-demande-acces-validation-admin.md`

---

### ADR-022 · Canal OTP parent paramétrable (WhatsApp par défaut, bascule SMS)
- **Statut** : Accepté — 2026-08-19
- **Décision** : Le code OTP parent est envoyé par WhatsApp par défaut, avec une nouvelle configuration `PARENT_OTP_CANAL_TELEPHONE` (valeurs `WHATSAPP`/`SMS`, éditable par un ADMIN via `/configuration`) permettant de basculer sur SMS. L'envoi par email reste indépendant de ce choix (toujours tenté en parallèle). Contrairement aux autres notifications (`NotificationDispatcher`), l'envoi de l'OTP n'est plus soumis aux bascules générales `NOTIFICATIONS_SMS_ENABLED`/`NOTIFICATIONS_WHATSAPP_ENABLED` : `ParentOtpService` sélectionne directement le `NotificationSender` du canal configuré (l'OTP est une étape fonctionnelle de connexion, pas une notification optionnelle).
- **Contexte** : demande explicite d'un paramétrage du canal OTP, WhatsApp par défaut.
- **Fichier ADR** : `adr/2026-08-19-canal-otp-parametrable-whatsapp-sms.md`
