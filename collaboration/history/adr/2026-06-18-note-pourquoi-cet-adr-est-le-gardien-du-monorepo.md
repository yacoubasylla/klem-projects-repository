Pourquoi cet ADR est "le gardien" de votre monorepo :
Référence pour l'IA : Quand votre équipe utilisera Claude Code ou Cursor, le fait d'avoir ce document permet à l'IA de savoir comment elle doit structurer son code. Si un développeur demande : "Crée-moi un utilitaire pour formater les dates", l'IA, en lisant cet ADR, saura qu'elle doit le mettre dans @klem/utils et non dans l'application locale.

Autorité technique : Cela définit la "loi" du projet. Si vous avez une revue de code avec votre développeur senior, cet ADR sert d'argument neutre pour justifier pourquoi un morceau de code doit être déplacé dans un package.

Scalabilité : Vous êtes maintenant prêt à accueillir plus de développeurs. Ils n'auront pas à vous demander "où dois-je mettre ce code ?", ils liront cet ADR et sauront instantanément où il doit aller.