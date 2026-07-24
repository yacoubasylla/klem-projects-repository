# ADR-005 · Authentification JWT Stateless (Spring Security 6 + jjwt 0.12.3)

- **Date** : 2026-06-30
- **Statut** : Accepté
- **Auteur** : Yacouba SYLLA

---

## Contexte

Le système Cantine Connect est une application multi-établissements. Les utilisateurs (ADMIN, GESTIONNAIRE, CAISSIER) s'authentifient via un tableau de bord web. Les contraintes :

- Plusieurs établissements peuvent être gérés depuis une même instance du serveur
- Architecture potentiellement scalable horizontalement (plusieurs instances backend)
- Pas d'état partagé entre requêtes (API REST pures)
- Trois niveaux de rôles avec des autorisations différentes

## Décision

**Mécanisme** : JWT (JSON Web Token) HMAC-SHA512 via la bibliothèque `jjwt 0.12.3`  
**Transport** : En-tête HTTP `Authorization: Bearer <token>`  
**Durée de vie** : 24h (configurable via `jwt.expiration-ms` dans `application.yml`)  
**Stockage client** : `localStorage` sous les clés `cc_token` et `cc_user`  
**Révocation** : Pas de révocation implémentée (stateless pur) — la déconnexion supprime le token côté client uniquement

## Implémentation

```
SecurityConfig (stateless, no CSRF)
    └── JwtAuthFilter (OncePerRequestFilter)
            ├── Extrait le token du header Authorization
            ├── Valide via JwtService.isTokenValid()
            └── Charge UserDetails via AuthService.loadUserByUsername()
                        └── SecurityContextHolder.setAuthentication(...)

AuthService (UserDetailsService)
    ├── login(LoginRequestDTO) → valide mdp BCrypt → génère token
    └── loadUserByUsername(email) → charge Utilisateur depuis BDD

JwtService
    ├── generateToken(UserDetails) → Claims + signature HMAC-SHA512
    ├── extractUsername(token) → email depuis sub claim
    └── isTokenValid(token, userDetails) → vérifie signature + expiration
```

## Alternatives Évaluées

| Option | Raison du rejet |
|--------|----------------|
| Sessions HTTP + cookies | Incompatible avec le scaling horizontal. État serveur partagé nécessaire (Redis ou sticky sessions). |
| OAuth2 / Keycloak | Overhead d'infrastructure trop important pour le contexte d'une PME en Côte d'Ivoire. |
| Basic Auth | Non sécurisé sans HTTPS permanent. Pas de notion de session ou d'expiration. |

## Conséquences

- **Positives** : Stateless, scalable, simple à déboguer (JWT décodable sur jwt.io). Pas de gestion de sessions serveur.
- **À surveiller** : Pas de révocation de token avant expiration. Si un utilisateur est désactivé, son token reste valide jusqu'à expiration (24h max). Pour les cas critiques, envisager une blacklist de tokens en Redis dans une version future.
- **Rôles implémentés** : `ADMIN` (gestion complète), `GESTIONNAIRE` (gestion élèves et structures), `CAISSIER` (scan et paiements uniquement).
