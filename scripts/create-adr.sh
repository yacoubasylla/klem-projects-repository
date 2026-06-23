#!/bin/bash

# Vérifier si un nom de sujet est fourni
if [ -z "$1" ]; then
    echo "Usage: ./scripts/create-adr.sh <nom-du-sujet>"
    exit 1
fi

DATE=$(date +%Y-%m-%d)
FILENAME="docs/adr/${DATE}-${1}.md"

# Créer le template ADR
cat <<EOF > "$FILENAME"
# ADR : $(echo "$1" | sed 's/-/ /g' | awk '{for(i=1;i<=NF;i++)sub(/./,toupper(substr($i,1,1)),$i)}1')

- **Date :** $DATE
- **Statut :** Proposé
- **Auteurs :** [Votre Nom]

## Contexte
[Décrivez le problème ici]

## Décision
[Décrivez la solution retenue]

## Alternatives envisagées
[Options écartées et raisons]

## Conséquences
- **Avantages :** 
- **Risques/Dettes :** 
EOF

echo "ADR créé avec succès : $FILENAME"