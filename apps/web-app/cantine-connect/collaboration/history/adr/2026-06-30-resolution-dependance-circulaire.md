# ADR-006 · Résolution Dépendance Circulaire Spring : PasswordEncoderConfig

- **Date** : 2026-06-30
- **Statut** : Accepté
- **Auteur** : Yacouba SYLLA

---

## Contexte

Lors de l'implémentation du module d'authentification JWT, Spring Boot refusait de démarrer avec l'erreur :

```
The dependencies of some of the beans in the application context form a cycle:
SecurityConfig → JwtAuthFilter → AuthService → PasswordEncoder ← SecurityConfig
```

Le cycle était causé par le fait que le bean `PasswordEncoder` était déclaré comme `@Bean` **dans** `SecurityConfig`, alors que `AuthService` (dépendance de `JwtAuthFilter`, elle-même dépendance de `SecurityConfig`) avait besoin de `PasswordEncoder` pour initialiser.

```
SecurityConfig
    ├── dépend de JwtAuthFilter
    │       └── dépend de AuthService
    │               └── dépend de PasswordEncoder ← déclaré dans SecurityConfig !
    └── déclare PasswordEncoder @Bean
```

## Décision

Extraction du bean `PasswordEncoder` dans une classe de configuration dédiée **`PasswordEncoderConfig.java`**, sans aucune dépendance vers d'autres beans Spring Security.

```java
// com.klem.cantine.auth.config.PasswordEncoderConfig
@Configuration
public class PasswordEncoderConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

Après cette extraction, le graphe de dépendances est acyclique :

```
PasswordEncoderConfig → crée PasswordEncoder (indépendant, 0 dépendance)
AuthService            → UtilisateurRepository + JwtService + PasswordEncoder ✓
JwtAuthFilter          → JwtService + UserDetailsService (AuthService) ✓
SecurityConfig         → JwtAuthFilter + UserDetailsService + PasswordEncoder ✓
```

## Alternatives Évaluées

| Option | Raison du rejet |
|--------|----------------|
| `@Lazy` sur l'injection de PasswordEncoder | Masque le problème sans le résoudre. Risque de NullPointerException au runtime si le bean lazy n'est pas encore créé. |
| `@Autowired` champ avec setter injection | Anti-pattern — viole l'injection par constructeur recommandée par Spring. |
| Supprimer AuthenticationManager et injecter PasswordEncoder directement dans AuthService | Première tentative — n'a pas résolu le cycle car le bean PasswordEncoder restait dans SecurityConfig. |

## Conséquences

- **Positives** : Cycle cassé proprement. Code plus lisible (responsabilité unique — PasswordEncoderConfig ne fait qu'une chose). `SecurityConfig` devient un bean constructeur-injecté standard.
- **Règle à retenir** : Dans Spring Security 6, le bean `PasswordEncoder` doit **toujours** être dans une classe de configuration indépendante si d'autres beans de la chaîne de sécurité en ont besoin.
- **Leçon** : Avec `@RequiredArgsConstructor` (Lombok), toutes les dépendances sont injectées par constructeur — le cycle est détecté immédiatement au démarrage, ce qui est préférable à une erreur runtime.
