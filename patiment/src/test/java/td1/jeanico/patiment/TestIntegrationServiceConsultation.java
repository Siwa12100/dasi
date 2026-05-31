package td1.jeanico.patiment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
import td1.jeanico.patiment.services.ConsultationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestIntegrationServiceConsultation extends TestIntegrationAbstrait {

    private final ConsultationDao consultationDao = new ConsultationDao();
    private final ClientDao clientDao = new ClientDao();
    private final MediumDao mediumDao = new MediumDao();
    private final EmployeDao employeDao = new EmployeDao();

    // Fonctionnalite metier testee: affectation d'une consultation a un employe eligible.
    // Comportement attendu: la consultation est creee pour un employe disponible et de genre compatible avec le medium choisi.
    @Test
    void demanderConsultationAffecteUnEmployeEligible() {
        Client client = creerClient("alice@predictif.fr", Genre.F);
        Medium medium = creerMedium("Mme Irma", Genre.F);
        Employe employeCompatible = creerEmploye("anna@predictif.fr", Genre.F, true);
        creerEmploye("bruno@predictif.fr", Genre.H, true);
        ConsultationService consultationService = creerService();

        boolean consultationCreee = consultationService.demanderConsultation(client, medium);
        List<Consultation> consultations = executerLecture(() -> consultationDao.trouverParClient(client));
        Employe employeRecharge = executerLecture(() -> employeDao.trouverParId(employeCompatible.getId()));

        assertTrue(consultationCreee);
        assertEquals(1, consultations.size());
        assertEquals(employeCompatible.getId(), consultations.get(0).getEmploye().getId());
        assertFalse(employeRecharge.isEstDisponible());
    }

    // Fonctionnalite metier testee: repartition equitable de charge entre employes eligibles.
    // Comportement attendu: l'employe ayant le moins de consultations historiques est selectionne.
    @Test
    void demanderConsultationChoisitLEmployeLeMoinsCharge() {
        Client client = creerClient("alice@predictif.fr", Genre.F);
        Client autreClient = creerClient("zoe@predictif.fr", Genre.F);
        Medium medium = creerMedium("Mme Irma", Genre.F);
        Employe anna = creerEmploye("anna@predictif.fr", Genre.F, true);
        Employe claire = creerEmploye("claire@predictif.fr", Genre.F, true);
        creerConsultation(autreClient, anna, medium, true);
        ConsultationService consultationService = creerService();

        boolean consultationCreee = consultationService.demanderConsultation(client, medium);
        Consultation consultation = executerLecture(() -> consultationDao.trouverParClient(client).get(0));

        assertTrue(consultationCreee);
        assertEquals(claire.getId(), consultation.getEmploye().getId());
    }

    // Fonctionnalite metier testee: refus d'une demande de consultation sans employe eligible.
    // Comportement attendu: la demande est rejetee lorsqu'aucun employe disponible et compatible n'existe.
    @Test
    void demanderConsultationRefuseQuandAucunEmployeEligibleNestDisponible() {
        Client client = creerClient("alice@predictif.fr", Genre.F);
        Medium medium = creerMedium("Mme Irma", Genre.F);
        creerEmploye("bruno@predictif.fr", Genre.H, true);
        creerEmploye("anna@predictif.fr", Genre.F, false);
        ConsultationService consultationService = creerService();

        boolean consultationCreee = consultationService.demanderConsultation(client, medium);

        assertFalse(consultationCreee);
        assertTrue(executerLecture(() -> consultationDao.trouverParClient(client)).isEmpty());
    }

    // Fonctionnalite metier testee: recuperation de la consultation en cours affectee a un employe.
    // Comportement attendu: la consultation non terminee de l'employe est retrouvee depuis le service.
    @Test
    void consulterConsultationAffecteeRetourneLaConsultationEnCours() {
        Client client = creerClient("alice@predictif.fr", Genre.F);
        Medium medium = creerMedium("Mme Irma", Genre.F);
        Employe employe = creerEmploye("anna@predictif.fr", Genre.F, false);
        Consultation consultation = creerConsultation(client, employe, medium, false);
        ConsultationService consultationService = creerService();

        Consultation consultationAffectee = consultationService.consulterConsultationAffectee(employe);

        assertNotNull(consultationAffectee);
        assertEquals(consultation.getId(), consultationAffectee.getId());
    }

    // Fonctionnalite metier testee: cloture d'une consultation.
    // Comportement attendu: la consultation est marquee terminee, le commentaire est sauvegarde et l'employe redevient disponible.
    @Test
    void terminerConsultationClotureLaConsultationEtLibereLEmploye() {
        Client client = creerClient("alice@predictif.fr", Genre.F);
        Medium medium = creerMedium("Mme Irma", Genre.F);
        Employe employe = creerEmploye("anna@predictif.fr", Genre.F, false);
        Consultation consultation = creerConsultation(client, employe, medium, false);
        ConsultationService consultationService = creerService();

        consultationService.terminerConsultation(consultation, "Commentaire metier");

        Consultation consultationRechargee = executerLecture(() -> consultationDao.trouverParId(consultation.getId()));
        Employe employeRecharge = executerLecture(() -> employeDao.trouverParId(employe.getId()));
        assertTrue(consultationRechargee.isEstTermine());
        assertEquals("Commentaire metier", consultationRechargee.getCommentaire());
        assertTrue(employeRecharge.isEstDisponible());
    }

    private ConsultationService creerService() {
        return new ConsultationService(consultationDao, clientDao, mediumDao, employeDao);
    }

    private Client creerClient(String mail, Genre genre) {
        return executerEnTransaction(() -> {
            Client client = new Client(
                    "Durand",
                    mail.substring(0, mail.indexOf('@')),
                    mail,
                    "secret",
                    "0601020304",
                    genre,
                    new Adresse("12", "rue des Etoiles", "69001", "69", "Lyon"),
                    LocalDate.of(1992, 4, 14)
            );
            clientDao.creer(client);
            return client;
        });
    }

    private Employe creerEmploye(String mail, Genre genre, boolean estDisponible) {
        return executerEnTransaction(() -> {
            Employe employe = new Employe(mail, "Prenom", "Nom", "secret", "0600000001", genre, estDisponible);
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

    private Consultation creerConsultation(Client client, Employe employe, Medium medium, boolean estTermine) {
        return executerEnTransaction(() -> {
            Consultation consultation = new Consultation("", LocalDateTime.now(), estTermine, client, employe, medium);
            consultationDao.creer(consultation);
            return consultation;
        });
    }
}