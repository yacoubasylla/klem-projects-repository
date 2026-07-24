/* Prompt du Développeur : claude "Exécute le prompt update.md pour la PR #X" */
# Commande : /update [Numéro de la PR]

Claude, nous terminons cette session de développement. Tu vas mettre à jour le fichier de traçabilité `collaboration/history/history-LOG.md` en analysant le travail effectué durant cette session[cite: 15].

## Étape 1 : Analyse automatique de l'espace de travail
1. Exécute la commande système `git status` et `git diff --name-only main` (ou compare avec votre branche parente) pour lister précisément les fichiers React (front-end) ou Spring Boot (back-end) créés, modifiés ou supprimés[cite: 6, 15].
2. Vérifie si un nouveau fichier a été ajouté dans le dossier `docs/`[cite: 15]. Si oui, lis-le pour identifier s'il s'agit d'un nouvel ADR (extrait son ID et son titre issus de `collaboration/history/decision-LOG.md`)[cite: 2, 15].

## Étape 2 : Récupération des variables
- **Date** : Utilise la date du jour au format AAAA-MM-JJ (Nous sommes en 2026)[cite: 15].
- **Pull Request** : Récupère le numéro de la PR fourni dans ma commande (ex: #12)[cite: 15]. Si je ne l'ai pas fourni, écris `[PR en attente]`[cite: 15].
- **ADR Associé** : Si tu as détecté un nouvel ADR à l'étape 1, isole son titre et sa référence[cite: 15]. Sinon, écris `Aucun`[cite: 15].

## Étape 3 : Rédaction du Log
Ouvre le fichier `collaboration/history/history-LOG.md` et injecte **tout en haut du fichier** (juste sous la description principale, pour garder un ordre antéchronologique) le bloc suivant, complété avec les informations réelles issues de l'analyse du workspace[cite: 15] :

---

### ## [AAAA-MM-JJ] - [Titre court et explicite de la fonctionnalité]

* **Type** : [🚀 Feature | 🐛 Bugfix | 🏗️ Architecture | 📝 Documentation][cite: 15]
* **Pull Request** : [#Numéro](https://github.com/votre-org/cantine-connect/pull/Numéro)[cite: 15]
* **ADR Associé** : [Ex: ADR #004 : Titre de l'ADR](./DECISION-LOG.md)[cite: 15]
* **Impact Technique (Fichiers modifiés)** :
    * `client-frontend/src/views/...`[cite: 6, 15]
    * `server-backend/src/main/java/com/cantine/connect/...`[cite: 6, 15]

> **Note de session** : [Rédige un résumé technique de 2-3 lignes maximum expliquant concrètement les composants ou modules fonctionnels de Cantine Connect impactés (ex: Formulaire Élève MUI Tabs, Intégration Webhook Mobile Money, Contrôle d'accès QR Code, Aspect AOP pour la table de trace, etc.)][cite: 1, 2, 5, 10, 15].

---

## Étape 4 : Mise à jour des autres fichiers de gouvernance
En plus du journal de bord, mets à jour — **si possible et nécessaire seulement** (ne modifie pas un fichier sans raison réelle liée au travail de la session) — les fichiers suivants :
- **`README.md`** : si la vision macro, la stack ou les objectifs ont changé.
- **`CLAUDE.md`** : si une nouvelle commande, une nouvelle règle de code ou une nouvelle contrainte d'architecture a été introduite durant la session.
- **`collaboration/context/CONTEXT.md`** : si le périmètre stratégique ou les contraintes métier ont évolué.
- **`collaboration/history/decision-log.md`** et **`collaboration/history/adr/`** : si une nouvelle décision architecturale a été prise (nouvelle bibliothèque, changement de modèle de données, nouveau flux réseau) — crée le fichier ADR correspondant si besoin.
- **Tout autre fichier `.md`** du projet directement concerné par le changement livré durant la session.

## Étape 5 : Validation
Une fois les fichiers mis à jour, montre-moi le bloc textuel exact que tu as inséré/modifié pour confirmation finale avant commit et push[cite: 4, 15].

## Étape 6 : Commit, Push et Déploiement
Une fois la confirmation obtenue :
1. Committe l'ensemble des changements de documentation (et de code le cas échéant) avec un message de commit conventionnel clair.
2. Pousse (`git push`) sur la branche `main` du dépôt GitHub.
3. Vérifie que le déploiement (backend Railway et/ou frontend Vercel selon les fichiers modifiés) se déclenche automatiquement et aboutit (statut « Online » / healthcheck OK), puis confirme-le-moi.