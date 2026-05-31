# Regles metiers Predict'IF

Ce document recense les regles metiers extraites du sujet Predict'IF afin de servir de reference de verification pour le code.

## Convention

- `RM-XX` : identifiant unique de regle metier
- `Source` : origine dans le sujet ou les consignes complementaires
- `Portee` : client, employe, consultation, statistiques, integration, etc.

## Tableau des regles metiers

| ID | Portee | Regle metier | Source |
| --- | --- | --- | --- |
| RM-01 | Domaine | Predict'IF est un cabinet de voyance par telephone. | Descriptif general |
| RM-02 | Domaine | Les mediums proposes par l'agence sont de trois types : spirite, cartomancien et astrologue. | Descriptif general |
| RM-03 | Domaine | Les predictions personnalisees repondent a des questions sentimentales, professionnelles ou psychologiques. | Descriptif general |
| RM-04 | Client | Un client peut consulter la liste des mediums avant de demander une consultation. | Scenario Alice |
| RM-05 | Client | Pour s'inscrire, le client renseigne nom, prenom, date de naissance, adresse postale, telephone, adresse electronique et mot de passe. | Scenario Alice |
| RM-06 | Client | Une inscription reussie declenche un mail de confirmation. | Scenario Alice, mail (1) |
| RM-07 | Client | Une inscription echouee declenche un mail d'echec. | Scenario Alice, mail (1) |
| RM-08 | Client | Le mail de confirmation doit etre envoye par `contact@predict.if`. | Scenario Alice, mail (1) |
| RM-09 | Client | Le client se connecte avec son adresse electronique et son mot de passe. | Scenario Alice |
| RM-10 | Client | Une fois connecte, le client peut consulter son profil astral. | Scenario Alice |
| RM-11 | Client | Le profil astral contient au minimum : signe du zodiaque, signe astrologique chinois, couleur porte-bonheur et animal-totem. | Scenario Alice, exemple de profil astral |
| RM-12 | Client | Le client peut demander une consultation en selectionnant un medium. | Scenario Alice |
| RM-13 | Client | Le client doit recevoir une notification indiquant quand et comment contacter le medium. | Scenario Alice, notification (2) |
| RM-14 | Client | L'historique des consultations du client doit etre consultable. | Scenario Alice |
| RM-15 | Employe | Un employe travaille a distance avec un telephone professionnel et un ordinateur. | Scenario Camille |
| RM-16 | Employe | Une notification est envoyee a un employe lorsqu'une consultation lui est affectee. | Scenario Camille, notification (3) |
| RM-17 | Employe | L'employe selectionne pour une consultation doit etre disponible. | Scenario Camille, hypotheses |
| RM-18 | Employe | L'employe selectionne doit avoir le meme genre que le medium choisi par le client, afin d'incarner ce medium. | Scenario Camille |
| RM-19 | Employe | Parmi les employes eligibles, l'application doit repartir equitablement la charge. | Scenario Camille |
| RM-20 | Employe | L'employe se connecte sur le site pour traiter sa consultation affectee. | Scenario Camille |
| RM-21 | Employe | L'employe doit pouvoir consulter le profil astral du client affecte. | Scenario Camille |
| RM-22 | Employe | L'employe doit pouvoir consulter l'historique des consultations du client affecte. | Scenario Camille |
| RM-23 | Employe | L'employe doit pouvoir declarer qu'il est pret avant l'appel du client. | Scenario Camille |
| RM-24 | Employe | Une fois pret, l'employe attend l'appel telephonique du client. | Scenario Camille |
| RM-25 | Employe | En cas de panne d'inspiration, l'employe peut demander de l'aide a l'application. | Scenario Camille |
| RM-26 | Employe | Pour demander de l'aide, l'employe saisit trois notes de 1 a 4 sur l'echelle du bonheur. | Scenario Camille, API AstroNet predictions |
| RM-27 | Employe | L'application retourne trois predictions de type amour, sante et travail. | Scenario Camille |
| RM-28 | Employe | En fin de consultation, l'employe valide la cloture de la consultation. | Scenario Camille |
| RM-29 | Employe | En fin de consultation, l'employe peut enregistrer un commentaire a destination de ses collegues. | Scenario Camille |
| RM-30 | Employe | L'employe peut consulter des graphiques d'activite de l'agence. | Scenario Camille |
| RM-31 | Statistiques | Les statistiques comprennent au minimum le nombre de consultations par medium. | Scenario Camille |
| RM-32 | Statistiques | Les statistiques comprennent au minimum la repartition des clients par employe. | Scenario Camille |
| RM-33 | Statistiques | Les statistiques comprennent au minimum le top 5 des mediums les plus choisis. | Scenario Camille |
| RM-34 | Consultation | Les demandes de consultation sont traitees instantanement. | Hypotheses |
| RM-35 | Consultation | Il n'existe ni reservation ni liste d'attente. | Hypotheses |
| RM-36 | Consultation | Si aucun employe n'est disponible, la demande de consultation est rejetee. | Hypotheses |
| RM-37 | Consultation | Une consultation en cours mobilise un employe qui ne doit pas etre affecte a une autre consultation simultanement. | Scenario Camille, hypotheses |
| RM-38 | Consultation | Une consultation doit etre associee a un client, un employe et un medium. | Diagramme/metier implicite |
| RM-39 | Consultation | Une consultation possede un etat de fin permettant de distinguer consultation en cours et terminee. | Scenario Camille |
| RM-40 | Consultation | Une consultation doit conserver la date et l'heure de la demande pour les notifications et le suivi. | Notification client (2) |
| RM-41 | Medium | Chaque medium possede une denomination, un genre et une presentation. | Exemples de mediums |
| RM-42 | Medium | Un medium spirite possede en plus un support. | Exemples de mediums |
| RM-43 | Medium | Un medium astrologue possede en plus une formation et une promotion. | Exemples de mediums |
| RM-44 | Medium | Un medium cartomancien n'a pas d'attribut specialise supplementaire dans le sujet. | Diagramme / exemples |
| RM-45 | Profil astral | Le profil astral d'un client est calcule par le service web externe IfAstroNet. | Hypotheses, consignes API |
| RM-46 | Predictions | Les predictions d'aide aux employes sont fournies par le service web externe IfAstroNet. | Hypotheses, consignes API |
| RM-47 | Predictions | L'API IfAstroNet de predictions attend la couleur porte-bonheur et l'animal-totem du profil astral de la personne. | Consignes Moodle API Web externes |
| RM-48 | Predictions | Les niveaux amour, sante et travail envoyes a IfAstroNet sont des valeurs entieres comprises entre 1 et 4. | Scenario Camille, consignes Moodle API Web externes |
| RM-49 | Authentification | Les mots de passe sont stockes sans chiffrement. | Hypotheses |
| RM-50 | Identite | Les adresses mails des clients et des employes sont supposees correctes et uniques. | Hypotheses |
| RM-51 | Notifications | Les mails et notifications sont simules par affichage console. | Hypotheses |
| RM-52 | Initialisation | Les employes sont crees en dur par un service d'initialisation. | Hypotheses |
| RM-53 | Initialisation | Les mediums sont crees en dur par un service d'initialisation. | Hypotheses |
| RM-54 | Geocodage | Une API externe de geocodage d'adresse est fournie par le professeur et peut etre utilisee pour obtenir les coordonnees geographiques d'une adresse postale. | Consignes Moodle API Web externes |
| RM-55 | Geocodage | L'API de geocodage renvoie les coordonnees dans l'ordre longitude puis latitude. | Consignes Moodle API Web externes |
| RM-56 | Architecture | Les services doivent etre developpes en respectant l'architecture en couches vue en cours. | Travail a fournir |
| RM-57 | Presentation | Deux IHM web doivent etre prevues : une pour les clients et une pour les employes. | Travail a fournir |
| RM-58 | Tests | Une IHM console peut etre utilisee pour tester les services metiers. | Travail a fournir |

## Regles metiers derivees utiles pour la verification

Ces regles ne sont pas formulees mot pour mot dans le sujet, mais elles en decoulent directement et servent de garde-fous lors de l'implementation.

| ID | Portee | Regle metier derivee | Justification |
| --- | --- | --- | --- |
| RMD-01 | Inscription | Une inscription ne doit pas reussir si une information obligatoire du formulaire est absente. | Derive de RM-05 |
| RMD-02 | Inscription | Une inscription ne doit pas reussir si l'adresse mail est deja utilisee par un client. | Derive de RM-50 |
| RMD-03 | Consultation | Une consultation ne peut etre creee que si le client et le medium existent. | Derive de RM-12 et RM-38 |
| RMD-04 | Consultation | Un employe indisponible ne peut pas etre affecte a une nouvelle consultation. | Derive de RM-17 et RM-37 |
| RMD-05 | Consultation | Une consultation deja terminee ne doit pas pouvoir etre terminee une seconde fois. | Derive de RM-28 et RM-39 |
| RMD-06 | Consultation | Une notification client ne peut etre envoyee que pour une consultation affectee existante. | Derive de RM-13 et RM-38 |
| RMD-07 | Predictions | Une demande de prediction hors bornes 1..4 doit etre rejetee ou neutralisee. | Derive de RM-26 et RM-48 |
| RMD-08 | Historique | Les consultations terminees et en cours doivent pouvoir alimenter l'historique du client selon le besoin de l'IHM. | Derive de RM-14 |

## Points de verification ulterieurs

- Verifier chaque regle metier contre la couche service.
- Identifier pour chaque regle si elle est : `respectee`, `partiellement respectee`, `non implementee`.
- Distinguer les regles purement metier des contraintes de presentation web.

## Etat de conformite actuel

### Regles metiers explicites

| ID | Etat | Observation |
| --- | --- | --- |
| RM-01 | Hors code | Regle de contexte metier, non verifiable directement par le code. |
| RM-02 | Respectee | Types de mediums modelises et servis. |
| RM-03 | Respectee | Les predictions metier sont orientees amour, sante, travail via AstroNet. |
| RM-04 | Respectee | La liste des mediums est exposee par la couche service. |
| RM-05 | Respectee | Les champs obligatoires d'inscription sont verifies. |
| RM-06 | Respectee | Un mail est envoye en cas de succes. |
| RM-07 | Respectee | Un mail est envoye en cas d'echec. |
| RM-08 | Respectee | L'expediteur est `contact@predict.if`. |
| RM-09 | Respectee | Authentification client par mail et mot de passe. |
| RM-10 | Respectee | Le profil astral peut etre consulte apres authentification. |
| RM-11 | Respectee | Le profil astral expose les quatre informations attendues. |
| RM-12 | Respectee | Une demande de consultation peut etre declenchee pour un medium choisi. |
| RM-13 | Respectee | Une notification client est emise avec la prise en charge. |
| RM-14 | Respectee | L'historique des consultations est consultable. |
| RM-15 | Hors code | Regle descriptive sur les conditions de travail, non portee par le domaine. |
| RM-16 | Respectee | Une notification employe est emise lors de l'affectation. |
| RM-17 | Respectee | Seuls les employes disponibles sont eligibles. |
| RM-18 | Respectee | L'affectation filtre sur le genre du medium a incarner. |
| RM-19 | Respectee | L'equilibrage se fait sur le volume de consultations des employes eligibles. |
| RM-20 | Partiellement respectee | Le service d'authentification employe existe, mais l'IHM web employee n'est pas encore presente. |
| RM-21 | Respectee | Le profil astral du client affecte est accessible via les services existants. |
| RM-22 | Respectee | L'historique du client affecte est accessible via les services existants. |
| RM-23 | Respectee | La couche service permet de declarer qu'un employe est pret. |
| RM-24 | Partiellement respectee | Le flux metier existe, mais l'etat `pret` n'est pas persiste distinctement. |
| RM-25 | Respectee | L'application expose un service de demande d'inspiration. |
| RM-26 | Respectee | Les notes sont bornees de 1 a 4. |
| RM-27 | Respectee | Trois predictions amour, sante et travail sont retournees. |
| RM-28 | Respectee | Une consultation peut etre cloturee par l'employe. |
| RM-29 | Respectee | Un commentaire de fin de consultation est enregistre. |
| RM-30 | Partiellement respectee | Les donnees statistiques existent, mais les graphiques web ne sont pas encore realises. |
| RM-31 | Respectee | Le nombre de consultations par medium est calcule. |
| RM-32 | Respectee | La repartition des clients par employe est calculee. |
| RM-33 | Respectee | Le top des mediums les plus choisis est calcule, parametrique et compatible avec un top 5. |
| RM-34 | Respectee | Les demandes sont traitees immediatement en service. |
| RM-35 | Respectee | Aucune logique de reservation ou de file d'attente n'existe. |
| RM-36 | Respectee | La demande est rejetee si aucun employe eligible n'est disponible. |
| RM-37 | Respectee | Un employe affecte devient indisponible jusqu'a la fin de la consultation. |
| RM-38 | Respectee | Une consultation reference toujours client, employe et medium. |
| RM-39 | Respectee | L'etat de fin est modele par `estTermine`. |
| RM-40 | Respectee | La consultation conserve date et heure de la demande. |
| RM-41 | Respectee | Les attributs communs des mediums sont modelises. |
| RM-42 | Respectee | Le support du spirite est modele. |
| RM-43 | Respectee | Formation et promotion de l'astrologue sont modelisees. |
| RM-44 | Respectee | Le cartomancien n'ajoute pas d'attribut specialise. |
| RM-45 | Respectee | Le profil astral est calcule via IfAstroNet. |
| RM-46 | Respectee | Les predictions d'aide sont fournies via IfAstroNet. |
| RM-47 | Respectee | Les predictions utilisent desormais couleur porte-bonheur et animal-totem. |
| RM-48 | Respectee | Les niveaux envoye a l'API sont verifies sur l'intervalle 1..4. |
| RM-49 | Respectee | Les mots de passe restent stockes en clair, conformement a l'hypothese. |
| RM-50 | Respectee | L'unicite du mail est verifiee au niveau service client et contrainte au niveau persistance utilisateur. |
| RM-51 | Respectee | Les mails et notifications sont simules par affichage console. |
| RM-52 | Respectee | Les employes sont crees en dur par un service d'initialisation. |
| RM-53 | Respectee | Les mediums sont crees en dur par un service d'initialisation. |
| RM-54 | Partiellement respectee | Le client web de geocodage existe, mais il n'est pas encore branche a un service metier. |
| RM-55 | Partiellement respectee | Le sujet sur l'ordre longitude/latitude est documente cote client web, mais pas encore consomme par le domaine. |
| RM-56 | Respectee | Le projet reste structure en couches modele/dao/service/webclient/util. |
| RM-57 | Non implementee | Les deux IHM web client et employe ne sont pas encore developpees. |
| RM-58 | Respectee | Une IHM console et des tests console/JUnit existent pour les services. |

### Regles derivees

| ID | Etat | Observation |
| --- | --- | --- |
| RMD-01 | Respectee | Les informations obligatoires d'inscription sont validees. |
| RMD-02 | Respectee | Une adresse mail client deja utilisee bloque l'inscription. |
| RMD-03 | Respectee | Une consultation ne se cree que si client et medium sont resolus. |
| RMD-04 | Respectee | Un employe indisponible n'est pas eligible a l'affectation. |
| RMD-05 | Respectee | Une consultation deja terminee ne peut plus etre terminee une seconde fois. |
| RMD-06 | Respectee | La notification client ne part que sur une consultation existante. |
| RMD-07 | Respectee | Les demandes de prediction hors bornes sont neutralisees. |
| RMD-08 | Respectee | L'historique recupere l'ensemble des consultations du client. |
