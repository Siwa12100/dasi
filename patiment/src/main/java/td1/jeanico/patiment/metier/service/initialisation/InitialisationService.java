package td1.jeanico.patiment.metier.service.initialisation;

import td1.jeanico.patiment.dao.EmployeDao;
import td1.jeanico.patiment.dao.MediumDao;
import td1.jeanico.patiment.metier.modele.Astrologue;
import td1.jeanico.patiment.metier.modele.Cartomancien;
import td1.jeanico.patiment.metier.modele.Employe;
import td1.jeanico.patiment.metier.modele.Genre;
import td1.jeanico.patiment.metier.modele.Spirite;
import td1.jeanico.patiment.metier.service.support.PersistenceSupport;

public class InitialisationService extends PersistenceSupport implements IInitialisationService {

    private final MediumDao mediumDao;
    private final EmployeDao employeDao;

    public InitialisationService() {
        this(new MediumDao(), new EmployeDao());
    }

    public InitialisationService(MediumDao mediumDao, EmployeDao employeDao) {
        this.mediumDao = mediumDao;
        this.employeDao = employeDao;
    }

    @Override
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

            if (employeDao.findAllOrderedByPrenom().isEmpty()) {
                employeDao.create(new Employe("anna@predictif.fr", "Anna", "anna123", "0600000001", true));
                employeDao.create(new Employe("bruno@predictif.fr", "Bruno", "bruno123", "0600000002", true));
                employeDao.create(new Employe("claire@predictif.fr", "Claire", "claire123", "0600000003", true));
            }
            return null;
        });
    }
}
