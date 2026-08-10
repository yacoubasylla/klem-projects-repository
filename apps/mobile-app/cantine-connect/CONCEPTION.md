# Cantine Connect Mobile — Conception fonctionnelle

## 1. Positionnement
Client mobile/PWA de Cantine Connect, pour les parents (solde, Pass QR, historique
des paiements) et les agents de caisse (scan QR au réfectoire). Consomme le même
`server-backend` que le web — aucune fonctionnalité inventée côté API.

## 2. Pass QR — état réel vs. piste d'évolution

**Réel, implémenté :** chaque élève a un `qrCodeToken` (UUID) statique, généré une
fois côté backend. Le web l'affiche déjà (badges PVC imprimés, page Élèves) via
`QRCodeSVG` avec la valeur brute du token — le mobile fait exactement pareil
(`react-native-qrcode-svg`, `app/(main)/index.tsx`). Le scan (`POST /scan/{token}`)
est le même endpoint que le poste de contrôle réfectoire web.

**Piste d'évolution — non implémentée :** un jeton éphémère (TOTP, ~30s) pour
empêcher le partage d'une capture d'écran du Pass QR. Nécessiterait côté backend :
un algorithme de génération/validation TOTP par élève, un nouvel endpoint de
vérification, et une clé secrète par élève à provisionner. **Ne pas construire cet
écran mobile sur cette hypothèse tant que la décision et le travail backend
correspondant n'ont pas été faits** — voir ADR à créer si cette évolution est retenue.

## 3. Paiement Mobile Money (CinetPay)
Le paiement (rechargement) reste initié depuis le web pour l'instant
(`POST /paiements/initier`, commission CinetPay ≈ 2,5–3,5 %, voir l'offre financière
Cantine Connect). Le mobile affiche l'historique en lecture (`GET /paiements`) ;
l'initiation de paiement depuis le mobile n'est pas dans le périmètre actuel.

## 4. Menus du jour
Aucune fonctionnalité de gestion des menus n'existe côté backend à ce jour. L'écran
mobile correspondant est un placeholder honnête, pas une fonctionnalité câblée.
