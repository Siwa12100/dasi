package td1.jeanico.patiment.metier.service;

import java.time.LocalDate;
import td1.jeanico.patiment.dao.ClientDao;
import td1.jeanico.patiment.dao.EmployeDao;
import td1.jeanico.patiment.dao.MediumDao;
import td1.jeanico.patiment.metier.modele.Adresse;
import td1.jeanico.patiment.metier.modele.Astrologue;
import td1.jeanico.patiment.metier.modele.Cartomancien;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.modele.Employe;
import td1.jeanico.patiment.metier.modele.Genre;
import td1.jeanico.patiment.metier.modele.Spirite;

public class InitialisationService extends PersistenceSupport {

    private final MediumDao mediumDao;
    private final EmployeDao employeDao;
    private final ClientDao clientDao;

    public InitialisationService() {
        this(new MediumDao(), new EmployeDao(), new ClientDao());
    }

    public InitialisationService(MediumDao mediumDao, EmployeDao employeDao, ClientDao clientDao) {
        this.mediumDao = mediumDao;
        this.employeDao = employeDao;
        this.clientDao = clientDao;
    }
    
    public void initialisation() {
        executeInTransaction(() -> {
            if (mediumDao.findAllOrderedByDenomination().isEmpty()) {
                mediumDao.create(new Spirite("Mme Irma", Genre.F, "Medium de tradition spirite.", "Boule de cristal"));
                mediumDao.create(new Spirite("Professeur Ombre", Genre.H, "Specialiste des messages de l'au-dela.", "Pendule"));
                mediumDao.create(new Cartomancien("Maitre Soleil", Genre.H, "Expert des tirages a haute precision."));
                mediumDao.create(new Cartomancien("Mlle Arcane", Genre.F, "Lectrice intuitive des energies du moment."));
                mediumDao.create(new Astrologue("Cassandre Vega", Genre.F, "Astrologue moderne et pedagogique.", "ENS Astro", "2015"));
                mediumDao.create(new Astrologue("Orion Delphes", Genre.H, "Interpretation approfondie des themes astraux.", "Institut Celeste", "2012"));
            }

            if (employeDao.findAllOrderedByNomPrenom().isEmpty()) {
                employeDao.create(new Employe("anna@predictif.fr", "Anna", "CONDA", "anna123", "0600000001", Genre.F, true));
                employeDao.create(new Employe("bruno@predictif.fr", "Bruno", "LEMAIRE", "bruno123", "0600000002", Genre.H, false));
                employeDao.create(new Employe("claire@predictif.fr", "Claire", "OBSCURE", "claire123", "0600000003", Genre.NON_SPECIFIE,  true));
            }
            
            if (clientDao.findAllOrderedByNomPrenom().isEmpty()) {
                clientDao.create(new Client("arthur@free.fr", "Arthur", "BOIL", "arthur123", "0400000001", Genre.F, new Adresse("52", "impasse des Buits", "01000", "01", "Bourg-en-Bresse"), LocalDate.now()));
                clientDao.create(new Client("robin@orange.fr", "Robin", "FRUIT", "bruno123", "0400000002", Genre.H, new Adresse("52", "impasse des Buits", "01000", "01", "Bourg-en-Bresse"), LocalDate.now()));
                clientDao.create(new Client("maxime@sfr.fr", "Maxime", "IMMO", "maxime123", "0400000003", Genre.NON_SPECIFIE, new Adresse("66", "avenue des cieux", "69000", "69", "Lyon"), LocalDate.now()));
            }
            return null;
        });
    }
}
