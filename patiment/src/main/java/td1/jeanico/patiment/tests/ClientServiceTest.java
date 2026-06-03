package td1.jeanico.patiment.tests;

import java.time.LocalDate;

import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.metier.services.ClientService;

public class ClientServiceTest {

    private static int nbTests = 0;
    private static int nbSucces = 0;
    private static int sequenceMail = 1;

    private static ClientService clientService;

    /**
     * Constructeur prive: classe utilitaire de tests statiques.
     */
    private ClientServiceTest() {
    }

    /**
     * Point d'entree de la suite de tests console ClientService.
     */
    public static void lancerTestsClientService() {
        nbTests = 0;
        nbSucces = 0;
        sequenceMail = 1;
        clientService = new ClientService();

        System.out.println("\n=== Lancement des tests console de ClientService ===");

        test_Inscrire_Client_Valide();
        test_Inscrire_Client_DoublonMail();

        System.out.println("=== Bilan ClientService: " + nbSucces + "/" + nbTests + " tests valides ===\n");
    }

    /**
     * Verifie qu'une inscription valide est acceptee et enrichie (id + profil astral).
     */
    public static void test_Inscrire_Client_Valide() {
        System.out.println("Test : Inscrire un client avec des données valides");

        Client client = clientValide(mailUnique("inscription.valide"));
        boolean resultat = clientService.inscrire(client);

        verifier("inscription reussie", resultat);
        verifier("id client renseigne", client.getId() != null);
        verifier("profil astral renseigne", client.getProfilAstral() != null);
    }

    /**
     * Verifie qu'une seconde inscription avec le meme mail est refusee.
     */
    public static void test_Inscrire_Client_DoublonMail() {
        System.out.println("Test : Refuser l'inscription si mail deja existant");

        String mail = mailUnique("doublon");
        Client clientInitial = clientValide(mail);
        boolean premiereInscription = clientService.inscrire(clientInitial);

        Client clientDoublon = clientValide(mail);
        boolean secondeInscription = clientService.inscrire(clientDoublon);

        verifier("premiere inscription acceptee", premiereInscription);
        verifier("inscription doublon refusee", !secondeInscription);
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

    /**
     * Construit un client valide pour les scenarios d'inscription.
     */
    private static Client clientValide(String mail) {
        Client client = new Client(
                "Doe",
                "Alice",
                mail,
                "secret",
                "0600000000",
                Genre.FEMME,
                adresseValide(),
                LocalDate.of(1998, 6, 18)
        );
        return client;
    }

    /**
     * Retourne une adresse complete exploitable pour l'inscription.
     */
    private static Adresse adresseValide() {
        return new Adresse("20", "Rue de la Paix", "75002", "75", "Paris");
    }

    /**
     * Genere un mail unique pour eviter les collisions entre executions.
     */
    private static String mailUnique(String prefix) {
        String mail = prefix + "." + System.currentTimeMillis() + "." + sequenceMail + "@test.fr";
        sequenceMail++;
        return mail;
    }
}
