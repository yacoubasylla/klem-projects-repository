# Context: Backend Spring Boot
- **Stack :** Java, Spring Boot, Spring Data JPA, PostgreSQL.
- **Architecture :** Controller -> Service -> Repository (Clean Architecture).
- **Standards :** 
    - Documentation via OpenAPI/Swagger obligatoire.
    - Exceptions gérées par `@ControllerAdvice`.
    - Transactions (@Transactional) strictement délimitées au service.
- **Spécificité :** Performance des queries SQL et gestion du pool de connexions Oracle/Postgres.