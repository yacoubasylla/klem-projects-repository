# services/

Réservé aux **microservices backend indépendants** (Java Spring Boot, API Python) une fois qu'un
domaine est effectivement extrait du monolithe actuel.

## Pourquoi c'est vide aujourd'hui

La règle de gouvernance 4 dans `klem-labs-repository/GLOBAL_README.md` est explicite : *« un
monolithe modulaire bien structuré (packages par domaine) est une étape de trajectoire acceptée au
stade Prototype, jamais une destination finale »*. L'actuel `apps/backend-api` est ce stade de
monolithe accepté — il **n'est pas** déplacé ici par anticipation. Le déplacer maintenant
réécrirait chaque document/config qui référence `apps/backend-api` (README, CLAUDE.md, ADR) pour
un gain fonctionnel nul, puisqu'il n'existe encore aucun second service indépendant pour justifier
la scission.

## Quand utiliser réellement ce dossier

Lorsqu'un domaine à l'intérieur d'`apps/backend-api` est décomposé en son propre service
déployable (selon
`shared_architecture/microservices_&_delivery/specifications_techniques.md` dans le dépôt Labs) :

1. `git mv apps/backend-api/<domain-package> services/<service-name>` (préserve l'historique).
2. Ajouter `services/<service-name>` à `pnpm-workspace.yaml` — déjà couvert par le glob
   `services/*` ajouté avec cet échafaudage, donc aucune modification supplémentaire du fichier de
   workspace n'est nécessaire.
3. Lui donner son propre `package.json` (Node) ou `pom.xml`/`build.gradle` (Java) et son propre job
   CI.
4. Mettre à jour les références d'`apps/backend-api` pour appeler le nouveau service via son API
   plutôt qu'en process.

## Convention de nommage

`services/<domaine>-service` (ex. `services/billing-service`, correspondant au nom
`billing-service` déjà utilisé dans
`klem-labs-repository/platform-devsecops/cycle-devsecops-complet.md` §1, maillon 10).
