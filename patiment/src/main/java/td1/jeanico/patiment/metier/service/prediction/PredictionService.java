package td1.jeanico.patiment.metier.service.prediction;

import java.util.List;
import td1.jeanico.patiment.metier.modele.Prediction;
import td1.jeanico.patiment.metier.service.support.AstroGateway;

public class PredictionService implements IPredictionService {

    private final AstroGateway astroGateway;

    public PredictionService() {
        this(new AstroGateway());
    }

    public PredictionService(AstroGateway astroGateway) {
        this.astroGateway = astroGateway;
    }

    @Override
    public List<Prediction> demandeInspiration(int scoreAmour, int scoreSante, int scoreTravail) {
        return astroGateway.demanderInspiration(scoreAmour, scoreSante, scoreTravail);
    }
}
