package td1.jeanico.patiment;

import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.services.EmployeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestIntegrationServiceEmploye extends TestIntegrationAbstrait {

    private final EmployeDao employeDao = new EmployeDao();

    // Fonctionnalite metier testee: authentification d'un employe.
    // Comportement attendu: l'employe est retrouve avec les bons identifiants et refuse avec des identifiants invalides.
    @Test
    void authentifierValideLesIdentifiantsDeLEmploye() {
        Employe employe = creerEmploye("anna@predictif.fr", "anna123", Genre.F, true);
        EmployeService employeService = new EmployeService(employeDao);

        Employe employeAuthentifie = employeService.authentifier("anna@predictif.fr", "anna123");
        Employe employeRefuse = employeService.authentifier("anna@predictif.fr", "erreur");

        assertNotNull(employeAuthentifie);
        assertEquals(employe.getId(), employeAuthentifie.getId());
        assertNull(employeRefuse);
    }

    // Fonctionnalite metier testee: recuperation d'un employe par identifiant.
    // Comportement attendu: l'employe persiste est retrouve par son identifiant technique.
    @Test
    void recupererEmployeParIdRetourneLEmployePersistant() {
        Employe employe = creerEmploye("claire@predictif.fr", "claire123", Genre.NON_SPECIFIE, true);
        EmployeService employeService = new EmployeService(employeDao);

        Employe employePersistant = employeService.recupererEmployeParId(employe.getId());

        assertNotNull(employePersistant);
        assertEquals("claire@predictif.fr", employePersistant.getMail());
    }

    private Employe creerEmploye(String mail, String motDePasse, Genre genre, boolean estDisponible) {
        return executerEnTransaction(() -> {
            Employe employe = new Employe(mail, "Test", "Employe", motDePasse, "0600000000", genre, estDisponible);
            employeDao.creer(employe);
            return employe;
        });
    }
}