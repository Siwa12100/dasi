# dasi
aaaaaaaaaaaaaaaaah

## Lancer le projet avec lancement.sh

Depuis la racine du repo:

```bash
cd patiment
chmod +x lancement.sh
./lancement.sh
```

Le script compile le projet (`clean compile`) puis démarre l'application avec Maven (`exec:java`).

## Directement avec Maven 

Test de connexion BDD
cd /workspaces/patiment/patiment
./mvnw -Dpatiment.runDbTests=true -Dtest=TestConnexionBdd test

Tous les tests avec BDD
./mvnw -Dpatiment.runDbTests=true test

Vérifier l’appli main
./mvnw -DskipTests exec:java

## comptes fakes pour cli

Client:
arthur@free.fr / arthur123

Employé:
anna@predictif.fr / anna123

Le compte client arthur@free.fr est désormais créé explicitement par l'initialisation console.

