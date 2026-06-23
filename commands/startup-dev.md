/* Développeur : claude "Exécute le prompt startup.md" */
# Commande : /startup

Claude, nous démarrons une nouvelle session de développement sur le projet FleetControl de Klem Technologies & Services. Tu agis en tant qu'Architecte Logiciel et Développeur Senior. 

Avant de générer ou de modifier du code, tu dois impérativement lire et analyser l'intégralité des fichiers de gouvernance et de contexte suivants pour aligner tes connaissances techniques, métier et historiques :

1. **`CLAUDE.md`** (À la racine : Commandes globales, stack React/MUI/Spring Boot/PostgreSQL et standards de code premium).
2. **`claude-collaboration/docs/CONTEXT.md`** (Périmètre fonctionnel des 6 piliers du parc auto et règles métier comme les commissions chauffeurs).
3. **`claude-collaboration/docs/workflows.md`** (Protocole de collaboration, GitFlow de l'équipe et règles de validation des Pull Requests).
4. **`claude-collaboration/history/history-log.md`** (Journal de bord chronologique pour savoir exactement où s'est arrêtée la dernière session).
5. **`claude-collaboration/history/decision-log.md`** (Registre des décisions architecturales - ADR - pour ne pas enfreindre les choix techniques passés).

## Instructions de traitement
- Utilise tes outils système pour lire le contenu de ces 5 fichiers.
- Analyse le dernier bloc inséré dans `history-log.md` pour comprendre l'état exact du workspace.

## Format obligatoire de ta réponse
Une fois ta phase de lecture et d'alignement terminée, réponds-moi strictement en suivant ce plan concis :

- **🎯 Alignement Réussi** : Confirme que tu as lu et assimilé les fichiers de configuration de Klem.
- **🏁 Point d'Arrêt Précédent** : Résume en 2 puces maximum la dernière tâche livrée (Date, numéro de PR et fichiers de code impactés d'après le `history-log.md`).
- **🧠 Règles d'Architecture Actives** : Rappelle les 2 ou 3 dernières décisions majeures (ADR) issues du `decision-log.md` que tu devras respecter durant cette session.
- **🚀 Disponibilité** : Déclare que tu es parfaitement synchronisé et prêt à recevoir mes instructions de codage.