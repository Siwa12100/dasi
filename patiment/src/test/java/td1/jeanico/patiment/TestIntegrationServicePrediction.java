package td1.jeanico.patiment;

import java.util.List;
import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.modeles.consultations.Prediction;
import td1.jeanico.patiment.services.PredictionService;
import td1.jeanico.patiment.webClients.ClientWebAstroNet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestIntegrationServicePrediction extends TestIntegrationAbstrait {

    // Fonctionnalite metier testee: demande d'inspiration personnalisee sur un profil astral valide.
    // Comportement attendu: le service transmet la couleur porte-bonheur et l'animal-totem du profil astral a l'API et retourne une prediction exploitable.
    @Test
    void demandeInspirationUtiliseLeProfilAstralEtRetourneUnePrediction() {
        PredictionService predictionService = new PredictionService(new ClientWebAstroNet());

        List<Prediction> predictions = predictionService.demandeInspiration(
                new ProfilAstral("Loup", "Taureau", "Bleu", "Singe"),
                2,
                3,
                4
        );

        assertEquals(1, predictions.size());
        assertFalse(predictions.get(0).getAmour().isBlank());
        assertFalse(predictions.get(0).getSante().isBlank());
        assertFalse(predictions.get(0).getTravail().isBlank());
        assertTrue(predictions.get(0).getAmour().contains("Signe antagoniste:"));
        assertTrue(predictions.get(0).getSante().contains("Conseil:"));
        assertTrue(predictions.get(0).getTravail().contains("Signe collaborateur:"));
    }

    // Fonctionnalite metier testee: rejet d'une demande d'inspiration avec des scores hors bornes.
    // Comportement attendu: le service neutralise la demande et n'appelle pas l'API externe.
    @Test
    void demandeInspirationRefuseLesScoresHorsBornes() {
        PredictionService predictionService = new PredictionService(new ClientWebAstroNet());

        List<Prediction> predictions = predictionService.demandeInspiration(
                new ProfilAstral("Loup", "Taureau", "Bleu", "Singe"),
                0,
                5,
                2
        );

        assertEquals(1, predictions.size());
        assertEquals("", predictions.get(0).getAmour());
        assertEquals("", predictions.get(0).getSante());
        assertEquals("", predictions.get(0).getTravail());
    }

    // Fonctionnalite metier testee: rejet d'une demande d'inspiration si le profil astral est incomplet.
    // Comportement attendu: la demande est neutralisee et le service externe n'est pas contacte.
    @Test
    void demandeInspirationRefuseUnProfilAstralIncomplet() {
        PredictionService predictionService = new PredictionService(new ClientWebAstroNet());

        List<Prediction> predictions = predictionService.demandeInspiration(
                new ProfilAstral("Loup", "Taureau", "", "Singe"),
                2,
                3,
                4
        );

        assertEquals(1, predictions.size());
        assertEquals("", predictions.get(0).getAmour());
    }
}