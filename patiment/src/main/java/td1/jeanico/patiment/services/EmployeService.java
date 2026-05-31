package td1.jeanico.patiment.services;

import td1.jeanico.patiment.outils.SupportPersistance;

import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;

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
     */
    public EmployeService(EmployeDao employeDao) {
        this.employeDao = employeDao;
    }

    /**
     * Authentifie un employé par mail/mot de passe.
     */
    public Employe authentifier(String mail, String motDePasse) {
        if (estVide(mail) || estVide(motDePasse)) {
            return null;
        }
        return executerLecture(() -> employeDao.trouverParMailEtMotDePasse(mail, motDePasse));
    }
    
    /**
     * Récupère un employé par son identifiant.
     */
    public Employe recupererEmployeParId(Long id) {
        if (id == null) {
            return null;
        }
        return executerLecture(() -> employeDao.trouverParId(id));
    }

    /**
     * Indique si une chaîne est nulle ou vide.
     */
    private boolean estVide(String value) {
        return value == null || value.isBlank();
    }
}
