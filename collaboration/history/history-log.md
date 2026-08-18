# Workspace History

> Journal chronologique de toutes les sessions et décisions importantes.
> Le plus récent en haut. Mis à jour automatiquement par Claude.
>
> **Comment ça marche :** Quand je lance la commande `/update` après une session importante, ou quand je raconte un changement significatif, Claude ajoute une entrée ici automatiquement. Je n'ai pas à écrire ce fichier manuellement.

---

[VIDE INITIALEMENT - SE REMPLIRA AU FIL DES SESSIONS]

### [2026-08-18] - Fix du check GitHub « Vercel » systématiquement rouge (projet `klem-repo`)
- **Statut :** Livré / Opérationnel (déploiement `READY` vérifié en pratique avant fusion)
- **Fichiers Modifiés :** `vercel.json` (nouveau, racine), `vercel-root-noop/index.html` (nouveau),
  `.gitignore` (`.env*` ajouté par `vercel link`).
- **Description :** Le check « Vercel » échouait systématiquement (constaté en erreur sur `main`
  depuis ≥4 jours, indépendant du code — repéré sur la PR #10). Cause : le projet Vercel
  `klem-repo` (seul projet rattaché à ce dépôt côté GitHub, `Root Directory: .`) utilisait le
  Framework Preset Create React App, qui attend un `build/` racine — alors que `pnpm build`
  (`turbo build`) construit chaque app dans son propre dossier de sortie, et qu'aucune application
  déployable n'existe à la racine du monorepo (chaque app réelle a déjà son propre projet Vercel
  dédié : `cantine-connect`, `klem-labs-dashboards`). Fix : `vercel.json` transforme `klem-repo` en
  déploiement no-op déterministe (page statique expliquant pourquoi), validé par `vercel build` +
  `vercel deploy --prebuilt` → `readyState: READY`. Détail et alternatives écartées :
  `collaboration/history/adr/2026-08-18-vercel-klem-repo-noop-deploy.md`.

### [2026-08-13] - Introduction de `services/billing-service` (Jalon 0 clos, Jalons 1/2 codés)
- **Statut :** Livré / Opérationnel (code non encore validé par un pilote réel en production)
- **Fichiers Modifiés :**
  - `services/billing-service/` (nouveau service Spring Boot 3.3.5 / Java 21) : agrégat
    `Transaction` et machine à états, contrat `PaymentProvider` implémenté par 4 agrégateurs
    (CinetPay, Bizao, Fedapay, PayDunya) et 4 opérateurs en API directe (Wave, Orange Money, MTN
    Mobile Money, Moov Money), API REST (`TransactionController`, `WebhookController`), migration
    Flyway `V1__init_billing_schema.sql`, tests unitaires (13/13 verts, `mvn test`).
  - `collaboration/history/adr/2026-08-13-introduction-billing-service.md` (nouveau).
  - Côté `klem-labs-repository` : `shared_architecture/billing_&_payments/{readme.md,
    specifications_techniques.md}` (audit + machine à états, contrat API, scope PCI-DSS, jalons
    MVP/V1/V2, Jalon 0 clos), `projects/03_cantine_connect/specifications_techniques.md` (dérogation
    documentée à la règle de gouvernance n°1).
- **Description :** Audit du module transverse `billing_&_payments`, jusqu'ici documenté sans
  contrat technique concret ni jalons de mise en œuvre. Jalon 0 clos : Yacouba Sylla désigné
  mainteneur transverse, écart `cantine_connect` (intégration paiement déjà en production, livraison
  client imminente) tranché par dérogation documentée plutôt que migration — voir l'ADR pour le
  détail. Code des Jalons 1 (MVP, CinetPay) et 2 (V1, multi-opérateur/agrégateur complet) écrit dans
  la foulée ; `cantine_connect` non touché (vérifié). Reste dû avant mise en production : validation
  par un premier projet consommateur réel (candidats : `07_boutiki`, `01_clear_comply`,
  `05_fleet_advance`, `10_dispo_link`), et confirmation des contrats de payload/signature par
  opérateur/agrégateur contre une documentation API réelle ou un compte sandbox.

### [2026-08-08] - Adoption de KLEM_MASTER_SYSTEM_DIRECTIVE.md : périmètre, gouvernance, scaffolding des produits pivots et conformité additive cantine-connect
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés :**
  - Gouvernance : `collaboration/history/adr/2026-08-08-adoption-directive-maitre-datasphere-perimetre.md` (nouveau),
    `collaboration/history/decision-log.md` (ADR 003 indexée), `CLAUDE.md` et
    `templates/{template_CLAUDE(maitre).md,CLAUDE_RACINE.md}` (notes de portée ajoutées),
    `README.md` (racine) et `klem-labs-repository/GLOBAL_README.md` (référence à la directive
    ajoutée).
  - Scaffolding : `services/transit-ops-service/**` (nouveau, squelette Hinterland-Track — Java 21,
    Spring Boot 3.3.5, OAuth2 Resource Server, Actuator, springdoc-openapi, Flyway, ArchUnit/
    Testcontainers en dépendances de test) et `services/referentiel-api-service/**` (nouveau,
    squelette KLEM Trade-X, même stack).
  - Conformité additive : `apps/web-app/cantine-connect/server-backend/{pom.xml,
    src/main/java/com/klem/cantine/common/GlobalExceptionHandler.java,
    src/main/java/com/klem/cantine/common/SecurityConfig.java}` (détail dans le history-log propre
    à cantine-connect).
- **Description :** `KLEM_MASTER_SYSTEM_DIRECTIVE.md` v2.0 existait dans 5 emplacements du workspace
  sans être référencé par aucun document. Un audit a montré que le workspace réel est un portefeuille
  de projets clients hétérogènes (Java 17, trois modèles d'authentification distincts déjà en
  production ou prévus) plus large que la seule plateforme KLEM DataSphere décrite par la directive.
  Décision actée par ADR 003 (voir fichier ci-dessus) : la directive fait autorité pour les nouveaux
  services KLEM DataSphere (`services/*`), les apps clients existantes gardent leur `CLAUDE.md`
  propre, aucune architecture de production n'est remplacée silencieusement.
  Les deux nouveaux services (`transit-ops-service`, `referentiel-api-service`) sont des squelettes
  Sprint 0 sans logique métier (packages domaine vides, un seul endpoint réel `/actuator/health`) —
  vérifiés par compilation, packaging et démarrage réel (PostgreSQL via conteneur Docker jetable) :
  `/actuator/health` → `UP`, `/v3/api-docs` → 200, endpoint protégé sans token → 401. Le test
  Testcontainers embarqué dans chaque module échoue dans cet environnement précis (incompatibilité
  de négociation d'API entre `docker-java`/testcontainers 1.20.1 et le Docker Engine 29.7.1 très
  récent de ce poste — client bloqué sur l'API 1.32, serveur exigeant ≥1.40) : limitation
  d'environnement constatée, pas un défaut du scaffold, à surveiller lors du premier `mvn verify`
  en CI/poste standard. cantine-connect (seule app avec du code de production, paiements Mobile
  Money réels) a reçu deux ajouts sans changement de comportement : `requestId` dans les réponses
  d'erreur et documentation OpenAPI (`/swagger-ui.html`) — 44 tests existants toujours au vert,
  démarrage réel vérifié. Java 21, migration OAuth2 Resource Server, ArchUnit/Testcontainers et
  refactor de couches restent des dettes documentées (ADR 003), volontairement non exécutées sans
  cycle de non-régression dédié.

### [2026-08-02] - Message d'indisponibilité unifié pour le chatbot IA (showcase-website)
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés :** `apps/showcase-website/site-klem/web/app/themes/klem-theme/inc/chatbot.php`,
  `apps/showcase-website/site-klem/web/app/themes/klem-theme/src/main.js`, assets recompilés
  (`dist/assets/main-*.js`, `dist/.vite/manifest.json`).
- **Description :** Le widget de chat affichait parfois un message d'erreur brut/générique côté
  visiteur en cas de dysfonctionnement (clé API Anthropic absente, erreur réseau serveur, réponse API
  non-2xx, échec du `fetch` côté navigateur, réponse malformée). Ces branches d'erreur "malfonction"
  (backend PHP et frontend JS) affichent désormais un message unique et clair : « Votre assistant est
  momentanément indisponible. Veuillez réessayer plus tard. ». Les messages spécifiques déjà
  pertinents pour les cas attendus (limite de débit, requête invalide, nonce non autorisé) sont
  conservés inchangés. `pnpm build` validé sans erreur.

### [2026-07-29] - Formalisation du workflow multi-agents IA (Recherche → Plan → Exécution → Revue)
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés :** `.github/PULL_REQUEST_TEMPLATE.md` (ce dépôt) ;
  `klem-labs-repository/platform-devsecops/workflow-multi-agents-ia.md` (nouveau),
  `klem-labs-repository/platform-devsecops/adr/0016-workflow-multi-agents-ia-recherche-plan-execution-review.md` (nouveau),
  `klem-labs-repository/platform-devsecops/templates/{readme,template-recherche,template-plan-architecture,template-review-ia}.md` (nouveaux),
  `klem-labs-repository/platform-devsecops/adr/readme.md`, `klem-labs-repository/platform-devsecops/decision-log.md`,
  `klem-labs-repository/platform-devsecops/cycle-devsecops-complet.md`,
  `klem-labs-repository/projects/09_klem_dev_workflow/specifications_fonctionnelles.md` (dépôt sibling).
- **Description :** Enrichissement du cycle DEVFLOW-KLEM existant (10 maillons déjà actés) plutôt
  que création d'un système parallèle : les maillons 1 (Cadrage), 4 (Cycle Git) et 5 (Revue)
  détaillent désormais comment des agents IA interviennent au-delà du rôle de copilote unique —
  recherche multi-agents, plans d'implémentation concurrents, exécution par rôle, revue `/code-review`
  avant la revue humaine. Deux profils actés par ADR 0016 (`klem-labs-repository`) : **light**
  (défaut, tâches courantes, compatible avec la contrainte « équipe 1-3 devs ») et **full** (features
  majeures/structurantes). Point de vigilance explicitement documenté dans l'ADR : les plans
  concurrents du profil full ne remettent jamais en cause la cible d'architecture déjà tranchée par
  `GLOBAL_README.md` règle de gouvernance n°4 (microservices cible, monolithe modulaire accepté
  comme étape de trajectoire) — ils comparent des options de mise en œuvre, pas la stack macro.
  Le template de PR de ce dépôt référence désormais explicitement cette revue agent préalable et la
  vérification humaine obligatoire des zones critiques (auth, paiement, données sensibles).
  Aucun nouvel outil introduit : le workflow s'appuie sur les mécanismes déjà en place (agents
  Explore/Plan/general-purpose, skills `/code-review`/`security-review`, gabarit ADR et
  `scripts/create-adr.sh` existants). La création de sous-agents dédiés par rôle
  (`.claude/agents/*.md` back/front/QA/migration) est notée comme piste V2 hors périmètre.

### [2026-07-25] - Traduction en français de tous les documents restés en anglais dans le workspace
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés :** `docs/README.md`, `services/README.md`,
  `infrastructure/{docker,terraform,kubernetes}/README.md`,
  `apps/web-app/cantine-connect/client-frontend/README.md`, `apps/web-app/CLAUDE.md` (ce dépôt) ;
  `klem-labs-repository/platform-devsecops/raw-notes/prompt_chirugical.md` (dépôt sibling) ;
  `README.md`, `AUDIT_SUMMARY.md` (workspace racine).
- **Description :** Un scan de langue sur les 286 fichiers `.md` du workspace (heuristique
  mots-clés FR/EN + densité de caractères accentués) a identifié 9 documents rédigés en anglais,
  tous traduits en français sans changement de structure, de liens ni de blocs de code/commandes.
  `AUDIT_SUMMARY.md` (racine) a été traduit et mis à jour au passage avec le résultat vérifié des
  alertes Dependabot (20/23 refermées, 3 restantes détaillées en tableau). `apps/web-app/CLAUDE.md`
  est un exemple générique (stack Express.js/Prisma, noms de package fautifs) qui contredit le
  vrai `CLAUDE.md` racine du dépôt (Spring Boot/React, déjà en français et faisant autorité) —
  traduit tel quel car demandé explicitement, mais à supprimer ou remplacer plutôt qu'à maintenir.
  Aucune référence croisée (liens markdown, chemins cités) n'a nécessité de changement : les noms
  de fichiers sont inchangés, seule la langue du contenu a changé. `README.md` et
  `GLOBAL_README.md` de `klem-labs-repository` étaient déjà en français et à jour — vérifiés, non
  modifiés.

### [2026-07-24] - Unification workspace KLEM + scaffold services/infrastructure/docs + pont Labs->Projects
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés :** `services/README.md`, `infrastructure/{docker,terraform,kubernetes}/README.md`,
  `docs/README.md`, `pnpm-workspace.yaml`, `README.md`, `collaboration/history/adr/2026-07-24-scaffold-services-infra-docs-et-pont-labs-projects.md`
  (ce dépôt) ; `klem-labs-repository/platform-devsecops/scripts/klem_promote.py`,
  `klem-labs-repository/bin/klem-promote`, `klem-labs-repository/GLOBAL_README.md` (dépôt sibling).
- **Description :** Les deux dépôts `klem-labs-repository` et `klem-projects-repository` sont
  unifiés sous `~/Documents/klem-enterprise-workspace/` (dossier plat, historique et remotes
  git intacts, vérifié par diff de `git status`/`git log` avant/après). Ajout de `services/`,
  `infrastructure/`, `docs/` en scaffolding (aucun code existant déplacé -- voir l'ADR associé pour
  le raisonnement). Ajout de `klem_promote.py` : passerelle Labs -> Projects qui réutilise la
  détection de statut de `generate_dashboard_data.py` et ne promeut un projet que si la règle de
  gouvernance n°7 est satisfaite.

## [2026-06-21] - Implémentation Sécurité & Authentification

*   **Type** : 🚀 Feature & Architecture
*   **Pull Request** : [#14](https://github.com/klem-tech/fleetcontrol/pull/14)
*   **ADR Associé** : [ADR #004 : Structure de l'authentification JWT](./claude-collaboration/history/ADR-004-jwt.md)
*   **Développeur(s)** : [Nom du Dev]
*   **Impact Technique (Fichiers modifiés)** :
    *   `packages/database/prisma/schema.prisma` (Ajout des tokens)
    *   `apps/backend/src/main/java/com/klem/fleetcontrol/config/SecurityConfig.java`
    *   `apps/backend/src/main/java/com/klem/fleetcontrol/auth/JwtFilter.java`
    *   `apps/frontend/src/hooks/useAuth.ts` (Gestion du cycle de vie du token)

> **Note de session** : Implémentation validée conformément aux exigences de l'ADR #004. Les tokens sont stockés dans des cookies HttpOnly pour parer les failles XSS. Le rôle `CHAUFFEUR` est bridé aux endpoints de courses et livraisons.