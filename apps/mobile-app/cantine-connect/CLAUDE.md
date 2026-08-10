# Projet : Cantine Connect Mobile & PWA

## Contexte
Client mobile universel (Android/iOS/PWA) pour **Cantine Connect** — second client du
même `server-backend` Spring Boot que l'application web existante
(`apps/web-app/cantine-connect/`). Aucun nouveau backend, aucune duplication de
logique métier : ce module ne fait qu'appeler les endpoints réels déjà utilisés par
`client-frontend/` (web).

> Le web reste la référence de production sous engagement commercial (voir
> `apps/web-app/cantine-connect/CLAUDE.md` et `klem-labs-repository/projects/03_cantine_connect/`).
> Ce module mobile est un projet séparé — il ne remplace ni ne modifie le client web.

## Commandes
Toutes les commandes s'exécutent depuis `apps/mobile-app/cantine-connect/` :

| Action | Commande |
|---|---|
| Installer les dépendances | `pnpm install` (depuis la racine du monorepo, ou `npm install` en local) |
| Démarrer (dev, choix de plateforme au menu) | `npx expo start` |
| Démarrer directement en Web/PWA | `npx expo start --web` |
| Export statique PWA | `npx expo export -p web` |
| Build natif (EAS) | `eas build --platform android` / `--platform ios` |

## Stack
- Expo SDK 51 / Expo Router v3 (file-based routing, dossier `app/`)
- NativeWind v4 (Tailwind) pour le style
- TanStack Query v5 pour les appels réseau + cache
- Axios (`src/services/api.ts`) — JWT via `expo-secure-store` (natif) / AsyncStorage (web)
- `expo-camera` pour le scan QR côté caissier

## URL de l'API
`EXPO_PUBLIC_API_URL` (voir `.env.example`) — pointe vers le `server-backend` de
`apps/web-app/cantine-connect/`. Mêmes identifiants de connexion que le web (voir
son README pour les comptes de démo).

## Périmètre réellement câblé (backend existant, vérifié dans le code)
- Connexion (`POST /auth/login`)
- Solde + Pass QR par enfant (`GET /parents/moi` — `EnfantDTO.solde` / `qrCodeToken`)
- Historique des paiements (`GET /paiements`)
- Scan QR côté caissier (`POST /scan/{qrCodeToken}`, même endpoint que `ScanPage.jsx` web)

## Hors périmètre — ne pas câbler sans décision explicite
- **Pass QR dynamique (TOTP rotatif)** : idée documentée dans `CONCEPTION.md`, mais
  **le backend ne l'implémente pas**. Le Pass QR mobile affiche aujourd'hui le même
  `qrCodeToken` statique que le web/les badges PVC imprimés.
- **Menus du jour** : aucune entité/endpoint côté backend — écran placeholder
  (`app/(main)/menu.tsx`) tant que cette fonctionnalité n'est pas construite côté serveur.
- **`POST /meals/verify-pass`** : n'existe pas — ne pas s'y référer.
