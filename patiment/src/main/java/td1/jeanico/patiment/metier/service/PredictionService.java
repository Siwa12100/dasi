package td1.jeanico.patiment.metier.service.prediction;

import java.util.List;
import jakarta.json.JsonObject;
import td1.jeanico.patiment.metier.modele.Prediction;
import td1.jeanico.patiment.metier.service.support.IfAstroNetMapper;
import td1.jeanico.patiment.webclient.IfAstroNetWebClient;

public class PredictionService implements IPredictionService {

    private final IfAstroNetWebClient astroNetWebClient;

    public PredictionService() {
        this(new IfAstroNetWebClient());
    }

    public PredictionService(IfAstroNetWebClient astroNetWebClient) {
        this.astroNetWebClient = astroNetWebClient;
    }

    @Override
    public List<Prediction> demandeInspiration(int scoreAmour, int scoreSante, int scoreTravail) {
        try {
            JsonObject jsonPredictions = astroNetWebClient.recupererPredictions("", "", scoreAmour, scoreSante, scoreTravail);
            List<Prediction> predictions = IfAstroNetMapper.versPredictions(jsonPredictions);
            return predictions.isEmpty() ? List.of(new Prediction("", "", "")) : predictions;
        } catch (RuntimeException ex) {
            return List.of(new Prediction("", "", ""));
        }
    }
}
