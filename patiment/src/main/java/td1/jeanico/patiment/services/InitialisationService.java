package td1.jeanico.patiment.services;

import td1.jeanico.patiment.outils.SupportPersistance;

import java.time.LocalDate;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.modeles.mediums.Astrologue;
import td1.jeanico.patiment.modeles.mediums.Cartomancien;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.modeles.mediums.Spirite;

public class InitialisationService extends SupportPersistance {

    private final MediumDao mediumDao;
    private final EmployeDao employeDao;
    private final ClientDao clientDao;

    /**
     * Constructeur par défaut.
     */
    public InitialisationService() {
        this(new MediumDao(), new EmployeDao(), new ClientDao());
    }

    /**
     * Constructeur injectable.
     * @param mediumDao
     * @param employeDao
     * @param clientDao
     */
    public InitialisationService(MediumDao mediumDao, EmployeDao employeDao, ClientDao clientDao) {
        this.mediumDao = mediumDao;
        this.employeDao = employeDao;
        this.clientDao = clientDao;
    }
    
    /**
     * Initialise les données de référence (médiums, employés, clients).
     * La méthode est idempotente: elle n'ajoute pas de doublons si les tables sont déjà remplies.
     */
    public void initialisation() {
        executerEnTransaction(() -> {
            if (mediumDao.listerParDenomination().isEmpty()) {
                mediumDao.creer(new Spirite("Mme Irma", Genre.FEMME, "Medium de tradition spirite.", "Boule de cristal"));
                mediumDao.creer(new Spirite("Professeur Ombre", Genre.HOMME, "Specialiste des messages de l'au-dela.", "Pendule"));
                mediumDao.creer(new Cartomancien("Maitre Soleil", Genre.HOMME, "Expert des tirages a haute precision."));
                mediumDao.creer(new Cartomancien("Mlle Arcane", Genre.FEMME, "Lectrice intuitive des energies du moment."));
                mediumDao.creer(new Astrologue("Cassandre Vega", Genre.FEMME, "Astrologue moderne et pedagogique.", "ENS Astro", "2015"));
                mediumDao.creer(new Astrologue("Orion Delphes", Genre.HOMME, "Interpretation approfondie des themes astraux.", "Institut Celeste", "2012"));
            }

            if (employeDao.listerParNomPrenom().isEmpty()) {
                employeDao.creer(new Employe("anna@predictif.fr", "Anna", "CONDA", "anna123", "0600000001", Genre.FEMME, true));
                employeDao.creer(new Employe("bruno@predictif.fr", "Bruno", "LEMAIRE", "bruno123", "0600000002", Genre.HOMME, false));
                employeDao.creer(new Employe("claire@predictif.fr", "Claire", "OBSCURE", "claire123", "0600000003", Genre.NON_SPECIFIE,  true));
            }
            
            if (clientDao.listerParNomPrenom().isEmpty()) {
                clientDao.creer(new Client("BOIL", "Arthur", "arthur@free.fr", "arthur123", "0400000001", Genre.FEMME, new Adresse("52", "impasse des Buits", "01000", "01", "Bourg-en-Bresse"), LocalDate.of(1994, 3, 18)));
                clientDao.creer(new Client("FRUIT", "Robin", "robin@orange.fr", "bruno123", "0400000002", Genre.HOMME, new Adresse("52", "impasse des Buits", "01000", "01", "Bourg-en-Bresse"), LocalDate.of(1988, 7, 9)));
                clientDao.creer(new Client("IMMO", "Maxime", "maxime@sfr.fr", "maxime123", "0400000003", Genre.NON_SPECIFIE, new Adresse("66", "avenue des cieux", "69000", "69", "Lyon"), LocalDate.of(1998, 11, 24)));
            }
            return null;
        });
    }
}
