package td1.jeanico.patiment.webClients;

import jakarta.json.JsonObject;
import java.net.http.HttpClient;

public class ClientWebGeocodage extends ClientWebAbstrait {

    public static final String URL = "https://data.geopf.fr/geocodage/search";

    /**
     * Constructeur par défaut.
     */
    public ClientWebGeocodage() {
        super();
    }

    /**
     * Constructeur injectable.
     */
    public ClientWebGeocodage(HttpClient httpClient) {
        super(httpClient);
    }

    /**
     * Interroge l'API de géocodage et retourne le premier résultat JSON.
     */
    public JsonObject rechercherAdresse(String libelleAdresse) {
        if (libelleAdresse == null || libelleAdresse.isBlank()) {
            throw new IllegalArgumentException("Le libelle d'adresse doit etre renseigne");
        }

        return executerGetJson(
                URL,
                "autocomplete", "0",
                "index", "address",
                "limit", "1",
                "returntruegeometry", "false",
                "q", libelleAdresse
        );
    }
}