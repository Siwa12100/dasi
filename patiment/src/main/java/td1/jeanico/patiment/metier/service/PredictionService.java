package td1.jeanico.patiment.metier.service;

import java.util.List;
import td1.jeanico.patiment.metier.modele.Prediction;

public class PredictionService {

    private final AstroGateway astroGateway;

    public PredictionService() {
        this(new AstroGateway());
    }

    public PredictionService(AstroGateway astroGateway) {
        this.astroGateway = astroGateway;
    }
    
    public List<Prediction> demandeInspiration(int scoreAmour, int scoreSante, int scoreTravail) {
        return astroGateway.demanderInspiration(scoreAmour, scoreSante, scoreTravail);
    }
}
