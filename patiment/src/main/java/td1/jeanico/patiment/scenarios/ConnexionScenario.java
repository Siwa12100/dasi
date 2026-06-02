package td1.jeanico.patiment.scenarios;

import java.time.LocalDate;
import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Utilisateur;
import td1.jeanico.patiment.metier.services.AuthService;
import td1.jeanico.patiment.metier.services.ClientService;

/**
 * Scénarios de tests pour l'authentification des utilisateurs
 * @author ncolomb
 */
public class ConnexionScenario {

    private static AuthService authService;
    private static ClientService clientService;

    public static void lancer() {
        System.out.println("\n========== SCÉNARIOS DE CONNEXION ==========");

        authService = new AuthService();
        clientService = new ClientService();

        // Préparer les données : créer un client valide pour les tests
        preparerDonneesTest();

        scenarioConnexionClientValide();
        scenarioConnexionMotDePasseIncorrect();
        scenarioConnexionMailInconnu();
        scenarioConnexionMotDePasseVide();
        scenarioConnexionMailVide();
        scenarioConnexionMailEtMotDePasseVides();
        scenarioConnexionEmployeValide();

        System.out.println("========== FIN SCÉNARIOS DE CONNEXION ==========\n");
    }

    /**
     * Prépare les données de test en créant un client d'essai
     */
    private static void preparerDonneesTest() {
        System.out.println("\n=> Préparation du client de test...");
        
        Adresse adresse = new Adresse("20", "Rue de la Paix", "75002", "75", "Paris");
        Client clientTest = new Client(
            "Doe",
            "Alice",
            "alice.doe@email.com",
            "motdepasse123",
            "0600000000",
            Genre.FEMME,
            adresse,
            LocalDate.of(1998, 6, 18)
        );

        ClientDao clientDao = new ClientDao();
        try {
            clientDao.creer(clientTest);
            System.out.println("INFO: Client de test créé avec succès");
        } catch (Exception e) {
            System.out.println("ERROR: Le client de test n'a pas pu être créé (peut-être déjà présent)");
        }

        System.out.println("\n=> Préparation de l'employé de test...");

        Employe employeTest = new Employe(
            "anna@predictif.fr",
            "Smith",
            "Anna",
            "anna123",
            "0600000001",
            Genre.FEMME,
            true
        );

        EmployeDao employeDao = new EmployeDao();
        try {
            employeDao.creer(employeTest);
            System.out.println("INFO: Employé de test créé avec succès");
        } catch (Exception e) {
            System.out.println("ERROR: L'employé de test n'a pas pu être créé (peut-être déjà présent)");
        }
    }

    /**
     * Test : connexion avec des identifiants valides
     */
    public static void scenarioConnexionClientValide() {
        System.out.println("\n=> Scénario : Connexion avec identifiants valides");

        Utilisateur utilisateur = authService.auth²entifier("alice.doe@email.com", "motdepasse123");
        
        if (utilisateur == null) {
            System.out.println("   ❌ ECHEC : L'authentification devrait réussir");
            return;
        }

        if (utilisateur instanceof Client) {
            Client client = (Client) utilisateur;
            System.out.println("   ✅ SUCCES : Authentification réussie  => " + utilisateur.toString());
        } else {
            System.out.println("   ❌ ECHEC : L'utilisateur authentifié n'est pas un client");
        }
    }

    /**
     * Test : connexion avec mot de passe incorrect
     */
    public static void scenarioConnexionMotDePasseIncorrect() {
        System.out.println("\n=> Scénario : Connexion avec mot de passe incorrect");

        Utilisateur utilisateur = authService.authentifier("alice.doe@email.com", "mauvaisMotDePasse");
        
        if (utilisateur == null) {
            System.out.println("   ✅ SUCCES : L'authentification a correctement échoué");
        } else {
            System.out.println("   ❌ ECHEC : L'authentification devrait échouer avec un mauvais mot de passe");
        }
    }

    /**
     * Test : connexion avec un email qui n'existe pas
     */
    public static void scenarioConnexionMailInconnu() {
        System.out.println("\n=> Scénario : Connexion avec email inconnu");

        Utilisateur utilisateur = authService.authentifier("emailinconnu@email.com", "motdepasse123");
        
        if (utilisateur == null) {
            System.out.println("   ✅ SUCCES : L'authentification a correctement échoué");
        } else {
            System.out.println("   ❌ ECHEC : L'authentification devrait échouer avec un email inconnu");
        }
    }

    /**
     * Test : connexion avec mot de passe vide
     */
    public static void scenarioConnexionMotDePasseVide() {
        System.out.println("\n=> Scénario : Connexion avec mot de passe vide");

        Utilisateur utilisateur = authService.authentifier("alice.doe@email.com", "");
        
        if (utilisateur == null) {
            System.out.println("   ✅ SUCCES : L'authentification a correctement échoué");
        } else {
            System.out.println("   ❌ ECHEC : L'authentification devrait échouer avec un mot de passe vide");
        }
    }

    /**
     * Test : connexion avec email vide
     */
    public static void scenarioConnexionMailVide() {
        System.out.println("\n=> Scénario : Connexion avec email vide");

        Utilisateur utilisateur = authService.authentifier("", "motdepasse123");
        
        if (utilisateur == null) {
            System.out.println("   ✅ SUCCES : L'authentification a correctement échoué");
        } else {
            System.out.println("   ❌ ECHEC : L'authentification devrait échouer avec un email vide");
        }
    }

    /**
     * Test : connexion avec email et mot de passe vides
     */
    public static void scenarioConnexionMailEtMotDePasseVides() {
        System.out.println("\n=> Scénario : Connexion avec email et mot de passe vides");

        Utilisateur utilisateur = authService.authentifier("", "");
        
        if (utilisateur == null) {
            System.out.println("   ✅ SUCCES : L'authentification a correctement échoué");
        } else {
            System.out.println("   ❌ ECHEC : L'authentification devrait échouer avec des identifiants vides");
        }
    }

    /**
     * Test : connexion d'un employé avec des identifiants valides
     */
    public static void scenarioConnexionEmployeValide() {
        System.out.println("\n=> Scénario : Connexion avec identifiants valides (Employé)");

        Utilisateur utilisateur = authService.authentifier("anna@predictif.fr", "anna123");
        
        if (utilisateur == null) {
            System.out.println("   ❌ ECHEC : L'authentification de l'employé devrait réussir");
            return;
        }

        if (utilisateur instanceof Employe) {
            Employe employe = (Employe) utilisateur;
            System.out.println("   ✅ SUCCES : Authentification réussie => Employé: " + employe.toString());
        } else {
            System.out.println("   ❌ ECHEC : L'utilisateur authentifié n'est pas un employé");
        }
    }
}
