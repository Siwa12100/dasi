package td1.jeanico.patiment;

import java.util.List;
import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.mediums.Astrologue;
import td1.jeanico.patiment.modeles.mediums.Cartomancien;
import td1.jeanico.patiment.modeles.mediums.Medium;
import td1.jeanico.patiment.modeles.mediums.Spirite;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.services.InitialisationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestIntegrationServiceInitialisation extends TestIntegrationAbstrait {

    private final MediumDao mediumDao = new MediumDao();
    private final EmployeDao employeDao = new EmployeDao();
    private final ClientDao clientDao = new ClientDao();

    // Fonctionnalite metier testee: initialisation du jeu de donnees de base.
    // Comportement attendu: les mediums, employes et clients de depart sont crees une fois en base.
    @Test
    void initialisationCreerLeJeuDeDonneesDeDepart() {
        InitialisationService initialisationService = new InitialisationService(mediumDao, employeDao, clientDao);

        initialisationService.initialisation();

        List<Medium> mediums = executerLecture(mediumDao::listerParDenomination);
        List<Employe> employes = executerLecture(employeDao::listerParNomPrenom);
        List<Client> clients = executerLecture(clientDao::listerParNomPrenom);

        assertEquals(6, mediums.size());
        assertEquals(3, employes.size());
        assertEquals(3, clients.size());
        assertTrue(mediums.stream().anyMatch(medium -> medium instanceof Spirite));
        assertTrue(mediums.stream().anyMatch(medium -> medium instanceof Cartomancien));
        assertTrue(mediums.stream().anyMatch(medium -> medium instanceof Astrologue));
    }

    // Fonctionnalite metier testee: idempotence de l'initialisation.
    // Comportement attendu: relancer l'initialisation ne duplique pas les donnees deja presentes.
    @Test
    void initialisationResteIdempotente() {
        InitialisationService initialisationService = new InitialisationService(mediumDao, employeDao, clientDao);

        initialisationService.initialisation();
        initialisationService.initialisation();

        assertEquals(6, executerLecture(mediumDao::listerParDenomination).size());
        assertEquals(3, executerLecture(employeDao::listerParNomPrenom).size());
        assertEquals(3, executerLecture(clientDao::listerParNomPrenom).size());
    }
}