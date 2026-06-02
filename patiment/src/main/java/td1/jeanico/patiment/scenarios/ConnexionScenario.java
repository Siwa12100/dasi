package td1.jeanico.patiment.scenarios;

import java.time.LocalDate;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Utilisateur;
import td1.jeanico.patiment.metier.services.AuthService;

/**
 * Scénarios de tests pour l'authentification des utilisateurs
 * @author ncolomb
 */
public class ConnexionScenario {

    private static AuthService authService;

    public static void lancer() {
        System.out.println("\n========== SCÉNARIOS DE CONNEXION ==========");

        authService = new AuthService();

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
     * Test : connexion avec des identifiants valides (issues de LanceurInitialisationBdd.java !!)
     */
    public static void scenarioConnexionClientValide() {
        System.out.println("\n=> Scénario : Connexion avec identifiants valides");

        Utilisateur utilisateur = authService.authentifier("alice.doe@email.com", "secret123");
        
        if (utilisateur == null) {
            System.out.println("   ❌ ECHEC : L'authentification devrait réussir");
            return;
        }

        if (utilisateur instanceof Client) {
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
            System.out.println("   ✅ SUCCES : Authentification réussie => Employé: " + utilisateur.toString());
        } else {
            System.out.println("   ❌ ECHEC : L'utilisateur authentifié n'est pas un employé");
        }
    }
}
