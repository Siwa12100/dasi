package td1.jeanico.patiment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.ConsultationDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.consultations.Consultation;
import td1.jeanico.patiment.modeles.mediums.Medium;
import td1.jeanico.patiment.modeles.mediums.Spirite;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.services.StatistiqueService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TestIntegrationServiceStatistique extends TestIntegrationAbstrait {

    private final ConsultationDao consultationDao = new ConsultationDao();
    private final MediumDao mediumDao = new MediumDao();
    private final EmployeDao employeDao = new EmployeDao();
    private final ClientDao clientDao = new ClientDao();

    // Fonctionnalite metier testee: comptage des consultations par medium.
    // Comportement attendu: le service compte les consultations de chaque medium et conserve aussi les mediums sans consultation a zero.
    @Test
    void listerNombreConsultationsParMediumCompteLesConsultationsEtGardeLesZeros() {
        Medium mediumA = creerMedium("Mme Irma", Genre.F);
        Medium mediumB = creerMedium("Professeur Ombre", Genre.H);
        Employe employe = creerEmploye("anna@predictif.fr", Genre.F);
        Client client = creerClient("alice@predictif.fr", Genre.F, "69");
        creerConsultation(client, employe, mediumA);
        StatistiqueService statistiqueService = creerService();

        Map<Medium, Integer> statistiques = statistiqueService.listerNombreConsultationsParMedium();

        assertEquals(1, statistiques.get(mediumA));
        assertEquals(0, statistiques.get(mediumB));
    }

    // Fonctionnalite metier testee: repartition des clients distincts par employe.
    // Comportement attendu: plusieurs consultations du meme client avec le meme employe ne comptent qu'une seule fois.
    @Test
    void listerRepartitionClientParEmployeCompteLesClientsDistincts() {
        Medium medium = creerMedium("Mme Irma", Genre.F);
        Employe employe = creerEmploye("anna@predictif.fr", Genre.F);
        Client alice = creerClient("alice@predictif.fr", Genre.F, "69");
        Client zoe = creerClient("zoe@predictif.fr", Genre.F, "69");
        creerConsultation(alice, employe, medium);
        creerConsultation(alice, employe, medium);
        creerConsultation(zoe, employe, medium);
        StatistiqueService statistiqueService = creerService();

        Map<Employe, Integer> repartition = statistiqueService.listerRepartitionClientParEmploye();

        assertEquals(2, repartition.get(employe));
    }

    // Fonctionnalite metier testee: classement des mediums les plus choisis.
    // Comportement attendu: les mediums sont tries par nombre de consultations decroissant.
    @Test
    void listerMediumsPopulaireTrieLesMediumsParVolumeDeConsultations() {
        Medium mediumA = creerMedium("Mme Irma", Genre.F);
        Medium mediumB = creerMedium("Professeur Ombre", Genre.H);
        Employe employe = creerEmploye("anna@predictif.fr", Genre.F);
        Client alice = creerClient("alice@predictif.fr", Genre.F, "69");
        Client zoe = creerClient("zoe@predictif.fr", Genre.F, "01");
        creerConsultation(alice, employe, mediumA);
        creerConsultation(zoe, employe, mediumA);
        creerConsultation(alice, employe, mediumB);
        StatistiqueService statistiqueService = creerService();

        List<Map<Medium, Integer>> topMediums = statistiqueService.listerMediumsPopulaire(2);

        assertFalse(topMediums.isEmpty());
        Medium premierMedium = topMediums.get(0).keySet().iterator().next();
        assertEquals(mediumA.getId(), premierMedium.getId());
        assertEquals(2, topMediums.get(0).values().iterator().next());
    }

    // Fonctionnalite metier testee: repartition geographique des clients.
    // Comportement attendu: les clients sont regroupes par code departement et comptes correctement.
    @Test
    void listerRepartitionGeographiqueClientsCompteLesClientsParDepartement() {
        creerClient("alice@predictif.fr", Genre.F, "69");
        creerClient("zoe@predictif.fr", Genre.F, "69");
        creerClient("tom@predictif.fr", Genre.H, "01");
        StatistiqueService statistiqueService = creerService();

        Map<String, Integer> repartition = statistiqueService.listerRepartitionGeographiqueClients();

        assertEquals(2, repartition.get("69"));
        assertEquals(1, repartition.get("01"));
    }

    private StatistiqueService creerService() {
        return new StatistiqueService(consultationDao, mediumDao, employeDao, clientDao);
    }

    private Client creerClient(String mail, Genre genre, String departement) {
        return executerEnTransaction(() -> {
            Client client = new Client(
                    "Durand",
                    mail.substring(0, mail.indexOf('@')),
                    mail,
                    "secret",
                    "0601020304",
                    genre,
                    new Adresse("12", "rue des Etoiles", "69001", departement, "Lyon"),
                    LocalDate.of(1992, 4, 14)
            );
            clientDao.creer(client);
            return client;
        });
    }

    private Employe creerEmploye(String mail, Genre genre) {
        return executerEnTransaction(() -> {
            Employe employe = new Employe(mail, "Prenom", "Nom", "secret", "0600000001", genre, true);
            employeDao.creer(employe);
            return employe;
        });
    }

    private Medium creerMedium(String denomination, Genre genre) {
        return executerEnTransaction(() -> {
            Medium medium = new Spirite(denomination, genre, "Presentation", "Support");
            mediumDao.creer(medium);
            return medium;
        });
    }

    private Consultation creerConsultation(Client client, Employe employe, Medium medium) {
        return executerEnTransaction(() -> {
            Consultation consultation = new Consultation("", LocalDateTime.now(), false, client, employe, medium);
            consultationDao.creer(consultation);
            return consultation;
        });
    }
}