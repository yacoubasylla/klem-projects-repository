Dans la vie d'un projet technologique chez Klem Technologies & Services, un ADR n'est pas un simple document administratif qu'on rédige pour le plaisir. C'est un acte de gouvernance.

Pour que cet outil reste efficace et ne devienne pas une corvée pour vos deux développeurs, voici les règles chirurgicales pour savoir quand le déclencher, comment le rédiger et comment le conserver.

1. Le "Quand" : Le test d'éligibilité d'un ADR
Tous les choix techniques ne méritent pas un ADR. Si un développeur choisit de nommer une fonction calculateTotal() plutôt que computeTotal(), c'est du style, pas de l'architecture.

Vous devez créer un ADR uniquement face à une décision significative. Une décision est significative si elle remplit au moins l'un de ces critères :

Le coût de retour en arrière est élevé : S'il faut plus de 3 jours de refactoring pour changer d'avis dans 6 mois (ex: choix du provider de paiement Mobile Money, choix de la structure de la base de données).

Impact transverse : La décision force les deux développeurs à changer leur manière de coder (ex: interdire les appels API directs dans les composants React et imposer des Hooks personnalisés).

Contrainte de rupture (Breaking Change) : Changement d'une dépendance majeure ou d'une version de framework (ex: passage de Java 17 à Java 21).

2. Le "Comment" : Le cycle de vie opérationnel
L'écriture et la validation d'un ADR doivent être intégrées nativement dans votre flux Git.

Voici les 4 étapes de sa création :

Étape 1 : La Proposition (Statut : Proposé)
Un développeur (ou vous-même) identifie un point de blocage ou un choix d'architecture. Il duplique le fichier adr-template.md, le remplit en local sur une branche Git dédiée (ex: feat/adr-gestion-tokens) et passe le statut à Proposé.

Étape 2 : La Revue Technique (Le Consensus)
La proposition est poussée sur GitHub via une Pull Request. C'est le moment d'échanger. L'autre développeur et l'architecte relisent l'ADR directement dans les commentaires de la PR. On valide les avantages et on accepte explicitement les inconvénients.

Étape 3 : L'Acceptation (Statut : Accepté)
Une fois l'accord trouvé, l'auteur passe le statut à Accepté. La PR est fusionnée sur la branche develop. À ce moment-là, la décision devient une loi technique pour le projet.

Étape 4 : L'Obsolescence (Statut : Obsolète)
C'est la règle d'or : On ne modifie jamais un ADR validé dans le passé. Si, dans un an, vos besoins changent et que vous devez abandonner une décision de l'ADR #002, vous créez l'ADR #015. Dans l'ADR #015, vous expliquez la nouvelle décision, et vous changez simplement le statut de l'ADR #002 en Obsolète (en y ajoutant un lien vers le #015). Votre historique reste propre.

3. Le "Où" : La conservation dans le Monorepo
Pour que Claude Code et vos équipes y accèdent instantanément, les ADR doivent être stockés selon une convention de nommage stricte.

Emplacement : claude-collaboration/history/

Format du nom de fichier : ADR-[Numéro]-[titre-en-minuscules].md

Exemple : ADR-001-choix-postgresql.md

Exemple : ADR-002-commissions-chauffeurs-immutables.md

L'avantage avec Claude Code
En centralisant les ADR à cet endroit, la commande /startup que nous avons configurée permet à Claude, au début de chaque session, de lire en quelques millisecondes l'intégralité des décisions passées. L'IA ne vous proposera jamais une solution technique qui va à l'encontre d'un ADR validé.

Souhaitez-vous que nous rédigions ensemble l'ADR officiel concernant la stratégie de gestion de la sécurité et de l'authentification des utilisateurs (JWT, sessions, rôles Admin/Chauffeur) pour le backend Spring Boot et React ?