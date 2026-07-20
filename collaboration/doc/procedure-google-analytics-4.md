# Procédure — Google Analytics 4 : création de propriété et configuration du site

> Documente comment (re)créer une propriété GA4 pour `www.klemtech.net` et la relier au site. Utile pour une rotation de propriété, un nouvel environnement, ou pour qu'une autre personne de l'équipe reproduise la configuration sans dépendre de cette session.
>
> Contexte technique de l'intégration : voir [DEC-047](../history/decision-log.md) et [ADR-011](ard/ADR-011-integration-google-analytics-4.md).

---

## 1. Créer la propriété GA4 (côté Google, une fois)

1. Se connecter sur [analytics.google.com](https://analytics.google.com) avec le compte Google qui doit administrer les statistiques du site.
2. **Admin** (roue crantée en bas à gauche) → colonne *Compte* → sélectionner ou créer le compte Google Analytics de KLEM.
3. Colonne *Propriété* → **Créer une propriété** :
   - Nom de la propriété : `KLEMTECH` (ou équivalent explicite)
   - Fuseau horaire des rapports : `(GMT+00:00) Abidjan` (ou fuseau de référence choisi pour le reporting)
   - Devise : selon le besoin de reporting (XOF/EUR/USD)
4. Renseigner les informations sur l'activité (secteur, taille) — n'affecte pas le fonctionnement, seulement les recommandations Google.
5. **Créer un flux de données** → **Web** :
   - URL du site : `https://www.klemtech.net`
   - Nom du flux : `KLEMTECH`
   - Laisser la **mesure améliorée** (*Enhanced measurement*) activée — elle capture automatiquement scroll, clics sortants, recherche sur site, téléchargements de fichiers, sans code supplémentaire.
6. Une fois le flux créé, noter les deux identifiants affichés dans **Détails du flux** :
   - **ID de flux** (interne à GA, ex. `15289565572`) — informatif, pas utilisé dans le code
   - **ID de mesure** (`G-XXXXXXXXXX`) — **c'est celui-ci qu'il faut copier dans le site**

Pour ce site, l'ID de mesure actuel est `G-TNR3CBT1NN` (flux `KLEMTECH`, ID de flux `15289565572`).

## 2. Configurer le site pour utiliser cet ID (côté dépôt)

Le tag n'est **jamais codé en dur** — il est lu depuis une variable d'environnement (cf. DEC-006, même schéma que les autres clés API du projet).

### En local (développement)
Déjà fait dans `.env` du dépôt local :
```
KLEM_GA_MEASUREMENT_ID=G-TNR3CBT1NN
```
Sans effet en local : le tag ne se charge que si `WP_ENV=production` (jamais le cas en dev/Docker).

### En production (Hostinger)
**Étape restant à faire manuellement, en dehors du dépôt Git** (le `.env` de production n'est jamais versionné) :

1. Se connecter à hPanel → Gestionnaire de fichiers (ou SSH/SFTP) sur l'hébergement de `www.klemtech.net`.
2. Ouvrir le fichier `.env` à la racine du projet (même niveau que `web/`).
3. Ajouter la ligne :
   ```
   KLEM_GA_MEASUREMENT_ID=G-TNR3CBT1NN
   ```
4. Vérifier que `WP_ENV=production` est bien déjà présent dans ce même fichier (condition pour que le tag se déclenche).
5. Sauvegarder — aucun redémarrage serveur nécessaire, la variable est relue à chaque requête PHP (`wp-config.php` la charge via `getenv()`).

Si l'ID de mesure change un jour (rotation de propriété), il suffit de mettre à jour cette seule ligne — aucune modification de code n'est nécessaire.

## 3. Vérifier que le tag fonctionne

1. Ouvrir `https://www.klemtech.net` **sans être connecté** en tant qu'administrateur ou partenaire (le tag est volontairement désactivé pour ces comptes, cf. ADR-011).
2. Un bandeau « Nous utilisons des cookies… » doit apparaître en bas à gauche (sauf si un choix a déjà été enregistré sur ce navigateur).
3. Cliquer **Accepter**.
4. Dans GA4 → **Rapports** → **Temps réel** : la visite doit apparaître dans les secondes qui suivent.
5. Alternative plus détaillée : extension Chrome **Google Tag Assistant** ou **GA4 DebugView** (Admin → DebugView) — utile pour vérifier les événements de la mesure améliorée (scroll, clics sortants…).
6. Vérifier aussi le cas « Refuser » : après clic, aucune nouvelle visite ne doit apparaître dans Temps réel pour ce navigateur.

## 4. Connecter la propriété à Looker Studio

1. Aller sur [lookerstudio.google.com](https://lookerstudio.google.com).
2. **Créer** → **Rapport**.
3. **Ajouter des données** → connecteur **Google Analytics** (natif, pas besoin d'API key) → se connecter avec le même compte Google que celui utilisé pour créer la propriété.
4. Sélectionner le compte GA, puis la propriété `KLEMTECH` (flux GA4, pas Universal Analytics).
5. Construire les visualisations utiles au suivi des KPI : sessions dans le temps, pages les plus visitées, taux d'engagement, canaux d'acquisition (organique, direct, réseaux sociaux, referral).

## 5. Événements personnalisés par CTA (implémenté — voir DEC-048)

En plus de la mesure améliorée automatique, deux événements custom sont envoyés depuis `src/main.js` :

| Événement GA4 | Déclenché par | Paramètres |
|---|---|---|
| `cta_click` | Clic sur un des 4 CTA de la section Services (« Planifier un atelier data », « Discuter de mon application », « Demander une démo FleetControl », « Obtenir une estimation infra ») ou sur une carte « Secteur ciblé » (section Clients) — tous partagent l'attribut HTML `data-sector` | `cta_label` (libellé du CTA/secteur), `cta_location` (id de la section : `services` ou `clients`) |
| `generate_lead` | Soumission réussie du formulaire de contact (`#klem-contact-form`) | `form_id: 'contact'` |

Ces événements ne remontent que dans les mêmes conditions que le tag principal (production, consentement accordé). Pour transformer `generate_lead` (et éventuellement `cta_click`) en objectif de conversion dans les rapports GA4 :

1. GA4 → **Admin** → **Événements**.
2. Repérer `generate_lead` dans la liste (apparaît après au moins une soumission de formulaire réelle).
3. Activer le bouton **Marquer comme événement clé** (*Mark as key event*).
4. Optionnel : faire de même pour `cta_click` si un suivi de conversion par CTA (et pas seulement par lead final) est utile au reporting.

## 6. Limites actuelles de l'intégration (état à 2026-07-20)

- Le bandeau de consentement est **binaire** (accepter/refuser tout GA) — suffisant tant qu'aucun autre outil (publicité, remarketing) n'est ajouté. À revoir si un tel besoin apparaît.
- Les CTA génériques qui ne portent pas l'attribut `data-sector` (ex. « Lancer mon projet » du footer, « Parler à un expert » en bas de la section Services, CTA du header) ne sont pas encore instrumentés individuellement — seul leur effet final (page vue de `#contact`, ou lead si le formulaire est soumis) est visible dans les rapports.
