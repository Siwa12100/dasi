package td1.jeanico.patiment.mappers;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import td1.jeanico.patiment.metier.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.metier.modeles.consultations.Prediction;

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
        
        String animal = lireTexte(source, "animal");
        String signeZodiaque = lireTexte(source, "signe-zodiaque");
        String couleur = lireTexte(source, "couleur");
        String signeChinois = lireTexte(source, "signe-chinois");
        
        if (!animal.isEmpty() && !signeZodiaque.isEmpty() && !couleur.isEmpty() && !signeChinois.isEmpty()) {
            return new ProfilAstral(
                animal,
                signeZodiaque,
                couleur,
                signeChinois
            );
        }
        
        return null;
    }

    public static Prediction versPrediction(JsonObject json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        String amour = lireTexte(json, "prediction-amour");
        String sante = lireTexte(json, "prediction-sante");
        String travail = lireTexte(json, "prediction-travail");

        if (!amour.isEmpty() && !sante.isEmpty() && !travail.isEmpty()) {
            return new Prediction(amour, sante, travail);
        }
        
        return null;
    }

    private static JsonObject extraireObjetProfil(JsonObject json) {
        if (json.containsKey("profil") && json.get("profil").getValueType() == JsonValue.ValueType.OBJECT) {
            return json.getJsonObject("profil");
        }
        return null;
    }

    private static String lireTexte(JsonObject json, String cle) {
        if (json.containsKey(cle) && !json.isNull(cle)) {
            JsonValue valeur = json.get(cle);
            if (valeur.getValueType() == JsonValue.ValueType.STRING) {
                return json.getString(cle, "");
            }
            return valeur.toString().replace('"', ' ').trim();
        }
        return "";
    }
}