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
        
    }
}
