# KLEM License Package Context

Ton rôle est de maintenir le module de vérification de licence KLEM (`@klem/license`).

- **Mode avertissement seul (décision du 2026-08-12) :** ce module ne bloque **jamais** le rendu
  d'une application, quel que soit le statut de la licence (absente, expirée, signature
  invalide, mauvaise application). Il ne fait que journaliser (`console.warn`) et, en
  développement (`import.meta.env.DEV`), afficher une bannière visuelle non bloquante. Ne pas
  réintroduire de blocage sans validation explicite — `apps/web-app/parcauto` et
  `apps/web-app/cantine-connect` n'ont pas de `KTS_LICENSE_KEY` réelle configurée aujourd'hui.
- **Vérification asymétrique, jamais de secret partagé côté client :** la validation utilise
  ECDSA P-256/SHA-256 (Web Crypto `crypto.subtle.verify`) avec une **clé publique** embarquée
  dans `validator.ts`. La clé privée correspondante ne doit **jamais** être committée dans ce
  dépôt — elle sert uniquement, hors-repo, à signer les jetons via
  `scripts/generate-demo-license.mjs` (ou un équivalent avec la vraie clé privée KLEM).
- **Aucune dépendance runtime nouvelle :** tout repose sur l'API Web Crypto native du
  navigateur/Node — pas de librairie JWT tierce.
