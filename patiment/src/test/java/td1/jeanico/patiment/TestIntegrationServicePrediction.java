package td1.jeanico.patiment;

import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.modeles.consultations.Prediction;
import td1.jeanico.patiment.services.PredictionService;
import td1.jeanico.patiment.webClients.ClientWebAstroNet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestIntegrationServicePrediction extends TestIntegrationAbstrait {

    // Fonctionnalite metier testee: demande d'inspiration personnalisee sur un profil astral valide.
    // Comportement attendu: le service transmet la couleur porte-bonheur et l'animal-totem du profil astral a l'API et retourne une prediction exploitable.
    @Test
    void demandeInspirationUtiliseLeProfilAstralEtRetourneUnePrediction() {
        PredictionService predictionService = new PredictionService(new ClientWebAstroNet());

        Prediction prediction = predictionService.demandeInspiration(
                new ProfilAstral("Loup", "Taureau", "Bleu", "Singe"),
                2,
                3,
                4
        );

        assertNotNull(prediction);
        assertFalse(prediction.getAmour().isBlank());
        assertFalse(prediction.getSante().isBlank());
        assertFalse(prediction.getTravail().isBlank());
        assertTrue(prediction.getAmour().contains("Signe antagoniste:"));
        assertTrue(prediction.getSante().contains("Conseil:"));
        assertTrue(prediction.getTravail().contains("Signe collaborateur:"));
    }

    // Fonctionnalite metier testee: rejet d'une demande d'inspiration avec des scores hors bornes.
    // Comportement attendu: le service neutralise la demande et n'appelle pas l'API externe.
    @Test
    void demandeInspirationRefuseLesScoresHorsBornes() {
        PredictionService predictionService = new PredictionService(new ClientWebAstroNet());

        Prediction prediction = predictionService.demandeInspiration(
                new ProfilAstral("Loup", "Taureau", "Bleu", "Singe"),
                0,
                5,
                2
        );

        assertNull(prediction);
    }

    // Fonctionnalite metier testee: rejet d'une demande d'inspiration si le profil astral est incomplet.
    // Comportement attendu: la demande est neutralisee et le service externe n'est pas contacte.
    @Test
    void demandeInspirationRefuseUnProfilAstralIncomplet() {
        PredictionService predictionService = new PredictionService(new ClientWebAstroNet());

        Prediction prediction = predictionService.demandeInspiration(
                new ProfilAstral("Loup", "Taureau", "", "Singe"),
                2,
                3,
                4
        );

        assertNull(prediction);
    }
}