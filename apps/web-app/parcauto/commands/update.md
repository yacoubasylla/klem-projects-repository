/Prompt du Développeur : claude "Exécute le prompt update.md pour la PR #12"*/
# Commande : /update [Numéro de la PR]

Claude, nous terminons cette session de développement. Tu vas mettre à jour le fichier de traçabilité `claude-collaboration/history/history-log.md` en analysant le travail effectué.

## Étape 1 : Analyse automatique de l'espace de travail
1. Exécute la commande système `git status` et `git diff --name-only develop` (ou compare avec la branche parente) pour lister précisément les fichiers créés, modifiés ou supprimés.
2. Vérifie si un nouveau fichier a été ajouté dans le dossier `claude-collaboration/history/`. Si oui, lis-le pour identifier s'il s'agit d'un nouvel ADR (extrait son ID et son titre).

## Étape 2 : Récupération des variables
- **Date** : Utilise la date du jour (au format AAAA-MM-JJ).
- **Pull Request** : Récupère le numéro de la PR fourni dans ma commande (ex: #14). Si je ne l'ai pas fourni, écris `[PR en attente]`.
- **ADR Associé** : Si tu as détecté un nouvel ADR à l'étape 1, isole son lien relatif et son titre. Sinon, écris `Aucun`.

## Étape 3 : Rédaction du Log
Ouvre le fichier `claude-collaboration/history/history-log.md` et injecte **tout en haut du fichier** (juste sous le titre principal, pour garder un ordre antéchronologique) le bloc suivant, complété avec les informations réelles :


## [AAAA-MM-JJ] - [Titre court et explicite de la fonctionnalité]

* **Type** : [🚀 Feature | 🐛 Bugfix | 🏗️ Architecture | 📝 Documentation]
* **Pull Request** : [#Numéro](https://github.com/klem-tech/fleetcontrol/pull/Numéro)
* **ADR Associé** : [Ex: ADR #004 : Titre de l'ADR](./nom-du-fichier-adr.md)
* **Impact Technique (Fichiers modifiés)** :
    * `chemin/du/fichier_1`
    * `chemin/du/fichier_2`

> **Note de session** : [Rédige un résumé de 2-3 lignes maximum expliquant concrètement ce qui a été fait, les choix techniques appliqués et les modules fonctionnels de FleetControl impactés (ex: Location, Maintenance, etc.)].


## Étape 4 : Validation
Une fois le fichier mis à jour, montre-moi le bloc que tu as inséré pour confirmation finale.