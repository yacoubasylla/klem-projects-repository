# ADR : Vercel Klem Repo Noop Deploy

- **Date :** 2026-08-18
- **Statut :** Accepté
- **Auteurs :** Yacouba SYLLA (avec Claude Code)

## Contexte
Le check GitHub « Vercel » échouait systématiquement sur toute PR/push (constaté en `Error` sur
`main` depuis au moins 4 jours, indépendamment de tout changement de code — voir PR #10).
Cause racine identifiée via `vercel project inspect klem-repo` : le projet Vercel `klem-repo`
(le seul rattaché à ce dépôt côté intégration GitHub, `Root Directory: .`) est configuré avec le
Framework Preset **Create React App**, qui attend un dossier `build/` à la racine après
`npm run build`. Or `package.json#scripts.build` exécute `turbo build`, qui construit chaque
application du monorepo dans son propre dossier de sortie (`dist/**`, `.next/**`, `target/**`,
`build/libs/**` — voir `turbo.json`), jamais un `build/` à la racine.

Il n'existe par ailleurs aucune application déployable à la racine du monorepo : chaque
application réelle a déjà son propre projet Vercel dédié avec son propre `Root Directory`
(`cantine-connect` → `apps/web-app/cantine-connect/client-frontend`, `klem-labs-dashboards`) ;
`apps/showcase-website` est un thème WordPress (hébergement distinct, pas Vercel).
`klem-repo` ne correspond donc à aucune cible de déploiement réelle — sa seule fonction observée
est de générer le check de statut « Vercel » sur les PR/pushes de ce dépôt.

## Décision
Ajouter un `vercel.json` à la racine du dépôt qui transforme `klem-repo` en déploiement no-op
déterministe et rapide, plutôt que de tenter de le faire correspondre à une "vraie" application
(qui n'existe pas) :
- `"framework": null` — désactive la détection automatique (qui réintroduirait les hypothèses CRA).
- `"installCommand": "true"` — évite d'installer tout le monorepo pnpm pour un déploiement qui ne
  sert qu'un fichier HTML statique (économie de temps/coût de build).
- `"buildCommand": "true"` — aucune vraie étape de build (limite de 256 caractères sur ce champ
  Vercel constatée en pratique, donc pas de logique inline ; le contenu vit dans un vrai fichier).
- `"outputDirectory": "vercel-root-noop"` — nouveau dossier statique committé
  (`vercel-root-noop/index.html`) expliquant explicitement, pour quiconque visite l'URL déployée,
  pourquoi ce projet ne sert intentionnellement rien.

Validé en pratique avant fusion : `vercel link --project klem-repo`, `vercel pull`,
`vercel build` (`"status": "ok"`), puis `vercel deploy --prebuilt` → `"readyState": "READY"`.

## Alternatives envisagées
- **Corriger le Framework Preset/Output Directory dans les réglages du projet Vercel
  (dashboard)** — fonctionnellement équivalent, mais non versionné/auditable dans le dépôt et
  non reproductible par un autre membre de l'équipe ; le message d'erreur Vercel lui-même propose
  `vercel.json#outputDirectory` comme alternative, retenue ici pour cette raison.
- **Supprimer purement et simplement le projet Vercel `klem-repo`** — écartée pour cette passe :
  action plus difficile à annuler (registre Vercel, intégration GitHub existante) qu'une simple
  reconfiguration ; peut être reconsidérée séparément si le check « Vercel » sur les PR s'avère
  définitivement sans utilité.
- **Faire pointer `klem-repo` vers une application existante** (ex. dupliquer la config de
  `cantine-connect`) — écartée : créerait un déploiement redondant du même frontend sous deux
  projets Vercel distincts, source de confusion (quelle URL fait foi ?).

## Conséquences
- **Avantages :** le check « Vercel » sur les PR/pushes de ce dépôt passe de systématiquement rouge
  à systématiquement vert (déploiement déterministe, ~10s), sans dépendre d'un réglage manuel dans
  le dashboard Vercel non versionné. `vercel link`/`vercel pull` a par ailleurs fait ajouter `.env*`
  à `.gitignore` (garde-fou supplémentaire contre un commit accidentel de secrets).
- **Risques/Dettes :** si `klem-repo` doit un jour héberger une vraie application racine, ce
  `vercel.json` devra être remplacé (pas seulement complété) — documenté ici pour que ce ne soit
  pas une surprise. Le projet Vercel `klem-repo` reste par ailleurs vestigial : sa suppression pure
  et simple reste une option à réévaluer si le check PR s'avère inutile à terme.
