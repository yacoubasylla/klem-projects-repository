# ADR : Adoption de SYSTEM_INSTRUCTIONS.md — Universal App First (Expo/React Native) pour les apps clients

- **Date :** 2026-08-10
- **Statut :** Accepté
- **Auteurs :** Claude Code (session d'harmonisation du workspace)

## Contexte

`SYSTEM_INSTRUCTIONS.md` a été introduit le 2026-08-09 (copies identiques, jusqu'ici non suivies
par git, dans `klem-labs-repository` et `klem-projects-repository`) et décrit une stratégie
**Universal App First** : chaque interface utilisateur applicative destinée aux clients (Cantine
Connect, Hinterland-Track, KLEM Trade-X, Fleet Management) doit être développée sur Expo SDK 51+ /
Expo Router v3 (React Native) + NativeWind v4, compilable en Android, iOS et PWA depuis une base de
code unique.

Cette direction a déjà été exécutée avant d'être documentée : `apps/mobile-app/cantine-connect/`
(Expo Router, NativeWind) a été scaffoldé le 2026-08-10 (`feat(mobile): scaffold Cantine Connect
mobile/PWA`), sans ADR ni référence depuis un `CLAUDE.md` ou un README — la directive existait
uniquement comme fichier local non versionné, invisible dans l'historique git.

Elle entre en tension directe avec deux documents déjà en vigueur :
- `MASTER_SYSTEM_DIRECTIVE.md` §3.3/§16, qui fixe Next.js 14+/TypeScript comme frontend de
  référence pour la plateforme KLEM DataSphere ;
- `CLAUDE.md` (racine `klem-projects-repository`) §2.2, qui fixe React.js/TypeScript/MUI comme
  référence pour les apps clients existantes (cantine-connect, parcauto, clinic, pharmacie).
- La ligne « Backends Applicatifs : FastAPI (Python) & Spring Boot (Java) » de
  `SYSTEM_INSTRUCTIONS.md` place les deux sur un pied d'égalité, ce qui contredit
  `MASTER_SYSTEM_DIRECTIVE.md` §3.1/§3.2 (Spring Boot backend de référence, FastAPI réservé
  aux services Python isolés — RAG, embeddings, scoring ML).

## Décision

1. `SYSTEM_INSTRUCTIONS.md` est adopté comme référence pour la **couche présentation (UI)** des
   apps clients grand public de `klem-projects-repository` — la famille `apps/web-app/*` +
   `apps/mobile-app/*` (cantine-connect, parcauto, clinic, pharmacie) — chaque fois qu'une parité
   mobile native (Android/iOS) est un besoin réel du produit. Stack : Expo SDK 51+ / Expo Router
   v3, NativeWind v4, TanStack Query v5 + MMKV/SQLite (offline-first), `expo-secure-store` pour les
   jetons côté mobile.
2. Non rétroactif par écrasement : les frontends web existants (ex. `client-frontend` de
   cantine-connect, React/Vite/MUI) ne sont pas réécrits automatiquement. Toute convergence vers
   Expo/PWA se fait par une migration dédiée, testée et tracée par app — le même principe que
   l'ADR `2026-08-08-adoption-directive-maitre-datasphere-perimetre.md` a déjà posé côté backend.
   Les nouvelles apps mobiles (comme `apps/mobile-app/cantine-connect`) démarrent directement sur
   Expo.
3. `MASTER_SYSTEM_DIRECTIVE.md` §3.3/§16 (Next.js) reste la référence pour les applications
   **internes/back-office** de la plateforme KLEM DataSphere (`apps/admin`, `apps/copilot` au sens
   de sa §4.2) qui n'ont pas de besoin de build natif mobile — ce ne sont pas des apps « clients »
   au sens de la présente décision.
4. La ligne « Backends Applicatifs » de `SYSTEM_INSTRUCTIONS.md` n'est **pas** adoptée telle
   quelle : elle reste supplantée par `MASTER_SYSTEM_DIRECTIVE.md` §3.1/§3.2 et par
   `CLAUDE.md` §2.1 déjà en vigueur (Spring Boot/Java reste le backend de référence ; FastAPI/
   Python reste réservé aux services isolés RAG/ML). Seule la partie UI/mobile de
   `SYSTEM_INSTRUCTIONS.md` est actée par cette ADR.
5. `SYSTEM_INSTRUCTIONS.md` est désormais suivi par git dans les deux dépôts
   (`klem-labs-repository` et `klem-projects-repository`) plutôt que de rester un fichier local non
   versionné — toute directive active doit être traçable dans l'historique, pas seulement dans
   l'espace de travail local d'une session.

## Alternatives envisagées

- **Ignorer `SYSTEM_INSTRUCTIONS.md` et revenir sur le scaffold mobile déjà fait** : écartée — le
  scaffold est un travail réel déjà commité (`feat(mobile): scaffold Cantine Connect mobile/PWA`,
  2026-08-10) ; le défaire sans raison produit détruirait du travail valide pour un désaccord
  purement documentaire.
- **Remplacer `MASTER_SYSTEM_DIRECTIVE.md` §3.3/§16 entièrement par Expo/RN** : écartée — la
  plateforme DataSphere (apps internes, back-office) n'a pas de besoin mobile natif démontré ;
  imposer Expo partout serait une sur-ingénierie non justifiée pour ces surfaces.
- **Adopter aussi la ligne backend de `SYSTEM_INSTRUCTIONS.md` (FastAPI à égalité avec Spring
  Boot)** : écartée — aucune app du portefeuille n'a de besoin Python démontré à ce niveau ; cela
  contredirait sans justification le principe déjà établi (Spring Boot backend de référence,
  Python réservé aux besoins ML/RAG spécialisés).

## Conséquences

- **Avantages :** la stratégie mobile déjà en cours d'exécution devient traçable et discoverable
  (référencée depuis les `CLAUDE.md`/README plutôt que de vivre dans un fichier local) ; les deux
  directives (`MASTER_SYSTEM_DIRECTIVE.md`, `SYSTEM_INSTRUCTIONS.md`) coexistent par périmètre
  clair au lieu de se contredire silencieusement, suivant le même principe que l'ADR du 8 août.
- **Risques/Dettes :** deux stacks frontend distinctes coexistent désormais dans le portefeuille
  (Next.js pour l'interne, Expo/RN pour le client) — dette de cohérence documentée ici plutôt que
  cachée ; la convergence de `client-frontend` (React/Vite) de cantine-connect vers Expo reste à
  planifier et n'est pas engagée par cette ADR.
