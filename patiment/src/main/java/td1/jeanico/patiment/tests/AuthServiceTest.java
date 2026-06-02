package td1.jeanico.patiment.tests;

import java.time.LocalDate;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.JpaUtil;
import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.modeles.utilisateurs.Utilisateur;
import td1.jeanico.patiment.services.AuthService;

public class AuthServiceTest {

    private static int nbTests = 0;
    private static int nbSucces = 0;
    private static int sequenceMail = 1;

    private static AuthService authService;

    private AuthServiceTest() {
    }

    public static void lancerTestsAuthService() {
        nbTests = 0;
        nbSucces = 0;
        sequenceMail = 1;
        authService = new AuthService();

        System.out.println("\n=== Lancement des tests console de AuthService ===");

        test_Authentifier_MailVide();
        test_Authentifier_MotDePasseVide();
        test_Authentifier_MailEtMotDePasseVides();
        test_Authentifier_IdentifiantsInconnus();
        test_Authentifier_Valide();

        System.out.println("=== Bilan AuthService: " + nbSucces + "/" + nbTests + " tests valides ===\n");
    }

    public static void test_Authentifier_MailVide() {
        System.out.println("Test : Refuser l'authentification si le mail est vide");

        Utilisateur resultat = authService.authentifier("", "secret123");

        verifier("authentification refusee", resultat == null);
    }

    public static void test_Authentifier_MotDePasseVide() {
        System.out.println("Test : Refuser l'authentification si le mot de passe est vide");

        Utilisateur resultat = authService.authentifier("test@test.fr", "");

        verifier("authentification refusee", resultat == null);
    }

    public static void test_Authentifier_MailEtMotDePasseVides() {
        System.out.println("Test : Refuser l'authentification si mail et mot de passe sont vides");

        Utilisateur resultat = authService.authentifier("", "");

        verifier("authentification refusee", resultat == null);
    }

    public static void test_Authentifier_IdentifiantsInconnus() {
        System.out.println("Test : Retourner null si les identifiants sont inconnus");

        Utilisateur resultat = authService.authentifier("inconnu@test.fr", "mauvaismdp");

        verifier("retourne null pour identifiants inconnus", resultat == null);
    }

    public static void test_Authentifier_Valide() {
        System.out.println("Test : Authentifier un utilisateur avec des identifiants valides");

        String mail = mailUnique("auth.service");
        String motDePasse = "secret123";
        boolean utilisateurCree = creerUtilisateurTest(mail, motDePasse);

        Utilisateur resultat = authService.authentifier(mail, motDePasse);

        verifier("creation utilisateur de test reussie", utilisateurCree);
        verifier("authentification retourne un utilisateur", resultat != null);
        verifier("mail utilisateur authentifie correct", resultat != null && mail.equalsIgnoreCase(resultat.getMail()));
    }

    private static boolean creerUtilisateurTest(String mail, String motDePasse) {
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();

            ClientDao clientDao = new ClientDao();
            if (clientDao.trouverParMail(mail) == null) {
                Client client = new Client(
                        "Test",
                        "Auth",
                        mail,
                        motDePasse,
                        "0600000000",
                        Genre.NON_SPECIFIE,
                        new Adresse("1", "Rue de Test", "75001", "75", "Paris"),
                        LocalDate.of(1990, 1, 1)
                );
                clientDao.creer(client);
            }

            JpaUtil.validerTransaction();
            return true;
        } catch (Exception ex) {
            JpaUtil.annulerTransaction();
            return false;
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }

    private static String mailUnique(String prefix) {
        String mail = prefix + "." + System.currentTimeMillis() + "." + sequenceMail + "@test.fr";
        sequenceMail++;
        return mail;
    }

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
