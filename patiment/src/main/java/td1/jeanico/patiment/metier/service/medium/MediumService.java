package td1.jeanico.patiment.metier.service.medium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import td1.jeanico.patiment.dao.MediumDao;
import td1.jeanico.patiment.metier.modele.Medium;
import td1.jeanico.patiment.metier.modele.TypeMedium;
import td1.jeanico.patiment.metier.service.support.PersistenceSupport;

public class MediumService extends PersistenceSupport implements IMediumService {

    private final MediumDao mediumDao;

    public MediumService() {
        this(new MediumDao());
    }

    public MediumService(MediumDao mediumDao) {
        this.mediumDao = mediumDao;
    }

    @Override
    public List<Medium> listerMediums() {
        return executeRead(mediumDao::findAllOrderedByDenomination);
    }

    @Override
    public List<Medium> listerMediums(TypeMedium typeMedium) {
        if (typeMedium == null) {
            return listerMediums();
        }
        return executeRead(() -> switch (typeMedium) {
            case SPIRITE -> new ArrayList<>(mediumDao.findSpiritesOrderedByDenomination());
            case CARTOMANCIEN -> new ArrayList<>(mediumDao.findCartomanciensOrderedByDenomination());
            case ASTROLOGUE -> new ArrayList<>(mediumDao.findAstrologuesOrderedByDenomination());
        });
    }

    @Override
    public List<TypeMedium> listerTypesMedium() {
        return Arrays.asList(TypeMedium.values());
    }

    @Override
    public Medium recupererMediumParId(Long id) {
        if (id == null) {
            return null;
        }
        return executeRead(() -> mediumDao.findById(id));
    }
}
