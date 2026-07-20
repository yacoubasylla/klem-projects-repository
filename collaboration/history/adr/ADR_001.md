# ADR 001 : Choix de l'architecture de gestion de flotte

**Date:** 2026-06-18
**Statut:** Accepté

## Contexte
Besoin de gérer les missions de véhicules en temps réel.

## Décision
Utilisation de `ai.koog` pour l'agent IA de gestion et `Kafka` pour la gestion des événements de missions.

## Conséquences
- **Positif:** Temps réel assuré, découplage des services.
- **Négatif:** Complexité accrue de l'infrastructure de streaming.