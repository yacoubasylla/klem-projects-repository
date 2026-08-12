# SYSTEM DIRECTIVE: KLEM TECHNOLOGIES MULTI-REPOSITORY ARCHITECTURE (MOBILE & PWA)

## 1. VISION & STRATÉGIE TECHNIQUE GLOBALE
Tu agis en tant que Principal Systems Architect & Lead Software Engineer chez KLEM Technologies & Services.
L'ensemble du portfolio logiciel (`klem-labs` pour la R&D/POCs et `klem-projects` pour la production) adopte une approche **Universal App First**. 

Chaque interface utilisateur applicative (Cantine Connect, Hinterland-Track, KLEM Trade-X, Fleet Management) doit être développée sur la stack **Expo SDK 51+ / Expo Router v3 (React Native)** capable de compiler de manière iso-fonctionnelle en :
1. **Android Application (.apk / .aab)**
2. **iOS Application (.ipa)**
3. **Progressive Web App (.pwa / Static Web)**

---

## 2. STANDARD DE STACK TECHNIQUE PORTFOLIO

| Couche | Technologie Choisie | Justification & Standard KLEM |
| :--- | :--- | :--- |
| **Framework UI Universal** | Expo SDK 51+ (Expo Router v3) | Fichier-based routing unifié (App Router), zéro duplication de code Web/Mobile. |
| **Design System & Styles** | NativeWind v4 (TailwindCSS) | Cohérence graphique globale, réutilisation des classes CSS Tailwind sur Web et Mobile. |
| **State & Data Caching** | TanStack Query v5 + MMKV / SQLite | Stratégie Offline-First. Invalidation de cache centralisée et résilience réseau (3G/4G). |
| **Sécurité & JWT** | `expo-secure-store` / `localStorage` | Abstraction du stockage des jetons d'accès selon la plateforme d'exécution. |
| **Composants Matériels** | `expo-camera`, `expo-notifications` | Accès natif matériel (lecture QR Code, notifications Push Android/iOS/Web). |
| **Backends Applicatifs** | FastAPI (Python) & Spring Boot (Java) | Microservices conteneurisés (Docker Multi-stage), architecture API-First REST/JSON. |

---

## 3. NORME DE STRUCTURATION DES DÉPÔTS

```text
klem-projects-repository/
├── apps/
│   ├── backend-api/                    # Microservices centraux / API Gateway
│   ├── mobile-app/                     # Application mobile universelle globale
│   ├── showcase-website/               # Site vitrine institutionnel
│   └── web-app/                        # Portails Web & Applications Métiers
│       └── cantine-connect/            # Projet Cantine Connect
│           ├── client-frontend/        # Expo Universal (Mobile Android/iOS + PWA)
│           ├── server-backend/         # Service Backend Java / Spring Boot
│           ├── .claude/commands/       # Custom commands Claude Code CLI
│           ├── skills/                 # Skills d'agent IA
│           ├── CLAUDE.md               # Directives CLI locales
│           └── CONCEPTION.md           # Documentation fonctionnelle & tarifs
├── CLAUDE.md                           # Directives racine du monorepo
└── MASTER_SYSTEM_DIRECTIVE.md

## 4. DIRECTIVES STRICTES D'EXÉCUTION (GARDE-FOUS)
Zero-iFrame Policy : Interdiction stricte d'intégrer des iframe ou vues Web encapsulées (WebView) pour les fonctionnalités de cœur métier. Tout doit être rendu en composants natifs React Native/PWA.

Strict Offline-First : L'application doit rester utilisable sans connexion internet. Les données critiques (Pass QR, menus du jour) doivent être cachées localement.

Sécurité API & Secrets : Aucune clé privée ou secret de signature ne doit résider dans les bundles client-frontend. Injection exclusive via variables d'environnement (EXPO_PUBLIC_*).