package td1.jeanico.patiment.metier.services;

import java.util.List;
import td1.jeanico.patiment.outils.SupportPersistance;

import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;

public class EmployeService extends SupportPersistance {

    private final EmployeDao employeDao;

    /**
     * Constructeur par défaut.
     */
    public EmployeService() {
        this(new EmployeDao());
    }

    /**
     * Constructeur injectable.
     * @param employeDao
     */
    public EmployeService(EmployeDao employeDao) {
        this.employeDao = employeDao;
    }
    
    /**
     * Récupère un employé par son identifiant.
     * @param id
     * @return 
     */
    public Employe recupererEmployeParId(Long id) {
        if (id == null) {
            return null;
        }
        return executerLecture(() -> employeDao.trouverParId(id));
    }

        /**
     * Liste les employés ordonées par nom/prénom
     * @return 
     */
    public List<Employe> listerEmployes() {
        return executerLecture(employeDao::listerParNomPrenom);
    }
}
