# Utilité du script create-adr.sh

1. **`Comment l'utiliser`** 
Avant de l'utiliser pour la première fois, vous devez : 
- rendre le script exécutable dans votre terminal
- Donner les droits d'exécution :
    
    chmod +x scripts/create-adr.sh


2.  **`Créer un nouvel ADR (exemple pour la gestion de parc)`**
    ```bash
./scripts/create-adr.sh choix-workflow-mission

Cela créera automatiquement le fichier docs/adr/2026-06-18-choix-workflow-mission.md avec le titre pré-rempli et la structure prête à être complétée.

### Pourquoi ce petit script va vous changer la vie :
- Standardisation forcée : Toute l'équipe utilisera exactement la même structure de document.
- Rapidité : Plus besoin de copier-coller manuellement le template.
- Discipline : En intégrant cette étape dans votre workflow, le fait de créer un ADR devient un réflexe plutôt qu'une corvée administrative.