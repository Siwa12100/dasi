package td1.jeanico.patiment.mappers;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.List;
import td1.jeanico.patiment.modeles.consultations.Prediction;
import td1.jeanico.patiment.modeles.clients.ProfilAstral;

public final class MappeurAstroNet {

    private MappeurAstroNet() {
    }

    public static ProfilAstral versProfilAstral(JsonObject json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        JsonObject source = extraireObjetProfil(json);
        if (source == null) {
            source = json;
        }

        return new ProfilAstral(
            lireTexte(source, "animalTotal", "animal_totem", "animalTotem", "animal", "totem", "animal-totem", "animal_totem"),
            lireTexte(source, "signeZodiac", "signe_zodiac", "signe", "zodiac", "signe-zodiaque", "signe_zodiaque"),
            lireTexte(source, "couleurBonheur", "couleur_bonheur", "couleur", "bonheur", "couleur-bonheur", "couleur_bonheur"),
            lireTexte(source, "signeChinois", "signe_chinois", "chinois", "signeChinois", "signe-chinois", "signe_chinois")
        );
    }

    public static List<Prediction> versPredictions(JsonObject json) {
        List<Prediction> predictions = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return predictions;
        }

        JsonObject source = extraireObjetPrediction(json);
        if (source != null) {
            predictions.add(new Prediction(
                    lireTexte(source, "amour", "predictionAmour", "predAmour", "prediction-amour"),
                    lireTexte(source, "sante", "santé", "predictionSante", "predSante", "prediction-sante"),
                    lireTexte(source, "travail", "predictionTravail", "predTravail", "prediction-travail")
            ));
            return predictions;
        }

        String amour = lireTexte(json, "amour", "predictionAmour", "predAmour", "prediction-amour");
        String sante = lireTexte(json, "sante", "santé", "predictionSante", "predSante", "prediction-sante");
        String travail = lireTexte(json, "travail", "predictionTravail", "predTravail", "prediction-travail");

        if (!amour.isEmpty() || !sante.isEmpty() || !travail.isEmpty()) {
            predictions.add(new Prediction(amour, sante, travail));
            return predictions;
        }

        JsonArray tableau = extraireTableauPredictions(json);
        if (tableau != null) {
            for (JsonValue valeur : tableau) {
                if (valeur.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject element = valeur.asJsonObject();
                    predictions.add(new Prediction(
                            lireTexte(element, "amour", "predictionAmour", "predAmour", "prediction-amour"),
                            lireTexte(element, "sante", "santé", "predictionSante", "predSante", "prediction-sante"),
                            lireTexte(element, "travail", "predictionTravail", "predTravail", "prediction-travail")
                    ));
                }
            }
        }

        return predictions;
    }

    private static JsonObject extraireObjetProfil(JsonObject json) {
        if (json.containsKey("profil") && json.get("profil").getValueType() == JsonValue.ValueType.OBJECT) {
            return json.getJsonObject("profil");
        }
        if (json.containsKey("data") && json.get("data").getValueType() == JsonValue.ValueType.OBJECT) {
            return json.getJsonObject("data");
        }
        return null;
    }

    private static JsonObject extraireObjetPrediction(JsonObject json) {
        if (json.containsKey("prediction") && json.get("prediction").getValueType() == JsonValue.ValueType.OBJECT) {
            return json.getJsonObject("prediction");
        }
        if (json.containsKey("predictions") && json.get("predictions").getValueType() == JsonValue.ValueType.OBJECT) {
            return json.getJsonObject("predictions");
        }
        return null;
    }

    private static JsonArray extraireTableauPredictions(JsonObject json) {
        if (json.containsKey("predictions") && json.get("predictions").getValueType() == JsonValue.ValueType.ARRAY) {
            return json.getJsonArray("predictions");
        }
        return null;
    }

    private static String lireTexte(JsonObject json, String... cles) {
        for (String cle : cles) {
            if (json.containsKey(cle) && !json.isNull(cle)) {
                JsonValue valeur = json.get(cle);
                if (valeur.getValueType() == JsonValue.ValueType.STRING) {
                    return json.getString(cle, "");
                }
                return valeur.toString().replace('"', ' ').trim();
            }
        }
        return "";
    }
}