# ADR #00X : [Titre Clair et Affirmation de la Décision]

*   **Statut** : [PROPOSÉ | ACCEPTÉ | REJETÉ | OBSOLÈTE]
*   **Date** : 2026-06-21
*   **Auteur(s)** : [Nom de l'Architecte / Développeur]
*   **Impact** : [Élevé | Moyen | Faible] (Impact sur le reste du système)
*   **Modules concernés** : [ex: apps/backend-api, apps/web-app, packages/ui]

---

## 1. Contexte et Problématique
*Décrire le défi technique ou métier auquel l'équipe est confrontée. Pourquoi devons-nous prendre une décision maintenant ? Quelles sont les contraintes (temps, budget, performance, compétences) ?*

> **Exemple :** Pour le module "Revenus & Paiements", nous devons intégrer le paiement des factures de location par Mobile Money (Orange, MTN, Wave). L'API doit être capable de gérer les callbacks asynchrones des opérateurs en cas de retard de traitement du réseau mobile sans bloquer l'expérience utilisateur sur le front React.

## 2. Décision
*Énoncer la décision de manière claire, directe et impérative. Expliquer pourquoi cette solution a été retenue par rapport au contexte.*

**Nous battrons en retraite sur l'approche X et nous utiliserons :** [La solution retenue]

> **Exemple :** Nous utiliserons un pattern d'architecture orientée événements pour les paiements. L'API Spring Boot recevra le webhook de l'opérateur, enregistrera immédiatement la transaction avec le statut `EN_COURS` dans PostgreSQL, et libérera le thread. Un service de tâche planifiée Spring (`@Scheduled`) ou un consommateur asynchrone validera définitivement le paiement dès réception de la confirmation finale.

## 3. Alternatives Évaluées
*Lister brièvement les autres options sérieuses qui ont été étudiées, et la raison chirurgicale de leur rejet.*

*   **Option A : [Nom de l'alternative]**
    *   *Avantage* : ...
    *   *Raison du rejet* : ...
*   **Option B : [Nom de l'alternative]**
    *   *Avantage* : ...
    *   *Raison du rejet* : ...

> **Exemple d'alternative rejetée :** 
> * **Traitement Synchrone Direct** : Bloquer la requête HTTP du client React tant que l'opérateur Mobile Money n'a pas répondu.
> * *Raison du rejet* : Risque élevé de timeout HTTP (supérieur à 30 secondes) si le réseau de l'opérateur est saturé, dégradant gravement l'expérience utilisateur.

## 4. Conséquences
*Chaque choix technique est un compromis. Lister honnêtement les impacts positifs (gains) et négatifs (dettes techniques acceptées, complexité ajoutée).*

### ✅ Impacts Positifs (Gains)
*   ...
*   ...

### ⚠️ Impacts Négatifs (Compromis & Points de vigilance)
*   ...
*   ...

> **Exemple de conséquences :**
> * **Positif** : Fluidité totale de l'IHM React (MUI). L'utilisateur voit instantanément l'écran "Traitement en cours" et peut continuer à naviguer.
> * **Négatif** : Complexité technique accrue côté Spring Boot (gestion des états de transaction complexes et des doublons potentiels de webhooks).

---

## 5. Approbation et Signatures

*   **Architecte Logiciel** : [ ] Validé  |  [ ] Refusé
*   **Développeur 1** : [ ] Validé  |  [ ] Refusé
*   **Développeur 2** : [ ] Validé  |  [ ] Refusé

*Note : Une fois les cases cochées et le fichier fusionné sur la branche `develop`, la décision devient une loi architecturale pour le projet.*