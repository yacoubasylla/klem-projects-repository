# FleetControl Backend - Spring Boot API

API RESTful gérant l'intelligence métier de FleetControl.

## 🛠️ Stack Technique
- Java 17 / Spring Boot 3.x
- Spring Data JPA & Hibernate
- PostgreSQL

## 🏛️ Architecture en Couches Strictes
Chaque module métier (ex: `maintenance`, `course`, `location`) doit respecter la structure suivante :
1. **`Controller`** : Point d'entrée HTTP. Reçoit les DTO, valide les requêtes, ne contient *aucune* logique métier.
2. **`Service`** : Contient l'intelligence d'affaires et la logique de calcul (ex: calcul des commissions de chauffeurs). Gère les transactions (`@Transactional`).
3. **`Repository`** : Interfaces Spring Data pour les requêtes de base de données.
4. **`Model/Entity`** : Classes persistantes mappées sur les tables PostgreSQL.

## 💾 Gestion de la Base de Données
- Toute modification du schéma SQL doit passer par un script de migration (Flyway/Liquibase) situé dans `src/main/resources/db/migration`. 
- **Interdiction stricte** de laisser Hibernate modifier la base de données en production (`spring.jpa.hibernate.ddl-auto=validate`).