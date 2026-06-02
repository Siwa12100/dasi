package td1.jeanico.patiment.tests;

import java.time.LocalDate;

import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.metier.services.ClientService;

public class ClientServiceTest {

    private static final String MAIL_CLIENT_INITIALISE = "arthur@free.fr";
    private static final String MDP_CLIENT_INITIALISE = "arthur123";

    private static int nbTests = 0;
    private static int nbSucces = 0;
    private static int sequenceMail = 1;

    private static ClientService clientService;

    private ClientServiceTest() {
    }

    public static void lancerTestsClientService() {
        nbTests = 0;
        nbSucces = 0;
        sequenceMail = 1;
        clientService = new ClientService();

        System.out.println("\n=== Lancement des tests console de ClientService ===");

        test_Inscrire_Client_Valide();
        test_Inscrire_Client_DoublonMail();
        test_Authentifier_Client_Valide();
        test_ConsulterProfilAstral_ClientExistant();
        test_RecupererClientParId();

        System.out.println("=== Bilan ClientService: " + nbSucces + "/" + nbTests + " tests valides ===\n");
    }

    public static void test_Inscrire_Client_Valide() {
        System.out.println("Test : Inscrire un client avec des données valides");

        Client client = clientValide(mailUnique("inscription.valide"));
        boolean resultat = clientService.inscrire(client);

        verifier("inscription reussie", resultat);
        verifier("id client renseigne", client.getId() != null);
        verifier("profil astral renseigne", client.getProfilAstral() != null);
    }

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

    public static void test_Authentifier_Client_Valide() {
        System.out.println("Test : Authentifier un client valide initialise en BDD");

        Client resultat = clientService.authentifier(MAIL_CLIENT_INITIALISE, MDP_CLIENT_INITIALISE);

        verifier("authentification retourne client", resultat != null);
        verifier("mail client authentifie correct", resultat != null && MAIL_CLIENT_INITIALISE.equalsIgnoreCase(resultat.getMail()));
    }

    public static void test_ConsulterProfilAstral_ClientExistant() {
        System.out.println("Test : Consulter le profil astral d'un client initialise en BDD");

        Client client = clientService.authentifier(MAIL_CLIENT_INITIALISE, MDP_CLIENT_INITIALISE);
        verifier("prerequis : client initialise retrouve", client != null);

        if (client != null) {
            ProfilAstral resultat = clientService.consulterProfilAstral(client);

            verifier("profil retourne", resultat != null);
            verifier("animal totem renseigne", resultat != null && !estVide(resultat.getAnimalTotal()));
            verifier("signe zodiac renseigne", resultat != null && !estVide(resultat.getSigneZodiac()));
        }
    }

    public static void test_RecupererClientParId() {
        System.out.println("Test : Recuperer un client initialise en BDD par son id");

        Client client = clientService.authentifier(MAIL_CLIENT_INITIALISE, MDP_CLIENT_INITIALISE);
        verifier("prerequis : client initialise retrouve", client != null);

        if (client != null) {
            Client recupere = clientService.recupererClientParId(client.getId());

            verifier("client recupere non nul", recupere != null);
            verifier("id recupere correct", recupere != null && client.getId().equals(recupere.getId()));
        }
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

    private static Adresse adresseValide() {
        return new Adresse("20", "Rue de la Paix", "75002", "75", "Paris");
    }

    private static String mailUnique(String prefix) {
        String mail = prefix + "." + System.currentTimeMillis() + "." + sequenceMail + "@test.fr";
        sequenceMail++;
        return mail;
    }

    private static boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }

}
