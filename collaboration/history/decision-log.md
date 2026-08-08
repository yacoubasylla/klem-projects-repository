# Registre des Décisions Techniques (Decision Log)

Ce document centralise les choix architecturaux et stratégiques majeurs pour **KLEM Technologies & Services**. Chaque entrée doit suivre le format ADR (Architecture Decision Record) pour garantir la traçabilité de nos choix.

## 📋 Index des Décisions

| ID | Date | Sujet | Statut |
|:---|:---|:---|:---|
| 001 | 2026-06-18 | Adoption du Monorepo (pnpm/Turborepo) | Accepté |
| 002 | 2026-06-19 | Standardisation via CLAUDE.md hiérarchique | Accepté |
| 003 | 2026-08-08 | Adoption de KLEM_MASTER_SYSTEM_DIRECTIVE.md et périmètre d'application | Accepté |

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

### ADR 003 : Adoption de KLEM_MASTER_SYSTEM_DIRECTIVE.md et périmètre d'application
- **Contexte :** `KLEM_MASTER_SYSTEM_DIRECTIVE.md` v2.0 (architecture cible KLEM DataSphere — Java 21, Spring Boot 3.x, OAuth2 Resource Server) coexistait avec des `CLAUDE.md` d'apps clients déjà en production sur des conventions différentes (Java 17, trois modèles d'auth distincts), sans qu'aucun document ne relie les deux.
- **Décision :** La directive maître fait autorité pour les nouveaux services KLEM DataSphere sous `services/*` (ex. `transit-ops-service`, `referentiel-api-service`). Les apps clients existantes gardent leur `CLAUDE.md` propre ; toute convergence est une migration dédiée et tracée, jamais un remplacement silencieux. Détail complet : `collaboration/history/adr/2026-08-08-adoption-directive-maitre-datasphere-perimetre.md`.
- **Conséquences :**
    - **Avantages :** la directive devient effectivement référencée depuis le reste du workspace ; le scaffolding des produits pivots peut démarrer sans casser l'existant.
    - **Dette :** trois modèles d'authentification distincts restent en place, documentés comme dette explicite plutôt que masqués.

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