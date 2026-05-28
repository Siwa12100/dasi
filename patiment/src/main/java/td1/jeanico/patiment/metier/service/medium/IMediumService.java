package td1.jeanico.patiment.metier.service.medium;

import java.util.List;
import td1.jeanico.patiment.metier.modele.Medium;
import td1.jeanico.patiment.metier.modele.TypeMedium;

public interface IMediumService {

    List<Medium> listerMediums();

    List<Medium> listerMediums(TypeMedium typeMedium);

    List<TypeMedium> listerTypesMedium();

    Medium recupererMediumParId(Long id);
}
