#Voici le contenu de votre ADR "Standards de développement et structure des packages". 
#Vous pouvez le générer en utilisant votre script ./scripts/create-adr.sh standards-dev-packages ou le créer manuellement.

# ADR : Standards de developpement et structure des packages

- **Date :** 2026-06-18
- **Statut :** Accepté
- **Auteurs :** [Votre Nom], CEO KLEM Technologies

## Contexte
Avec l'adoption du monorepo, il est crucial d'harmoniser la manière dont les applications (`apps/`) consomment les briques communes (`packages/`). Sans standardisation, nous risquons des incohérences de typage, des imports circulaires et une dette technique élevée.

## Décision
1. **Namespace :** Tous les packages partagés utiliseront le préfixe `@klem/`.
2. **Structure des packages :** Chaque package doit contenir un `package.json` configuré avec `workspace:*` et exposer ses fonctions via un fichier `src/index.ts` central.
3. **Interopérabilité :** 
   - Aucune logique métier ne doit être dupliquée dans les `apps/`. Si une fonction est utilisée deux fois, elle doit être extraite dans un `package`.
   - Les composants UI doivent être purement présentationnels et respecter le Design System centralisé.
4. **Typage :** TypeScript est obligatoire. Les interfaces doivent être exportées depuis les packages pour garantir un typage cohérent entre le backend et le frontend.

## Alternatives envisagées
- **Liberté totale de structure par équipe :** Rejeté car cela empêche la mutualisation du code et rend la maintenance globale impossible pour le Lead Développeur.
- **Importation via chemins relatifs (`../../`) :** Rejeté car source d'erreurs et de fragilité lors des refactorisations de dossiers.

## Conséquences
- **Avantages :** 
    - Auto-documentation facilitée par VS Code (Navigation par Clic).
    - Rapidité de développement accrue par la réutilisation immédiate.
    - Uniformité visuelle et fonctionnelle de toutes les solutions KLEM.
- **Risques :**
    - Courbe d'apprentissage pour les nouveaux arrivants sur la gestion des workspaces.
    - Nécessité de maintenir rigoureusement le fichier `src/index.ts` de chaque package.