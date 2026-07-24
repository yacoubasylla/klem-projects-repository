# Spécifications Opérationnelles — Cantine-Connect

> **Public visé :** Opérationnels — déploiement terrain, contraintes, rôles, SLA, continuité.

## Sommaire
1. [Contexte terrain](#1-contexte-terrain)
2. [Contraintes opérationnelles](#2-contraintes-opérationnelles)
3. [Processus métier impactés](#3-processus-métier-impactés)
4. [Rôles et responsabilités opérationnelles](#4-rôles-et-responsabilités-opérationnelles)
5. [Formation & Accompagnement au changement](#5-formation--accompagnement-au-changement)
6. [SLA / Niveaux de service attendus](#6-sla--niveaux-de-service-attendus)
7. [Plan de continuité (dégradé / offline)](#7-plan-de-continuité-dégradé--offline)

## 1. Contexte terrain
Le personnel de restauration opère à l'entrée du réfectoire avec son smartphone Android personnel
ou de service (pas de terminal dédié). Les gestionnaires d'établissement travaillent sur des postes
fixes ou tablettes à écran compact (13 à 17 pouces). Les parents accèdent au portail depuis des
smartphones variés, souvent en situation de mobilité, sur un réseau mobile de qualité hétérogène.

## 2. Contraintes opérationnelles
- **Connectivité** : réseau mobile instable côté cantine → l'application de scan doit être 100%
  opérationnelle offline, avec cache local chiffré de 24h et synchronisation différée dès retour
  réseau.
- **Équipement** : badge PVC personnalisé avec QR Code par élève (aucun équipement dédié pour le
  scan — smartphone Android existant du personnel) ; postes fixes/tablettes pour les gestionnaires.
- **Écrans compacts** : les formulaires de saisie (ex: fiche élève) doivent tenir sans défilement
  vertical — d'où le choix d'un formulaire à onglets plutôt qu'un long formulaire linéaire.
- **Langues** : français.
- **Mobile Money dominant** : le paiement Mobile Money (Orange, MTN, Moov, Wave via CinetPay/
  PayDunya) est le flux principal ; carte bancaire et virement bancaire restent secondaires.
- **Volumétrie cible** : 5 établissements, 600 élèves au pilote — architecture conçue pour scaler
  vers 10 000+ élèves.

## 3. Processus métier impactés
**Avant** : encaissement et suivi des paiements cantine 100% manuels (temps administratif élevé,
erreurs de saisie) ; aucun contrôle d'accès automatisé au réfectoire (risque sanitaire et de
fraude) ; données fragmentées entre établissements, aucun pilotage consolidé ; communication aux
parents réactive plutôt que proactive sur les impayés.

**Après** : le parent paie en ligne 24h/24 en Mobile Money, avec confirmation instantanée et
rappels automatiques (J-7, J-3, J-1) avant échéance ; l'accès au réfectoire est validé par scan QR
en moins d'une seconde avec un mode de tolérance configurable (période de grâce) ; toutes les
opérations sont journalisées de façon immuable pour l'audit comptable et la conformité ARTCI.

## 4. Rôles et responsabilités opérationnelles
| Rôle | Responsabilité | Fréquence d'action |
|---|---|---|
| Super Administrateur (`ADMIN`) | Configuration des tarifs et de la période de grâce, gestion des comptes utilisateurs, supervision multi-établissements, accès complet aux logs d'audit | Hebdomadaire / à la demande |
| Gestionnaire d'établissement (`GESTIONNAIRE`) | Inscription et mise à jour des élèves, suivi des paiements de son établissement, envoi de notifications, consultation des passages | Quotidienne |
| Agent de scan (`CAISSIER`) | Validation des QR Codes à l'entrée du réfectoire, gestion des cas de mode manuel avec motif | À chaque service (déjeuner) |
| Parent / Tuteur (`PARENT`) | Paiement de la cantine de ses enfants, consultation de l'historique et du statut d'accès | Selon échéance de paiement |

## 5. Formation & Accompagnement au changement
Livrables contractuels prévus : guide utilisateur parents (PDF illustré + vidéo tutoriel), guide
administrateur/gestionnaire (PDF + sessions de formation en présentiel), guide opérateur cantine
(guide de poche plastifié + formation sur site). Déploiement en 4 phases sur 8 semaines : cadrage,
développement MVP, pilote sur un établissement (formation équipes, ajustements), généralisation
(formation étendue, go-live complet).

## 6. SLA / Niveaux de service attendus
- Disponibilité plateforme : **99,5 %** minimum hors maintenance planifiée.
- Temps de réponse API paiements : **< 2 secondes (P95)**.
- Temps de validation cantine (scan QR) : **< 1 seconde par élève**.
- Délai de réponse support incidents P1 (critiques) : **< 2 heures ouvrables**.
- Délai de réponse support incidents P2 : **< 8 heures ouvrables**.
- Sauvegarde des données : automatique toutes les 24h, rétention 30 jours.
- Maintenance planifiée : notifiée 72h à l'avance, hors heures scolaires.

## 7. Plan de continuité (dégradé / offline)
L'application de scan télécharge un snapshot chiffré des élèves autorisés dès qu'une connexion est
disponible (stockage local, TTL 24h) ; en cas de coupure réseau côté réfectoire, la validation
continue de fonctionner sur ce cache local, avec anti-passback géré localement et écriture
différée dans une file de synchronisation ; au retour de connexion, la file de passages différés
est transmise à l'API et le cache est rechargé. Côté backend, une sauvegarde `pg_dump` automatique
nocturne (02h00) est chiffrée et exportée vers un stockage objet distant, rétention 30 jours.
