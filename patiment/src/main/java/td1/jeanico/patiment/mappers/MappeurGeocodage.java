package td1.jeanico.patiment.mappers;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import td1.jeanico.patiment.modeles.clients.Adresse;

public final class MappeurGeocodage {

    private MappeurGeocodage() {
    }

    /**
     * Extrait une {@link Adresse} depuis une réponse GeoJSON FeatureCollection
     * renvoyée par l'API de géocodage.
     * Retourne {@code null} si aucun résultat n'est trouvé dans la réponse.
     */
    public static Adresse versAdresse(JsonObject json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        JsonArray features = json.getJsonArray("features");
        if (features == null || features.isEmpty()) {
            return null;
        }
        JsonValue premierFeature = features.get(0);
        if (premierFeature.getValueType() != JsonValue.ValueType.OBJECT) {
            return null;
        }
        JsonObject properties = premierFeature.asJsonObject().getJsonObject("properties");
        if (properties == null) {
            return null;
        }
        String housenumber = lireTexte(properties, "housenumber");
        String street = lireTexte(properties, "street");
        String postcode = lireTexte(properties, "postcode");
        String depcode = lireTexte(properties, "depcode");
        String city = lireTexte(properties, "city");
        return new Adresse(housenumber, street, postcode, depcode, city);
    }

    private static String lireTexte(JsonObject json, String cle) {
        if (json.containsKey(cle) && !json.isNull(cle)) {
            JsonValue valeur = json.get(cle);
            if (valeur.getValueType() == JsonValue.ValueType.STRING) {
                return json.getString(cle, "");
            }
        }
        return "";
    }
}
