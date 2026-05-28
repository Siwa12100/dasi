package td1.jeanico.patiment.metier.service.prediction;

import java.util.List;
import td1.jeanico.patiment.metier.modele.Prediction;

public interface IPredictionService {

    List<Prediction> demandeInspiration(int scoreAmour, int scoreSante, int scoreTravail);
}
