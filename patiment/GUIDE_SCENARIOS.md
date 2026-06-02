# 🎯 Scénarios

### 1. **InscriptionScenario** - Tests d'inscription de clients
Teste tous les cas d'usage et cas d'erreur pour l'inscription de clients:

- Inscription valide avec données complètes
- Tentative d'inscription avec email déjà utilisé (doublon)
- Inscription avec données incomplètes (prénom vide)
- Inscription avec adresse invalide (null)
- Inscription avec prénom null
- Inscription avec email invalide (vide)

---

### 2. **ConnexionScenario** - Tests d'authentification
Teste tous les cas d'authentification d'utilisateurs:

- Connexion avec identifiants valides
- Connexion avec mot de passe incorrect
- Connexion avec email inconnu
- Connexion avec mot de passe vide
- Connexion avec email vide
- Connexion avec email et mot de passe vides

---

### 3. **ConsultationScenario** - Tests de demande de consultation
Teste la création et la gestion des consultations:

- Demande de consultation valide
- Demande avec client null
- Demande avec medium null
- Demande avec client et medium null
- Affichage de la liste des consultations

---

### 4. **StatistiqueScenario** - Tests des statistiques
Teste les fonctionnalités de statistiques et de reporting:

- Nombre total de clients
- Nombre total de consultations
- Medium le plus populaire
- Statistiques par consultant (employé)
- Statistiques sur les prédictions