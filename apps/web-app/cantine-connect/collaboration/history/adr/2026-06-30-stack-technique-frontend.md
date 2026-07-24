# ADR-004 · Stack Technique Front-end : React 18 + Vite + Material UI v9

- **Date** : 2026-06-30
- **Statut** : Accepté
- **Auteur** : Yacouba SYLLA

---

## Contexte

Le projet Cantine Connect nécessite un tableau de bord d'administration opérationnel pour les gestionnaires sur site. Ces agents travaillent sur des postes fixes avec des moniteurs de 15 à 17 pouces (résolution typique 1280×720 à 1920×1080). Les contraintes identifiées :

- Interface rapide à charger — dashboard interne, pas de besoin SEO
- Composants formulaires complexes (formulaire élèves 3 onglets) sans scroll vertical
- Tableaux avec pagination côté serveur (10 000+ élèves)
- Cohérence visuelle sur tous les écrans de l'application

## Décision

**Framework** : React.js 18 avec Vite comme bundler  
**Design System** : Material UI (MUI) v9  
**Routing** : React Router v7  
**Appels API** : Axios avec client singleton (`services/apiClient.js`)  
**Langage** : JavaScript pur (`.js`, `.jsx`) — pas de TypeScript

## Alternatives Évaluées

| Option | Raison du rejet |
|--------|----------------|
| Next.js | SSR/SSG inutile pour un dashboard SPA interne. Overhead de configuration pour aucun bénéfice. |
| Vue.js + Vuetify | Écosystème MUI déjà maîtrisé par l'équipe. Cohérence avec le reste du monorepo KLEM. |
| Tailwind CSS | Interdit par le CLAUDE.md du projet — utiliser uniquement `sx={{ }}` MUI. |
| TypeScript | Contrainte de vitesse de développement sur ce projet spécifique. Le CLAUDE.md précise `.js`, `.jsx`. |

## Conséquences

- **Positives** : Build Vite < 500ms en développement. HMR instantané. Bibliothèque de composants MUI exhaustive (DataGrid, Tabs, Dialog, etc.).
- **À surveiller** : Aucun typage statique — discipline de nommage et de structure des hooks obligatoire. Toute logique métier/API doit être dans les `services/` ou `hooks/`, jamais dans les composants vues.
- **Règle d'équipe** : Interdiction d'utiliser `useEffect` brut pour les appels API. Utiliser des custom hooks (`useEleves`, `useEtablissements`, etc.).
