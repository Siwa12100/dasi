package td1.jeanico.patiment.metier.service;

import td1.jeanico.patiment.dao.EmployeDao;
import td1.jeanico.patiment.metier.modele.Employe;

public class EmployeService extends PersistenceSupport {

    private final EmployeDao employeDao;

    public EmployeService() {
        this(new EmployeDao());
    }

    public EmployeService(EmployeDao employeDao) {
        this.employeDao = employeDao;
    }
    
    public Employe recupererEmployeParId(Long id) {
        if (id == null) {
            return null;
        }
        return executeRead(() -> employeDao.findById(id));
    }
}
