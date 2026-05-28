package td1.jeanico.patiment.metier.service.statistique;

import java.util.List;
import java.util.Map;
import td1.jeanico.patiment.metier.modele.Employe;
import td1.jeanico.patiment.metier.modele.Medium;

public interface IStatistiqueService {

    Map<Medium, Integer> listerNombreConsultationsParMedium();

    Map<Employe, Integer> listerRepartitionClientParEmploye();

    List<Map<Medium, Integer>> listerMediumsPopulaire(int nbMediums);

    Map<String, Integer> listerRepartitionGeographiqueClients();
}
