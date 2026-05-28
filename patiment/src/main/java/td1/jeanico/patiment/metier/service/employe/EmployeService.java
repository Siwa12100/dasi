package td1.jeanico.patiment.metier.service.employe;

import td1.jeanico.patiment.dao.EmployeDao;
import td1.jeanico.patiment.metier.modele.Employe;
import td1.jeanico.patiment.metier.service.support.PersistenceSupport;

public class EmployeService extends PersistenceSupport implements IEmployeService {

    private final EmployeDao employeDao;

    public EmployeService() {
        this(new EmployeDao());
    }

    public EmployeService(EmployeDao employeDao) {
        this.employeDao = employeDao;
    }

    @Override
    public Employe recupererEmployeParId(Long id) {
        if (id == null) {
            return null;
        }
        return executeRead(() -> employeDao.findById(id));
    }
}
