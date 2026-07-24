# 🍽️ Cantine Connect — Système de Gestion de Cantine Scolaire Multi-Établissements

> **KLEM Technologies & Services** | Version 1.0.0-beta | MVP Opérationnel — Juillet 2026

---

## 🎯 Présentation

**Cantine Connect** est une plateforme digitale cloud-native conçue pour automatiser la gestion complète de la restauration scolaire multi-établissements en Côte d'Ivoire :

- 📋 Inscription et gestion centralisée des élèves par établissement et classe
- 💳 Paiement Mobile Money (Orange, MTN, Moov, Wave) via CinetPay / PayDunya
- 📷 Contrôle d'accès réfectoire en temps réel par scan de QR Code (< 1 seconde)
- 📊 Tableau de bord de pilotage avec statistiques globales
- 🔒 Traçabilité exhaustive de toutes les opérations (table `action_logs`)

---

## 🚀 Déploiement Production

| Service | Plateforme | URL |
|---------|-----------|-----|
| **Frontend (React)** | Vercel | _(URL Vercel du projet)_ |
| **Backend (Spring Boot)** | Railway | _(URL Railway du projet)_ |
| **Base de données** | Railway PostgreSQL | Managée automatiquement |

---

## 🛠️ Stack Technique

### Frontend
- **React.js 18** propulsé par **Vite** (JavaScript pur)
- **Material UI (MUI) v9** — Design System unique
- **Axios** — Client HTTP avec intercepteurs JWT
- **React Router v6** — Navigation SPA
- **3 thèmes switchables** : 🏢 Corporatif (dark) / ✨ Moderne (bling) / 🇨🇮 École Ivoirienne

### Backend
- **Java 17 + Spring Boot 3.x** — Architecture en couches
- **Spring Security + JWT Stateless** (HMAC-SHA512)
- **Spring Data JPA** (Hibernate 6) + **JPA Specifications** (Criteria API)
- **PostgreSQL** — Moteur de base de données

---

## 📁 Structure du Projet

```
cantine-connect/
├── client-frontend/          # Application React (Vite)
│   ├── src/
│   │   ├── components/       # Composants réutilisables (AProposDialog, ThemeSwitcher…)
│   │   ├── context/          # Contextes React (Auth, Theme)
│   │   ├── hooks/            # Custom hooks (useDashboard, useEleves, usePassages…)
│   │   ├── layouts/          # MainLayout (Drawer responsive)
│   │   ├── pages/            # Pages par module fonctionnel
│   │   ├── services/         # Clients API Axios
│   │   └── theme/            # Thèmes MUI (themes.js)
│   ├── vercel.json           # Config Vercel (rewrites SPA)
│   └── package.json
│
├── server-backend/           # API Spring Boot
│   ├── src/main/java/com/klem/cantine/
│   │   ├── auth/             # Authentification JWT
│   │   ├── dashboard/        # Statistiques globales
│   │   ├── eleve/            # Gestion des élèves
│   │   ├── etablissement/    # Établissements & Classes
│   │   ├── paiement/         # Transactions Mobile Money
│   │   ├── scan/             # Passages réfectoire + QR Code
│   │   └── common/           # ApiResponse, GlobalExceptionHandler
│   ├── Dockerfile            # Multi-stage (JDK→JRE alpine)
│   └── railway.toml          # Config healthcheck Railway
│
├── collaboration/
│   ├── context/CONTEXT.md    # Vision produit & enjeux métier
│   ├── doc/                  # Architecture, specs, workflows
│   └── history/              # Logs de livraisons et ADR
│
└── documentations/           # Manuels et cahier de recette (PDF)
```

---

## ⚙️ Installation & Lancement Local

### Prérequis
- Java 17+
- Node.js 20+
- PostgreSQL 15+ (ou Docker)

### Backend
```bash
cd server-backend

# Créer la base de données
createdb cantine_connect

# Configurer src/main/resources/application.yml
# (SPRING_DATASOURCE_URL, USERNAME, PASSWORD)

# Lancer
./mvnw spring-boot:run
# → http://localhost:8081
```

### Frontend
```bash
cd client-frontend

# Installer les dépendances
npm install

# Créer .env.local
echo "VITE_API_BASE_URL=http://localhost:8081/api/v1" > .env.local

# Lancer
npm run dev
# → http://localhost:5173
```

---

## 👤 Comptes de Référence (Développement — un par rôle)

| Email | Mot de passe | Rôle |
|-------|-------------|------|
| `admin@cantine.connect` | `admin@123` | ADMIN |
| `gestionnaire@cantine.connect` | `gestionnaire@123` | GESTIONNAIRE |
| `caissier@cantine.connect` | `caissier@123` | CAISSIER |
| `parent@cantine.connect` | `parent@123` | PARENT |

Chaque compte requiert un numéro de cellulaire unique (colonne `telephone`, obligatoire depuis la migration V5). Ces comptes sont recréés par la migration `V6__reset_comptes_un_par_role.sql` — voir `collaboration/history/adr/2026-07-01-migrations-source-unique-comptes-seed.md`. Ils ne sont plus affichés sur l'écran de connexion.

---

## 📦 Modules Fonctionnels

| Module | Statut | Description |
|--------|--------|-------------|
| 🏫 Établissements | ✅ Livré | CRUD établissements, niveaux, classes (masqué au rôle PARENT) |
| 👨‍🎓 Élèves | ✅ Livré | CRUD + formulaire 3 onglets + QR Code + pagination + recherche + export CSV (masqué au rôle PARENT) |
| 💳 Paiements | ✅ Livré | Transactions Mobile Money + webhooks CinetPay + recherche + filtres date/opérateur + export CSV (PARENT limité à ses enfants) |
| 📷 Scan Réfectoire | ✅ Livré | Validation QR Code temps réel + cache offline 24h (masqué au rôle PARENT) |
| 📋 Historique | ✅ Livré | Passages filtrés (date, établissement, résultat) + export CSV (PARENT limité à ses enfants) |
| 📊 Dashboard | ✅ Livré | KPIs globaux + tendance 7 jours + statistiques paiements |
| 🖨️ Rapports | 🔄 v1 exploratoire | États financiers/statistiques, paiements, passages — export PDF/Excel (GESTIONNAIRE/CAISSIER/ADMIN) |
| 👥 Utilisateurs | ✅ Livré | CRUD complet (ADMIN) + recherche (email/nom/prénom/téléphone) + filtres rôle/statut/date de création + téléphone obligatoire/unique + protection dernier admin |
| 👨‍👩‍👧 Parents | ✅ Livré | Rattachement compte PARENT ↔ élèves (ADMIN), recherche par email sur la liste, sélection assistée par numéro/nom/matricule dans le formulaire |
| ⚙️ Configuration | ✅ Livré | Paramètres tarifs et période de grâce |
| 🎨 Thèmes UI | ✅ Livré | 3 thèmes switchables persistés en localStorage |

Détail fonctionnel complet par rôle : `collaboration/doc/manuel-utilisateur.md`. Scénarios de recette : `collaboration/doc/cahier-de-recette.md`.

---

## 🔐 Sécurité

- JWT Stateless HMAC-SHA512 via en-tête `Authorization: Bearer`
- `GlobalExceptionHandler` (`@ControllerAdvice`) — erreurs standardisées JSON
- Interdiction de supprimer le dernier administrateur
- QR Code UUID v4 unique et inaltérable par élève

---

## 📞 Contact & Support

**KLEM Technologies & Services**
- 📞 +225 07 58 89 24 77
- 🌐 [www.klemtech.net](https://www.klemtech.net)
- 📧 infos@klemtech.net

---

*© 2026 KLEM Technologies & Services. Document confidentiel — Projet Cantine Connect.*
