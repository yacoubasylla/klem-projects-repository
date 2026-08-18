# ADR-021 : L'accès OTP remplace le formulaire "Demande d'accès" avec validation admin

**Date :** 2026-08-18
**Statut :** Accepté
**Décideur :** Yacouba SYLLA (avec Claude Code)

---

## Contexte

L'ADR-020 (même journée) avait délibérément conçu l'accès parent par OTP comme un mécanisme de
**connexion** réservé aux comptes déjà approuvés par un ADMIN via le formulaire "Demande d'accès"
existant (`DemandeAccesPage.jsx`, 3 étapes Identité/Contact/Résidence) — un numéro inconnu était
explicitement redirigé vers ce formulaire plutôt que de créer un compte à la volée, pour préserver
le contrôle anti-fraude déjà acté (`decision-log.md` : "Activation immédiate du compte parent à
la soumission — rejetée : aucun contrôle admin avant l'accès, risque de faux comptes en contexte
scolaire").

Instruction explicite et directe du porteur du projet, le même jour : **"Pour des questions de
facilité, nous allons apporter des modifications"** — le formulaire "Demande d'accès parent"
lui-même doit devenir le flux OTP : le parent saisit uniquement son numéro WhatsApp et son email,
reçoit un code, et ce code donne directement accès à la création/modification/suppression
d'enfants — sans étape de validation admin. Capture d'écran fournie pointant explicitement
`DemandeAccesPage.jsx` ("voici le formulaire à modifier") avec instruction de ne garder que
numéro + email (retrait de Nom/Prénom/Fonction/Contact/Résidence).

## Décision Retenue

> **Le formulaire public "Demande d'accès" (`/demande-acces`) est remplacé par le flux OTP à
> deux pages : (1) numéro WhatsApp + email → envoi du code ; (2) code → vérification, qui crée
> le compte PARENT et son profil à la volée si ce numéro n'en avait pas encore, puis affiche
> directement la gestion des enfants. Cette décision remplace explicitement le contrôle admin
> préalable acté précédemment (ci-dessus et ADR-020) — décision assumée du porteur du projet,
> pas une interprétation de Claude Code.**

Implémentation :
- `ParentOtpService.envoyerOtp(whatsappNumber, email)` : fonctionne désormais que le numéro
  corresponde à un compte existant ou non (ne lève plus d'erreur pour un numéro inconnu).
  `OtpStore`/`InMemoryOtpStore` conservent l'email soumis le temps de la vérification (5 min).
- `ParentOtpService.verifierOtp` : si aucun compte PARENT actif n'existe pour ce numéro, crée
  `Utilisateur` (rôle PARENT, mot de passe aléatoire jamais communiqué — connexion OTP uniquement)
  + `Parent`, en réutilisant le style déjà en place dans `DemandeAccesService.valider` (génération
  de mot de passe, vérification d'unicité email). **Aucun nom/prénom n'est collecté** par ce
  formulaire — remplacés par un intitulé générique (`nom="Parent"`, `prenom=<téléphone>`),
  modifiable ensuite par un ADMIN si besoin (`UtilisateurController` existant, inchangé).
- Frontend : `DemandeAccesPage.jsx` réécrit entièrement (l'ancien stepper Identité/Contact/
  Résidence, react-hook-form + zod, est retiré) — page 1 = numéro + email + code (même composant
  que l'ex-`ParentOtpAccessPage.jsx`, désormais supprimé et fusionné ici) ; page 2 = `<MesEnfantsPage
  />` (déjà existant) rendu directement dans ce même flux public, sans redirection vers l'espace
  authentifié complet. Route `/acces-otp` retirée (fusionnée dans `/demande-acces`).

## Alternatives envisagées

- **Conserver le contrôle admin, ajouter juste le champ email au flux OTP existant** (ADR-020
  inchangé) — écartée : contredit l'instruction explicite et directe reçue ("pour des questions
  de facilité"), qui vise justement à retirer cette étape.
- **Garder les deux formulaires en parallèle** (`/demande-acces` avec validation admin ET
  `/acces-otp` sans) — écartée : source de confusion (deux chemins pour le même besoin, lequel
  privilégier ?) et contredit l'instruction explicite pointant `DemandeAccesPage.jsx` comme LE
  formulaire à modifier.
- **Exiger nom/prénom malgré tout** (contournement de la contrainte `NOT NULL` de `Utilisateur`
  par une valeur par défaut plus élaborée) — écartée : l'instruction est explicite ("Le parent ne
  doit saisir que son numéro whatsapp et email (pas de nom, prénom, fonction etc...)").

## Conséquences et Impacts

### ✅ Impacts Positifs (Gains)
- Un seul formulaire d'accès parent, cohérent avec l'instruction reçue, moins de friction pour
  les familles (aucune attente de validation admin).
- 68/68 tests backend verts (62 existants adaptés/inchangés + nouveaux cas de création de compte
  à la volée dans `ParentOtpServiceTest`).

### ⚠️ Impacts Négatifs ou Risques (Compromis acceptés explicitement)
- **Perte du contrôle anti-fraude admin** que l'ADR-020 et une décision antérieure avaient
  spécifiquement mis en place — assumé explicitement par le porteur du projet, pas une régression
  silencieuse. À surveiller si des comptes/enfants frauduleux apparaissent en pratique.
- Le back-office de validation des demandes d'accès (`DemandeAccesService`, `AccesController`,
  `DemandesAccesPage.jsx`, entité `DemandeAcces`) **n'est pas supprimé** mais devient orphelin :
  plus aucun appel `POST /api/v1/demandes-acces` depuis le frontend public. Conservé tel quel
  (non-régression, pas demandé de le retirer) — à réévaluer séparément si confirmé inutile.
- Aucun nom/prénom réel n'est associé au compte parent créé par OTP — un ADMIN devra corriger ces
  champs manuellement si l'identité réelle du parent est nécessaire ailleurs dans l'application.
- `react-hook-form`/`zod` (packages npm) deviennent inutilisés dans `client-frontend` (n'étaient
  utilisés que par l'ancien formulaire) — non retirés de `package.json` dans cette passe (hors
  périmètre de la demande), à nettoyer séparément si souhaité.

---
## Suivi et Validation
- [x] Code mis à jour selon l'ADR.
- [x] Fichier `history-log.md` mis à jour après implémentation.
