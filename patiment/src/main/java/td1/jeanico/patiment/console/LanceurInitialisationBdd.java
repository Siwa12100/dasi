package td1.jeanico.patiment.console;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.ConsultationDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.metier.modeles.consultations.Consultation;
import td1.jeanico.patiment.metier.modeles.mediums.Astrologue;
import td1.jeanico.patiment.metier.modeles.mediums.Cartomancien;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.mediums.Spirite;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.outils.SupportPersistance;

/**
 * Initialisation de la base de données avec des données de test complètes
 * @author ncolomb
 */
public class LanceurInitialisationBdd extends SupportPersistance {
    private static final long SEED = 42; // Seed pour la génération de données aléatoires

    private static final String[] NOMS = {
        "DUPONT", "MARTIN", "BERNARD", "THOMAS", "ROBERT", "RICHARD", "PETIT", "DURAND", "LEFEVRE", "MOREAU",
        "SIMON", "LAURENT", "LEFEBVRE", "MICHEL", "GARCIA", "DAVID", "BERTRAND", "ROUX", "VINCENT", "FOURNIER",
        "MOREL", "GIRARDIN", "ANDRE", "LEROY", "GUERIN", "NOEL", "CARON", "LECLERC", "RENAUD", "GAILLARD"
    };
    
    private static final String[] PRENOMS = {
        "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry", "Iris", "Jack",
        "Kevin", "Laura", "Michel", "Nathalie", "olivier", "Patricia", "Quentin", "Rachel", "Samuel", "Thomas",
        "Ursula", "Vincent", "William", "Xavier", "Yves", "Zoé", "Adrian", "Beatrice", "Cedric", "Dominique"
    };
    
    private static final String[] VILLES = {
        "Paris", "Lyon", "Marseille", "Toulouse", "Nice", "Nantes", "Strasbourg", "Montpellier", "Bordeaux", "Lille",
        "Rennes", "Reims", "Le Havre", "Saint-Étienne", "Toulon", "Grenoble", "Angers", "Dijon", "Nîmes", "Aix-en-Provence",
        "Brest", "Le Mans", "Amiens", "Tours", "Limoges", "Clermont-Ferrand", "Villeurbanne", "Metz", "Besançon", "Orléans"
    };
    
    private static final String[] DOMAINES_EMAIL = {
        "gmail.com", "yahoo.fr", "hotmail.com", "orange.fr", "sfr.fr", "free.fr", "wanadoo.fr", "laposte.net", "live.fr"
    };

    private static final String[] RUES_TYPES = {
        "Rue", "Avenue", "Boulevard", "Allée", "Chemin", "Route", "Place", "Quai", "Cours"
    };
    
    private static final String[] RUES_SUFFIXES = {
        "de la Paix", "des Fleurs", "Principale", "Nationale", "de Paris", "de la Gare", "du Commerce", "de la Mairie", "de la République", "Résistance"
    };

    private static final String[] ANIMAL_TOTEM = {
        "Bélier", "Taureau", "Gémeaux", "Cancer", "Lion", "Vierge", "Balance", "Scorpion", "Sagittaire", "Capricorne", "Verseau", "Poissons"
    };

    private static final String[] SIGNE_ZODIAC = {
        "Feu", "Terre", "Air", "Eau"
    };

    private static final String[] COULEUR_BONHEUR = {
        "Rouge", "Vert", "Bleu", "Jaune", "Violet", "Orange", "Rose", "Blanc", "Noir", "Gris"
    };

    private static final String[] SIGNE_CHINOIS = {
        "Rat", "Bœuf", "Tigre", "Lapin", "Dragon", "Serpent", "Cheval", "Chèvre", "Singe", "Coq", "Chien", "Cochon"
    };

    public static void main(String[] args) {
        LanceurInitialisationBdd lanceur = new LanceurInitialisationBdd();
        lanceur.lancementInitialisationBdd();
    }

    public void lancementInitialisationBdd() {
        System.out.println("INITIALISATION DE LA BASE DE DONNÉES");
        
        MediumDao mediumDao = new MediumDao();
        EmployeDao employeDao = new EmployeDao();
        ClientDao clientDao = new ClientDao();
        ConsultationDao consultationDao = new ConsultationDao();
        
        executerEnTransaction(() -> {
            // Initialiser les mediums
            if (mediumDao.listerParDenomination().isEmpty()) {
                System.out.println("Création de 20 mediums...");
                creerMediums(mediumDao);
            } else {
                System.out.println("INFO: Les mediums existent déjà");
            }

            // Initialiser les employés
            if (employeDao.listerParNomPrenom().isEmpty()) {
                System.out.println("Création de 15 employés...");
                creerEmployes(employeDao);
            } else {
                System.out.println("INFO: Les employés existent déjà");
            }
            
            // Initialiser les clients
            if (clientDao.listerParNomPrenom().isEmpty()) {
                System.out.println("Création de 5000 clients...");
                creerClients(clientDao);
            } else {
                System.out.println("INFO: Les clients existent déjà");
            }
            
            // Initialiser les consultations
            if (consultationDao.listerParDateDesc().isEmpty()) {
                System.out.println("Création de 100 consultations...");
                creerConsultations(consultationDao, clientDao, mediumDao, employeDao);
            } else {
                System.out.println("INFO: Les consultations existent déjà");
            }
            
            System.out.println("INITIALISATION TERMINÉE AVEC SUCCÈS");
            
            return null;
        });
    }

    /**
     * Crée 20 mediums (astrologies + cartomanciens + spirites)
     */
    private void creerMediums(MediumDao mediumDao) {
        mediumDao.creer(new Spirite("Mme Irma", Genre.FEMME, "Medium de tradition spirite classique.", "Boule de cristal"));
        mediumDao.creer(new Spirite("Professeur Ombre", Genre.HOMME, "Specialiste des messages de l'au-dela.", "Pendule"));
        mediumDao.creer(new Spirite("Madame Lumière", Genre.FEMME, "Médium avec 30 ans d'expérience.", "Cartes anciennes"));
        mediumDao.creer(new Spirite("Monsieur Écho", Genre.HOMME, "Communication avec les esprits bienveillants.", "Miroir sacré"));
        mediumDao.creer(new Spirite("Sister Mystère", Genre.NON_SPECIFIE, "Channeling et guidance spirituelle.", "Cristal guérisseur"));
        mediumDao.creer(new Spirite("Brother Visions", Genre.NON_SPECIFIE, "Interprète des messages de l'univers.", "Bâton de sauge"));

        mediumDao.creer(new Cartomancien("Maitre Soleil", Genre.HOMME, "Expert des tirages a haute precision."));
        mediumDao.creer(new Cartomancien("Mlle Arcane", Genre.FEMME, "Lectrice intuitive des energies du moment."));
        mediumDao.creer(new Cartomancien("Madame Destinée", Genre.NON_SPECIFIE, "Tarot Marseille et Oracle moderne."));
        mediumDao.creer(new Cartomancien("Monsieur Atout", Genre.HOMME, "Spécialiste des tirages amoureux."));
        mediumDao.creer(new Cartomancien("Gypsy Rose", Genre.FEMME, "Cartes divinatoires et interprétation profonde."));
        mediumDao.creer(new Cartomancien("King Karma", Genre.NON_SPECIFIE, "Lecture karmique par les cartes."));
        mediumDao.creer(new Cartomancien("Madame Oracle", Genre.FEMME, "Oracle des anges et guidance spirituelle."));

        mediumDao.creer(new Astrologue("Cassandre Vega", Genre.FEMME, "Astrologue moderne et pedagogique.", "ENS Astro", "2015"));
        mediumDao.creer(new Astrologue("Orion Delphes", Genre.HOMME, "Interpretation approfondie des themes astraux.", "Institut Celeste", "2012"));
        mediumDao.creer(new Astrologue("Stella Cosmos", Genre.NON_SPECIFIE, "Astrologie sidérale et tropicale.", "Academie Stellaire", "2008"));
        mediumDao.creer(new Astrologue("Dr. Jupiter", Genre.HOMME, "Expert en astrologie psychologique.", "Université de Zurich", "2010"));
        mediumDao.creer(new Astrologue("Lune Celeste", Genre.FEMME, "Synastrie et astrologie relationnelle.", "Centre Astrologique", "2018"));
        mediumDao.creer(new Astrologue("Phoenix Solar", Genre.NON_SPECIFIE, "Révolution solaire et progressions.", "Institut Hermès", "2014"));
        mediumDao.creer(new Astrologue("Venus Étoile", Genre.FEMME, "Astrologie amoureuse et karmique.", "Ecole des Astres", "2016"));
    }

    /**
     * Crée 15 employés
     */
    private void creerEmployes(EmployeDao employeDao) {
        employeDao.creer(new Employe("anna@predictif.fr", "Anna", "CONDA", "anna123", "0600000001", Genre.FEMME, true));
        employeDao.creer(new Employe("bruno@predictif.fr", "Bruno", "LEMAIRE", "bruno123", "0600000002", Genre.HOMME, true));
        employeDao.creer(new Employe("claire@predictif.fr", "Claire", "OBSCURE", "claire123", "0600000003", Genre.NON_SPECIFIE, true));
        employeDao.creer(new Employe("david@predictif.fr", "David", "BLANC", "david123", "0600000004", Genre.FEMME, false));
        employeDao.creer(new Employe("elise@predictif.fr", "Elise", "ROUGE", "elise123", "0600000005", Genre.HOMME, false));
        employeDao.creer(new Employe("fabien@predictif.fr", "Fabien", "NOIR", "fabien123", "0600000006", Genre.NON_SPECIFIE, false));
        employeDao.creer(new Employe("geraldine@predictif.fr", "Géraldine", "VERT", "geraldine123", "0600000007", Genre.FEMME, false));
        employeDao.creer(new Employe("henri@predictif.fr", "Henri", "BLEU", "henri123", "0600000008", Genre.HOMME, false));
        employeDao.creer(new Employe("isabelle@predictif.fr", "Isabelle", "JAUNE", "isabelle123", "0600000009", Genre.NON_SPECIFIE, false));
        employeDao.creer(new Employe("julien@predictif.fr", "Julien", "ROSE", "julien123", "0600000010", Genre.FEMME, false));
        employeDao.creer(new Employe("karine@predictif.fr", "Karine", "GRIS", "karine123", "0600000011", Genre.HOMME, false));
        employeDao.creer(new Employe("laurent@predictif.fr", "Laurent", "BRUN", "laurent123", "0600000012", Genre.NON_SPECIFIE, false));
        employeDao.creer(new Employe("marthe@predictif.fr", "Marthe", "VIOLET", "marthe123", "0600000013", Genre.FEMME, false));
        employeDao.creer(new Employe("nicolas@predictif.fr", "Nicolas", "ORANGE", "nicolas123", "0600000014", Genre.HOMME, false));
        employeDao.creer(new Employe("odette@predictif.fr", "Odette", "ARGENT", "odette123", "0600000015", Genre.NON_SPECIFIE, false));
    }

    /**
     * Crée 5000 clients
     */
    private void creerClients(ClientDao clientDao) {
        clientDao.creer(new Client("Doe", "Alice", "alice.doe@email.com", "secret123", "0612345678", Genre.FEMME, new Adresse("20", "Rue de la Paix", "75002", "75", "Paris"), LocalDate.of(1998, 6, 18)));
        
        Random random = new Random(SEED);
        int totalClients = 4999;
        
        for (int i = 0; i < totalClients; i++) {
            String nom = NOMS[random.nextInt(NOMS.length)];
            String prenom = PRENOMS[random.nextInt(PRENOMS.length)];
            
            // Générer un email unique
            String email = prenom.toLowerCase() + "." + nom.toLowerCase() + i + "@" + DOMAINES_EMAIL[random.nextInt(DOMAINES_EMAIL.length)];
            String motDePasse = "pass" + i;
            String telephone = "06" + String.format("%08d", random.nextInt(100000000));
            
            // Générer une adresse aléatoire
            int numero = random.nextInt(1, 300);
            String adresse1 = numero + " " + RUES_TYPES[random.nextInt(RUES_TYPES.length)] + " " + RUES_SUFFIXES[random.nextInt(RUES_SUFFIXES.length)];
            String ville = VILLES[random.nextInt(VILLES.length)];
            String codePostal = String.format("%05d", random.nextInt(100000));
            String departement = String.format("%02d", (random.nextInt(95) + 1));
            
            Adresse adresse = new Adresse(String.valueOf(numero), adresse1, codePostal, departement, ville);
            
            // Générer une date de naissance aléatoire
            int annee = 1960 + random.nextInt(50);
            int mois = random.nextInt(12) + 1;
            int jour = random.nextInt(28) + 1; // Limiter à 28 (pour éviter les problèmes de jours)
            LocalDate dateNaissance = LocalDate.of(annee, mois, jour);
            
            Genre genre = Genre.values()[random.nextInt(Genre.values().length)];

            // Générer un profil astral aléatoire (sans passer par l'API AstroNet) -- pas cohérent avec la date de naissance !!
            ProfilAstral profilAstral = new ProfilAstral(
                ANIMAL_TOTEM[random.nextInt(ANIMAL_TOTEM.length)],
                SIGNE_ZODIAC[random.nextInt(SIGNE_ZODIAC.length)],
                COULEUR_BONHEUR[random.nextInt(COULEUR_BONHEUR.length)],
                SIGNE_CHINOIS[random.nextInt(SIGNE_CHINOIS.length)]
            );
            
            try {
                Client client = new Client(nom, prenom, email, motDePasse, telephone, genre, adresse, dateNaissance, profilAstral);
                clientDao.creer(client);
                
                if ((i + 1) % 500 == 0) {
                    System.out.println((i + 1) + "/" + totalClients + " clients créés...");
                }
            } catch (Exception e) {
                System.err.println("WARN: Erreur lors de la création du client " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    /**
     * Crée 100 consultations (terminées et non terminées)
     */
    private void creerConsultations(ConsultationDao consultationDao, ClientDao clientDao, MediumDao mediumDao, EmployeDao employeDao) {
        Random random = new Random(SEED);
        
        List<Client> clients = clientDao.listerParNomPrenom();
        List<Medium> mediums = mediumDao.listerParDenomination();
        List<Employe> employes = employeDao.listerParNomPrenom();
        
        if (clients == null || clients.isEmpty() || mediums == null || mediums.isEmpty() || employes == null || employes.isEmpty()) {
            System.out.println("ERROR: Impossible de créer des consultations (données manquantes)");
            return;
        }
        
        // Créer 90 consultations terminées
        for (int i = 0; i < 90; i++) {
            Client client = clients.get(random.nextInt(clients.size()));
            Medium medium = mediums.get(random.nextInt(mediums.size()));
            Employe employe = employes.get(random.nextInt(employes.size()));
            
            // Date random dans les 6 derniers mois
            LocalDateTime dateDemande = LocalDateTime.now().minusDays(random.nextInt(180));

            // Commentaire de consultation
            String commentaire = random.nextBoolean() ? "Je suis le commentaire de la consultation consultation " + (i + 1) : "";

            try {
                Consultation consultation = new Consultation(commentaire, dateDemande, true, client, employe, medium);
                consultationDao.creer(consultation);
            } catch (Exception e) {
                System.err.println("WARN: Erreur création consultation terminée " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        // Créer 10 consultations non terminées
        for (int i = 0; i < 10; i++) {
            Client client = clients.get(random.nextInt(clients.size()));
            Medium medium = mediums.get(random.nextInt(mediums.size()));
            Employe employe = employes.get(random.nextInt(employes.size()));
            
            // Date random dans les 1 à 3 derniers jours
            LocalDateTime dateDemande = LocalDateTime.now().minusDays(random.nextInt(1, 3));
            
            try {
                Consultation consultation = new Consultation("", dateDemande, false, client, employe, medium);
                consultationDao.creer(consultation);
            } catch (Exception e) {
                System.err.println("WARN: Erreur création consultation non terminée " + (i + 1) + ": " + e.getMessage());
            }
        }
    }
}
