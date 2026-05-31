package td1.jeanico.patiment;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.services.ClientService;
import td1.jeanico.patiment.webClients.ClientWebAstroNet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestIntegrationServiceClient extends TestIntegrationAbstrait {

    private final ClientDao clientDao = new ClientDao();

    // Fonctionnalite metier testee: inscription d'un client avec calcul du profil astral.
    // Comportement attendu: l'inscription reussit et persiste le client avec un profil astral complet en base.
    @Test
    void inscrirePersisteLeClientEtSonProfilAstral() {
        ClientService clientService = new ClientService(clientDao, new ClientWebAstroNet());
        Client client = creerClient("alice@predictif.fr");

        boolean inscriptionReussie = clientService.inscrire(client);

        assertTrue(inscriptionReussie);
        Client clientPersistant = clientService.authentifier("alice@predictif.fr", "secret");
        assertNotNull(clientPersistant);
        assertNotNull(clientPersistant.getId());
        assertNotNull(clientPersistant.getProfilAstral());
        assertEquals("Bélier", clientPersistant.getProfilAstral().getSigneZodiac());
        assertEquals("Sphynx", clientPersistant.getProfilAstral().getAnimalTotal());
        assertEquals("Rouge anglais", clientPersistant.getProfilAstral().getCouleurBonheur());
        assertEquals("Singe", clientPersistant.getProfilAstral().getSigneChinois());
    }

    // Fonctionnalite metier testee: inscription client avec unicite du mail.
    // Comportement attendu: une seconde inscription sur le meme mail est refusee et ne cree pas de doublon en base.
    @Test
    void inscrireRefuseUnMailDejaUtilise() {
        ClientService clientService = new ClientService(clientDao, new ClientWebAstroNet());

        assertTrue(clientService.inscrire(creerClient("alice@predictif.fr")));
        assertFalse(clientService.inscrire(creerClient("alice@predictif.fr")));

        List<Client> clients = executerLecture(clientDao::listerParNomPrenom);
        assertEquals(1, clients.size());
    }

    // Fonctionnalite metier testee: validation des donnees d'inscription client.
    // Comportement attendu: une inscription incomplete est rejetee et aucun client n'est persiste en base.
    @Test
    void inscrireRefuseUnClientIncomplet() {
        ClientService clientService = new ClientService(clientDao, new ClientWebAstroNet());
        Client clientInvalide = creerClient("invalide@predictif.fr");
        clientInvalide.setAdresse(new Adresse("", "", "", "", ""));

        boolean inscriptionReussie = clientService.inscrire(clientInvalide);

        assertFalse(inscriptionReussie);
        assertTrue(executerLecture(clientDao::listerParNomPrenom).isEmpty());
    }

    // Fonctionnalite metier testee: authentification d'un client.
    // Comportement attendu: le client est retrouve avec les bons identifiants et refuse avec un mot de passe errone.
    @Test
    void authentifierValideLesIdentifiantsDuClient() {
        ClientService clientService = new ClientService(clientDao, new ClientWebAstroNet());
        assertTrue(clientService.inscrire(creerClient("alice@predictif.fr")));

        Client clientAuthentifie = clientService.authentifier("alice@predictif.fr", "secret");
        Client clientRefuse = clientService.authentifier("alice@predictif.fr", "mauvais-mdp");

        assertNotNull(clientAuthentifie);
        assertEquals("alice@predictif.fr", clientAuthentifie.getMail());
        assertNull(clientRefuse);
    }

    // Fonctionnalite metier testee: consultation du profil astral deja enregistre pour un client.
    // Comportement attendu: le profil deja persiste est retourne sans rappeler le service externe.
    @Test
    void consulterProfilAstralReutiliseLeProfilDejaPersistant() {
        ClientService clientService = new ClientService(clientDao, new ClientWebAstroNet());
        assertTrue(clientService.inscrire(creerClient("alice@predictif.fr")));

        Client clientPersistant = clientService.authentifier("alice@predictif.fr", "secret");
        ProfilAstral profilAstral = clientService.consulterProfilAstral(clientPersistant);

        assertNotNull(profilAstral);
        assertEquals("Bélier", profilAstral.getSigneZodiac());
        assertEquals("Sphynx", profilAstral.getAnimalTotal());
        assertEquals("Rouge anglais", profilAstral.getCouleurBonheur());
    }

    private Client creerClient(String mail) {
        return new Client(
                "Durand",
                "Alice",
                mail,
                "secret",
                "0601020304",
                Genre.F,
                new Adresse("12", "rue des Etoiles", "69001", "69", "Lyon"),
                LocalDate.of(1992, 4, 14)
        );
    }

}