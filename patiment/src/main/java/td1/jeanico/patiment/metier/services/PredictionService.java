package td1.jeanico.patiment.metier.services;

import td1.jeanico.patiment.mappers.MappeurAstroNet;
import td1.jeanico.patiment.metier.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.metier.modeles.consultations.Prediction;
import jakarta.json.JsonObject;
import td1.jeanico.patiment.webClients.ClientWebAstroNet;

public class PredictionService {

    private final ClientWebAstroNet astroNetWebClient;

    /**
     * Constructeur par défaut.
     */
    public PredictionService() {
        this(new ClientWebAstroNet());
    }

    /**
     * Constructeur injectable.
     * @param astroNetWebClient
     */
    public PredictionService(ClientWebAstroNet astroNetWebClient) {
        this.astroNetWebClient = astroNetWebClient;
    }

    /**
     * Demande une inspiration à AstroNet à partir d'un profil astral et de 3 scores.
     * Retourne une prédiction vide si les entrées sont invalides ou si l'API échoue.
     * @param profilAstral
     * @param scoreAmour
     * @param scoreSante
     * @param scoreTravail
     * @return 
     */
    public Prediction demandeInspiration(ProfilAstral profilAstral, int scoreAmour, int scoreSante, int scoreTravail) {
        if (!estScoreValide(scoreAmour)
                || !estScoreValide(scoreSante)
                || !estScoreValide(scoreTravail)
                || !profilAstralValide(profilAstral)) {
            return null;
        }
        try {
            JsonObject jsonPredictions = astroNetWebClient.recupererPredictions(
                    profilAstral.getCouleurBonheur(),
                    profilAstral.getAnimalTotem(),
                    scoreAmour,
                    scoreSante,
                    scoreTravail
            );
            return MappeurAstroNet.versPrediction(jsonPredictions);
        } catch (RuntimeException ex) {
            // Fallback robuste: la couche appelante reçoit toujours un objet Prediction.
            return null;
        }
    }

    /**
     * Vérifie qu'un score est dans l'intervalle [1..4].
     */
    private boolean estScoreValide(int score) {
        return score >= 1 && score <= 4;
    }

    /**
     * Vérifie la complétude minimale du profil nécessaire à AstroNet.
     */
    private boolean profilAstralValide(ProfilAstral profilAstral) {
        return profilAstral != null
                && !estVide(profilAstral.getCouleurBonheur())
                && !estVide(profilAstral.getAnimalTotem());
    }

    /**
     * Indique si une chaîne est nulle ou vide.
     */
    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
