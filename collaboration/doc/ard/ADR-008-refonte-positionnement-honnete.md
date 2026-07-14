# ADR-008 — Refonte du positionnement : honnêteté (Hero/About) + Cas Clients illustratifs

**Date :** 2026-07-14
**Statut :** Accepté
**Décideur :** KLEM Technologies & Services / Claude Code

---

## Contexte

Un brief externe (`prompt-renovation.md.md`, généré via Perplexity) demandait une refonte de contenu pour rassurer des décideurs B2B alors que KLEM est une jeune structure en phase de prospection active de ses premiers clients, et pour montrer une compréhension à jour des tendances data/IA/commerce digital via la page Actualités.

En explorant le site avant d'exécuter le brief, un problème de crédibilité plus large que ce que le brief mentionnait explicitement a été identifié : le Hero et la section À Propos affichaient des statistiques entièrement inventées — "5+ ans d'expertise", "30+ projets livrés", "4,2M événements/jour", "18+ solutions déployées", "99,97% uptime infrastructure", "96% satisfaction clients", "98% projets livrés dans les délais", "88% fidélisation clients". Aucune de ces métriques n'est vérifiable pour une structure qui démarre sa prospection — c'est exactement le type de risque qu'une décision antérieure du projet (DEC-012, 2026-06-26) avait déjà traité en retirant les faux témoignages clients ("nuit à la crédibilité"), sans que ces statistiques n'aient été revues au même moment.

## Décision

**1. Honnêteté du Hero et de l'About.** Toute métrique impliquant un historique client (satisfaction, fidélisation, volume traité, nombre de projets livrés, ancienneté) est retirée et remplacée par des faits structurels vrais et vérifiables dès aujourd'hui (délai de réponse promis, nombre de secteurs couverts, nombre de piliers de service, engagement de sur-mesure). Le panneau "Performances clés" (barres de %) de la section About devient un panneau "Nos engagements" (promesses opérationnelles, sans pourcentage fabriqué). Le seul indicateur chiffré conservé avec une connotation de performance (disponibilité infrastructure) est reformulé en objectif explicite ("Objectif : 99,9% de disponibilité") plutôt qu'en moyenne mesurée sans historique.

**2. Page "Cas Clients" sans faux témoignages.** Le brief suggérait 3 cas clients avec une citation fictive attribuée à un rôle générique ("Responsable/Directeur"). Cette approche a été explicitement écartée par l'utilisateur (choix validé avant implémentation) au profit de cas d'usage clairement présentés comme illustratifs — "Exemple d'application" / "type de projet que nous menons" — sans citation attribuée à qui que ce soit, sans résultat chiffré inventé (pas de "-30%" ou "+20%" fabriqués). Seul le contexte, le défi, la solution KLEM et le bénéfice qualitatif sont décrits. Cette approche est cohérente avec DEC-012 et évite tout risque de publicité trompeuse (faux témoignage).

**3. Contenu réel pour Actualités.** Le hub Actualités existait techniquement (filtre, article vedette, grille) mais était vide de tout contenu réel — seul le post "Hello world!" par défaut de WordPress existait en base (confirmé par requête directe sur `klem_posts`). 5 articles de fond réels (temps réel/streaming, gouvernance des données, agents IA, cloud-first, commerce omnicanal) ont été rédigés et publiés, tous classés dans la catégorie "Blog" existante — pas de nouvelle taxonomie créée (voir DEC-033).

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Garder les statistiques actuelles | Risque de crédibilité : un décideur qui vérifie et ne trouve aucune référence publique associera l'incohérence à un manque de sérieux, contre-productif pour une structure qui cherche justement à être prise au sérieux par ses premiers clients |
| Citations fictives sur la page Cas Clients (suivre le brief tel quel) | Un faux témoignage attribué à un rôle, même sans nom, reste une allégation trompeuse si un visiteur le prend pour un retour client réel — écarté par choix explicite de l'utilisateur |
| 4 nouvelles catégories Actualités ("Données & IA", "ERP & applications", "Commerce digital", "Secteur public") suggérées par le brief | L'UI de filtre du hub est câblée en dur sur exactement 3 onglets (Blog/Actualités/Événements) ; casser ce composant pour un gain éditorial marginal sur seulement 5 articles n'était pas justifié à ce stade |

## Conséquences

- ✅ Le site ne contient plus aucune allégation invérifiable sur l'historique client de KLEM
- ✅ La page Cas Clients apporte de la substance concrète sans risque de publicité trompeuse
- ✅ Le hub Actualités n'est plus vide — 5 articles réels, réutilisables tels quels pour la prospection commerciale
- ⚠️ Si KLEM obtient de vrais premiers clients/résultats chiffrés dans le futur, ces sections (stats Hero/About, cas d'usage) devront être mises à jour pour refléter le réel historique désormais disponible plutôt que rester purement qualitatives
- ⚠️ Les 5 articles Actualités sont insérés via une fonction PHP idempotente (`klem_bootstrap_seed_articles()`, pas de WP-CLI disponible dans l'environnement) plutôt que rédigés dans l'admin WordPress — toute correction éditoriale ultérieure doit se faire directement dans wp-admin (la fonction ne réinsère pas si le `post_name` existe déjà, donc modifier le post en base ne sera jamais écrasé par une ré-exécution du bootstrap)
