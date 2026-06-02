package td1.jeanico.patiment.tests;

import td1.jeanico.patiment.metier.modeles.utilisateurs.Utilisateur;
import td1.jeanico.patiment.metier.services.AuthService;

public class AuthServiceTest {

    private static final String MAIL_CLIENT_INITIALISE = "arthur@free.fr";
    private static final String MDP_CLIENT_INITIALISE = "arthur123";

    private static int nbTests = 0;
    private static int nbSucces = 0;

    private static AuthService authService;

    /**
     * Constructeur prive: classe utilitaire de tests statiques.
     */
    private AuthServiceTest() {
    }

    /**
     * Point d'entree de la suite de tests console AuthService.
     */
    public static void lancerTestsAuthService() {
        nbTests = 0;
        nbSucces = 0;
        authService = new AuthService();

        System.out.println("\n=== Lancement des tests console de AuthService ===");

        test_Authentifier_MailVide();
        test_Authentifier_MotDePasseVide();
        test_Authentifier_MailEtMotDePasseVides();
        test_Authentifier_IdentifiantsInconnus();
        test_Authentifier_Valide();

        System.out.println("=== Bilan AuthService: " + nbSucces + "/" + nbTests + " tests valides ===\n");
    }

    /**
     * Verifie que l'authentification est refusee quand le mail est vide.
     */
    public static void test_Authentifier_MailVide() {
        System.out.println("Test : Refuser l'authentification si le mail est vide");

        Utilisateur resultat = authService.authentifier("", "secret123");

        verifier("authentification refusee", resultat == null);
    }

    /**
     * Verifie que l'authentification est refusee quand le mot de passe est vide.
     */
    public static void test_Authentifier_MotDePasseVide() {
        System.out.println("Test : Refuser l'authentification si le mot de passe est vide");

        Utilisateur resultat = authService.authentifier("test@test.fr", "");

        verifier("authentification refusee", resultat == null);
    }

    /**
     * Verifie que l'authentification est refusee quand mail et mot de passe sont vides.
     */
    public static void test_Authentifier_MailEtMotDePasseVides() {
        System.out.println("Test : Refuser l'authentification si mail et mot de passe sont vides");

        Utilisateur resultat = authService.authentifier("", "");

        verifier("authentification refusee", resultat == null);
    }

    /**
     * Verifie que des identifiants inconnus retournent null.
     */
    public static void test_Authentifier_IdentifiantsInconnus() {
        System.out.println("Test : Retourner null si les identifiants sont inconnus");

        Utilisateur resultat = authService.authentifier("inconnu@test.fr", "mauvaismdp");

        verifier("retourne null pour identifiants inconnus", resultat == null);
    }

    /**
     * Verifie le parcours nominal d'authentification avec un compte initialise en BDD.
     */
    public static void test_Authentifier_Valide() {
        System.out.println("Test : Authentifier un utilisateur initialise en BDD");

        Utilisateur resultat = authService.authentifier(MAIL_CLIENT_INITIALISE, MDP_CLIENT_INITIALISE);

        verifier("authentification retourne un utilisateur", resultat != null);
        verifier("mail utilisateur authentifie correct", resultat != null && MAIL_CLIENT_INITIALISE.equalsIgnoreCase(resultat.getMail()));
    }

    /**
     * Assertion console minimale: incremente les compteurs et affiche le resultat.
     */
    private static void verifier(String message, boolean condition) {
        nbTests++;
        if (condition) {
            nbSucces++;
            System.out.println("  🟢 [OK] " + message);
        } else {
            System.out.println("  🔴 [KO] " + message);
        }
    }
}
