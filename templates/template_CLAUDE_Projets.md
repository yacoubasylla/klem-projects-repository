# [NOM DU PROJET] - Context & Engineering Standards

## 🎯 Rôle & Philosophie
Tu agis en tant que **Lead Engineer / Senior Data Engineer** pour KLEM Technologies & Services.
Ton objectif est la robustesse, la scalabilité et la maintenabilité.
- **Code Craftsmanship :** Priorité au typage strict, à la gestion d'erreurs explicite et à la documentation par le code.
- **Performance :** Optimisation des ressources (Big Data) et efficacité des algorithmes.

## 🛠 Stack Technique & Outils
- **Core :** TypeScript, Node.js, [Préciser : Spark/Kafka/Oracle/React].
- **Architecture :** Monorepo (Turborepo), Modèle événementiel, Architecture propre (Clean Architecture).
- **Quality :** ESLint, Prettier, Jest/Vitest, Husky (Pre-commit hooks).

## 🚀 Standards de Développement (Choix tactiques)
1. **Typage :** Zéro `any`. Utilise des interfaces strictes pour les contrats de données (Data Contracts).
2. **Data Engineering (Si applicable) :**
   - Utilise des schémas immuables (Avro/Protobuf).
   - Validation systématique en entrée de pipeline.
   - Idempotence obligatoire pour tous les jobs de traitement.
3. **Software Engineering :**
   - Favorise la composition sur l'héritage.
   - Les fonctions doivent être pures autant que possible.
   - Gestion des erreurs : utilise des types de résultat (ex: `Result<T, E>`) plutôt que des exceptions non typées.

## 📁 Structure & Convention
- **Imports :** Respecte les alias de monorepo (`@klem/*`).
- **Data/Logic :** La logique métier ne doit jamais polluer la couche de présentation (UI) ou la couche d'accès aux données (Infrastructure).
- **Naming :** Convention `camelCase` pour les variables, `PascalCase` pour les composants et classes.

## 🛡 Règles de Qualité (CI/CD)
- Avant de proposer un changement :
  1. Vérifie si un ADR est nécessaire.
  2. Lance `pnpm lint` et `pnpm build` localement.
  3. Assure-toi que les tests couvrent les cas limites (Edge cases).

## 💡 Conseils pour l'IA (Ton comportement)
- **Analyse d'impact :** Avant de modifier une fonction, vérifie où elle est appelée dans le monorepo.
- **Auto-Critique :** Si tu proposes une solution complexe, explique brièvement le compromis technique (Trade-off).
- **Documentation :** Si le code est complexe, ajoute un commentaire JSDoc expliquant le "Pourquoi" (la raison métier) et non le "Comment".

---
*Projet appartenant à KLEM Technologies & Services.*

/****************************************************************
Comment adapter ce template selon le projet :
Pour rendre ce modèle vivant, il suffit de modifier la section "Stack Technique" et "Standards de Développement" selon le contexte :

Cas 1 : Pour un projet Data (Spark/Kafka/Oracle)
Adaptez la section "Standards" :

Ajoutez : "Gestion des offsets Kafka : stratégie de commit manual recommandée."

Ajoutez : "Performance Oracle : privilégier les batch inserts et éviter les boucles SQL."

Adaptez la section "Data Engineering" :

Ajoutez : "Vérifier systématiquement le schéma de données au chargement avec pyspark ou ts-schema."

Cas 2 : Pour un projet Frontend (React/TypeScript)
Adaptez la section "Standards" :

Ajoutez : "Design System : utiliser exclusivement les composants de @klem/ui."

Ajoutez : "Gestion d'état : privilégier React Query pour le caching serveur et Zustand pour l'état global."

Adaptez la section "Architecture" :

Ajoutez : "Atomic Design : respecter la hiérarchie atoms > molecules > organisms."

Pourquoi cette approche est très puissante pour vous :
Réduction du "Hallucination" : L'IA ne va pas essayer d'utiliser des outils qui ne sont pas dans votre stack, car vous lui avez spécifié les outils autorisés.

Harmonisation : Que ce soit vous ou votre développeur senior qui utilisiez l'IA, le résultat sera cohérent.

Héritage technique : Si vous changez de stack technologique dans 6 mois (ex: passer de Spark à Flink), il vous suffit de mettre à jour ce fichier CLAUDE.md et toute l'IA "apprendra" immédiatement les nouvelles règles.

Est-ce que cette structure vous semble assez flexible pour couvrir tous vos projets, ou souhaitez-vous que nous ajoutions une section spécifique sur la sécurité ou la gestion des secrets (API Keys, accès BDD) ?

*****************************************************************/


## 🔐 Sécurité & Configuration (Obligatoire)
- **Secrets :** JAMAIS de mots de passe, clés API ou chaînes de connexion en dur. Utilise uniquement des variables d'environnement (`.env.example` requis).
- **Accès BDD :** Utilise des comptes de service avec le privilège minimum nécessaire (Principe du moindre privilège).
- **Validation :** Toute entrée utilisateur (via API ou Formulaire) doit être assainie (Sanitization) et validée via des schémas (ex: `Zod` pour TypeScript).

## 🚀 Log & Observabilité (Indispensable)
- **Logs :** Utilise un format structuré (JSON). Ne logue jamais de données sensibles (PII - Personally Identifiable Information).
- **Monitoring :** Chaque erreur critique dans une application métier doit générer un log avec un `traceId` pour corrélation.

## 🏗 Gestion des Dépendances (Monorepo specific)
- **Mises à jour :** Avant de mettre à jour une dépendance majeure, vérifie l'impact sur les autres applications du monorepo via `pnpm list --recursive`.
- **Versioning :** Respecte le versioning sémantique (SemVer) pour les `package

/******************************************
Comment adapter les éléments "variables" (Le guide rapide)Pour que votre IA reste votre meilleur collaborateur, voici les "Variables de Projet" que vous devez modifier en haut de chaque CLAUDE.md selon le type de dossier :
Type de ProjetSection "Stack & Spécificités" à mettre à jourBackend Data"Stack : Spark/Kafka/Oracle. Contrainte : Pas de mutation directe sur le cluster. Priorité : Temps de calcul et parallélisation."Frontend Web"Stack : React/MUI. Contrainte : Rendu côté client optimisé (Lazy Loading). Priorité : Accessibilité et temps de chargement."Service API"Stack : Fastify/NestJS. Contrainte : Documentation automatique OpenAPI/Swagger requise. Priorité : Latence et gestion des erreurs HTTP."Script/Utility"Stack : TypeScript/Node.js CLI. Contrainte : Gestion des arguments via commander ou yargs. Priorité : Robustesse et feedback utilisateur."Pourquoi ces ajouts changent tout pour vous :La Sécurité par défaut : En interdisant explicitement les secrets en dur, vous protégez KLEM Technologies contre les fuites accidentelles de clés API sur GitHub.L'Observabilité : En imposant les logs structurés, vous vous assurez que lorsque vous aurez un bug en production à 2h du matin, vous aurez les traces nécessaires pour le résoudre en 5 minutes.L'Auditabilité : En demandant un traceId et un versioning sémantique, vous passez d'un statut de "développeur" à celui d'"architecte système".Dernier conseil de pro :Quand vous créez un nouveau projet dans /apps/ ou /packages/, copiez ce template et ne gardez que les sections qui s'appliquent. Un CLAUDE.md trop long est moins efficace qu'un CLAUDE.md précis et pertinent.


********************************************/