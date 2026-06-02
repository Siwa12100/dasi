package td1.jeanico.patiment.metier.services;

import td1.jeanico.patiment.outils.SupportPersistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.mediums.TypeMedium;

public class MediumService extends SupportPersistance {

    private final MediumDao mediumDao;

    /**
     * Constructeur par défaut.
     */
    public MediumService() {
        this(new MediumDao());
    }

    /**
     * Constructeur injectable.
     * @param mediumDao
     */
    public MediumService(MediumDao mediumDao) {
        this.mediumDao = mediumDao;
    }
    
    /**
     * Liste tous les médiums par ordre alphabétique de dénomination.
     * @return 
     */
    public List<Medium> listerMediums() {
        return executerLecture(mediumDao::listerParDenomination);
    }
    
    /**
     * Liste les médiums filtrés par type.
     * Si le type est null, retourne la liste complète.
     * @param typeMedium
     * @return 
     */
    public List<Medium> listerMediums(TypeMedium typeMedium) {
        if (typeMedium == null) {
            return listerMediums();
        }
        return executerLecture(() -> switch (typeMedium) {
            case Spirite -> new ArrayList<>(mediumDao.listerSpiritesParDenomination());
            case Cartomancien -> new ArrayList<>(mediumDao.listerCartomanciensParDenomination());
            case Astrologue -> new ArrayList<>(mediumDao.listerAstrologuesParDenomination());
        });
    }
    
    /**
     * Retourne l'ensemble des types de médium disponibles.
     * @return 
     */
    public List<TypeMedium> listerTypesMedium() {
        return Arrays.asList(TypeMedium.values());
    }
    
    /**
     * Récupère un médium par son identifiant.
     * @param id
     * @return 
     */
    public Medium recupererMediumParId(Long id) {
        if (id == null) {
            return null;
        }
        return executerLecture(() -> mediumDao.trouverParId(id));
    }
}
