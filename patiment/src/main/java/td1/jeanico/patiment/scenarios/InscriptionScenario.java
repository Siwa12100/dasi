package td1.jeanico.patiment.scenarios;

import java.time.LocalDate;
import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.metier.services.ClientService;

/**
 * Scénarios de tests pour l'inscription de clients
 * @author ncolomb
 */
public class InscriptionScenario {

    private static ClientService clientService;

    public static void lancer() {
        System.out.println("\n========== SCÉNARIOS D'INSCRIPTION ==========");

        clientService = new ClientService();

        scenarioClientValide();
        scenarioClientMailDuplique();
        scenarioClientTelephoneVide();
        scenarioClientAdresseVide();
        scenarioClientAdresseInventee();
        scenarioClientPrenomVide();
        scenarioClientMailVide();
        scenarioClientDateNaissanceFuture();

        System.out.println("========== FIN SCÉNARIOS D'INSCRIPTION ==========\n");
    }

    /**
     * Test d'inscription d'un client avec des données valides
     */
    public static void scenarioClientValide() {
        System.out.println("\n=> Scénario : Inscription d'un client avec des données valides");

        Adresse adresseValide = new Adresse("20", "Rue de la Paix", "75002", "75", "Paris");
        Client client = new Client(
            "Doe",
            "Alice",
            "alice.doe@email.com",
            "secret123",
            "0612345678",
            Genre.FEMME,
            adresseValide,
            LocalDate.of(1998, 6, 18)
        );

        boolean resultat = clientService.inscrire(client);
        if (!resultat) {
            System.out.println("   ❌ ECHEC : La requete d'inscription a échoué alors qu'elle devrait réussir");
            return;
        }
        if (client.getId() == null) {
            System.out.println("   ❌ ECHEC : ID du client non renseigné après inscription");
            return;
        } 
        if (client.getProfilAstral() == null) {
            System.out.println("   ❌ ECHEC : Profil astral du client non renseigné après inscription");
            return;
        }
        if (client.getProfilAstral().getSigneZodiac().isEmpty()) {
            System.out.println("   ❌ ECHEC : Signe du zodiaque du client non renseigné après inscription");
            return;
        }
        if (client.getProfilAstral().getCouleurBonheur().isEmpty()) {
            System.out.println("   ❌ ECHEC : Couleur porte-bonheur du client non renseigné après inscription");
            return;
        }
        if (client.getProfilAstral().getAnimalTotem().isEmpty()) {
            System.out.println("   ❌ ECHEC : Animal totem du client non renseigné après inscription");
            return;
        }
        if (client.getProfilAstral().getSigneChinois().isEmpty()) {
            System.out.println("   ❌ ECHEC : Signe chinois du client non renseigné après inscription");
            return;
        }
        System.out.println("   ✅ SUCCES : Inscription réussie => " + client.toString());
        System.out.println("      Un email de succès a dû être envoyé ! A vérifier dans les logs du dessus !");
    }

    /**
     * Test : tentative d'inscription avec un email déjà utilisé
     */
    public static void scenarioClientMailDuplique() {
        System.out.println("\n=> Scénario : Inscription avec email déjà existant (doublon)");

        Adresse adresse = new Adresse("20", "Rue de la Paix", "75002", "75", "Paris");
        
        // Premier client avec email unique
        Client client1 = new Client(
            "Dupont",
            "Jean",
            "jean.dupont@email.com",
            "password123",
            "0611111111",
            Genre.HOMME,
            adresse,
            LocalDate.of(1990, 3, 15)
        );
        
        boolean resultat1 = clientService.inscrire(client1);
        System.out.println("   ✅ SUCCES : Premier client inscrit : " + resultat1);

        // Deuxième client avec le même email
        Client client2 = new Client(
            "Martin",
            "Marie",
            "jean.dupont@email.com",  // Email identique
            "password456",
            "0622222222",
            Genre.FEMME,
            adresse,
            LocalDate.of(1995, 7, 22)
        );

        boolean resultat2 = clientService.inscrire(client2);
        if (resultat2) {
            System.out.println("   ❌ ECHEC : L'inscription avec email doublon devrait échouer");
        } else {
            System.out.println("   ✅ SUCCES : L'inscription avec email doublon a correctement échoué");
            System.out.println("      Un email d'échec a dû être envoyé ! A vérifier dans les logs du dessus !");
        }
    }

    /**
     * Test : inscription avec données incomplètes
     */
    public static void scenarioClientTelephoneVide() {
        System.out.println("\n=> Scénario : Inscription avec téléphone vide");

        Adresse adresse = new Adresse("20", "Rue de la Paix", "75002", "75", "Paris");
        
        // Client sans téléphone
        Client client = new Client(
            "Bernard",
            "Oui",
            "bernard@email.com",
            "password789",
            "", // Téléphone vide
            Genre.HOMME,
            adresse,
            LocalDate.of(1992, 5, 10)
        );

        boolean resultat = clientService.inscrire(client);
        if (resultat) {
            System.out.println("   ❌ ECHEC : L'inscription avec téléphone vide devrait échouer");
        } else {
            System.out.println("   ✅ SUCCES : L'inscription avec téléphone vide a correctement échoué");
        }
    }

    /**
     * Test : inscription avec adresse vide
     */
    public static void scenarioClientAdresseVide() {
        System.out.println("\n=> Scénario : Inscription avec adresse null");

        Client client = new Client(
            "Lefevre",
            "Sophie",
            "sophie.lefevre@email.com",
            "password000",
            "0644444444",
            Genre.FEMME,
            null,  // Adresse null
            LocalDate.of(1997, 9, 25)
        );

        boolean resultat = clientService.inscrire(client);
        if (resultat) {
            System.out.println("   ❌ ECHEC : L'inscription avec adresse null devrait échouer");
        } else {
            System.out.println("   ✅ SUCCES : L'inscription avec adresse invalide a correctement échoué");
        }
    }

    /**
     * Test : inscription avec adresse inventée
     */
    public static void scenarioClientAdresseInventee() {
        System.out.println("\n=> Scénario : Inscription avec adresse inventée");

        Client client = new Client(
            "Lefevre",
            "Sophie",
            "sophie.lefevre@email.com",
            "password000",
            "0644444444",
            Genre.FEMME,
            new Adresse("999", "Rue Imaginaire", "00000", "00", "NullePart"),  // Adresse inventée
            LocalDate.of(1997, 9, 25)
        );

        boolean resultat = clientService.inscrire(client);
        if (resultat) {
            System.out.println("   ❌ ECHEC : L'inscription avec adresse inventée devrait échouer");
        } else {
            System.out.println("   ✅ SUCCES : L'inscription avec adresse inventée a correctement échoué");
        }
    }

    /**
     * Test : inscription avec prénom vide
     */
    public static void scenarioClientPrenomVide() {
        System.out.println("\n=> Scénario : Inscription avec prénom vide");

        Adresse adresse = new Adresse("10", "Avenue des Champs", "75008", "75", "Paris");
        
        Client client = new Client(
            "Moreau",
            "",  // Prénom vide
            "test@email.com",
            "password111",
            "0655555555",
            Genre.HOMME,
            adresse,
            LocalDate.of(1988, 11, 30)
        );

        boolean resultat = clientService.inscrire(client);
        if (resultat) {
            System.out.println("   ❌ ECHEC : L'inscription avec prénom vide devrait échouer");
        } else {
            System.out.println("   ✅ SUCCES : L'inscription avec prénom vide a correctement échoué");
        }
    }

    /**
     * Test : inscription avec email vide
     */
    public static void scenarioClientMailVide() {
        System.out.println("\n=> Scénario : Inscription avec email vide");

        Adresse adresse = new Adresse("5", "Rue du Commerce", "69000", "69", "Lyon");
        
        Client client = new Client(
            "Girard",
            "Pierre",
            "",  // Email vide
            "password222",
            "0666666666",
            Genre.HOMME,
            adresse,
            LocalDate.of(1985, 2, 14)
        );

        boolean resultat = clientService.inscrire(client);
        if (resultat) {
            System.out.println("   ❌ ECHEC : L'inscription avec email vide devrait échouer");
        } else {
            System.out.println("   ✅ SUCCES : L'inscription avec email vide a correctement échoué");
        }
    }

    /**
     * Test : inscription avec date de naissance future
     */
    public static void scenarioClientDateNaissanceFuture() {
        System.out.println("\n=> Scénario : Inscription avec date de naissance future");

        Adresse adresse = new Adresse("15", "Boulevard Saint-Michel", "75005", "75", "Paris");
        
        Client client = new Client(
            "Dubois",
            "Emma",
            "emma.dubois@email.com",
            "password333",
            "0677777777",
            Genre.FEMME,
            adresse,
            LocalDate.now().plusDays(1)  // Date de naissance future
        );

        boolean resultat = clientService.inscrire(client);
        if (resultat) {
            System.out.println("   ❌ ECHEC : L'inscription avec date de naissance future devrait échouer");
        } else {
            System.out.println("   ✅ SUCCES : L'inscription avec date de naissance future a correctement échoué");
        }
    }

}