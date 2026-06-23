# Registre des Décisions Techniques (Decision Log)

Ce document centralise les choix architecturaux et stratégiques majeurs pour **KLEM Technologies & Services**. Chaque entrée doit suivre le format ADR (Architecture Decision Record) pour garantir la traçabilité de nos choix.

## 📋 Index des Décisions

| ID | Date | Sujet | Statut |
|:---|:---|:---|:---|
| 001 | 2026-06-18 | Adoption du Monorepo (pnpm/Turborepo) | Accepté |
| 002 | 2026-06-19 | Standardisation via CLAUDE.md hiérarchique | Accepté |

---

## 🏛 Détail des Décisions

### ADR 001 : Adoption d'une structure Monorepo
- **Contexte :** Multiplication des projets (Gestion parc, Data plateforme, Site vitrine) entraînant une duplication de code et une fragmentation des dépendances.
- **Décision :** Utilisation de `pnpm workspaces` combiné à `Turborepo` pour centraliser le build et les dépendances.
- **Conséquences :** 
    - **Avantages :** Uniformisation du Design System (@klem/ui), facilité de mise à jour.
    - **Dette :** Nécessite une discipline rigoureuse sur la gestion des versions de paquets et la documentation.

### ADR 002 : Standardisation via CLAUDE.md hiérarchique
- **Contexte :** Nécessité d'aligner l'IA sur les standards de KLEM quel que soit le sous-projet.
- **Décision :** Implémentation d'un `CLAUDE.md` racine (règles globales) couplé à des `CLAUDE.md` spécifiques dans chaque dossier (spécificités techniques par projet).
- **Conséquences :**
    - **Avantages :** Autonomie accrue de l'IA, réduction des erreurs de contexte.
    - **Dette :** Nécessite une maintenance à chaque changement majeur de stack technique.

---

## 💡 Comment ajouter une nouvelle décision ?

Pour toute nouvelle décision impactant l'architecture, créez une nouvelle section en suivant ce canevas :

> ### ADR XXX : [Titre explicite]
> - **Contexte :** Quel était le problème initial ? Quelle était la contrainte ?
> - **Décision :** Quelle solution a été choisie ?
> - **Conséquences :** Quels sont les avantages immédiats et quelle dette technique potentielle avons-nous acceptée ?
> - **Schéma (optionnel) :** Utiliser Mermaid.js si nécessaire pour illustrer l'architecture.


## 📐 Visualisation de l'Architecture (Mermaid)

### Structure du Monorepo KLEM
Ce diagramme illustre comment vos applications consomment vos briques partagées.

```mermaid
graph TD
    subgraph Apps
        A[Gestion de Parc]
        B[Site Vitrine]
        C[Data Plateforme]
    end

    subgraph Packages
        D[@klem/ui]
        E[@klem/utils]
        F[@klem/data-schemas]
    end

    A --> D
    A --> E
    C --> F
    C --> E
    B --> D