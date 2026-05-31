package td1.jeanico.patiment.webClients;

import jakarta.json.JsonObject;
import java.net.http.HttpClient;
import java.time.LocalDate;


public class ClientWebAstroNet extends ClientWebAbstrait {

    public static final String URL = "https://servif.insa-lyon.fr/WebDataGenerator/Astro";
    public static final String CLE_API = "ASTRO-01-M0lGLURBU0ktQVNUUk8tQjAx";

    /**
     * Constructeur par défaut.
     */
    public ClientWebAstroNet() {
        super();
    }

    /**
     * Constructeur injectable.
     */
    public ClientWebAstroNet(HttpClient httpClient) {
        super(httpClient);
    }

    /**
     * Appelle AstroNet pour récupérer un profil astral.
     */
    public JsonObject recupererProfilAstral(String prenom, LocalDate dateNaissance) {
        return appelerApi(
                "profil",
                "prenom", prenom,
                "date-naissance", dateNaissance == null ? "" : dateNaissance.toString()
        );
    }

    /**
     * Appelle AstroNet pour récupérer les prédictions amour/santé/travail.
     */
    public JsonObject recupererPredictions(String couleur, String animal, int niveauAmour, int niveauSante, int niveauTravail) {
        return appelerApi(
                "predictions",
                "couleur", couleur,
                "animal", animal,
                "niveau-amour", Integer.toString(niveauAmour),
                "niveau-sante", Integer.toString(niveauSante),
                "niveau-travail", Integer.toString(niveauTravail)
        );
    }

    /**
     * Point d'appel générique AstroNet.
     */
    public JsonObject recupererAstroNetData(String service, String... parametresCleValeur) {
        if (service == null || service.isBlank()) {
            throw new IllegalArgumentException("Le service AstroNet doit etre renseigne");
        }
        return executerGetJson(URL, parametresCleValeur);
    }

    /**
     * Ajoute les paramètres obligatoires AstroNet (service + clé API).
     */
    private JsonObject appelerApi(String service, String... parametresCleValeur) {
        String[] parametresAvecServiceEtCle = new String[parametresCleValeur.length + 4];
        parametresAvecServiceEtCle[0] = "service";
        parametresAvecServiceEtCle[1] = service;
        parametresAvecServiceEtCle[2] = "key";
        parametresAvecServiceEtCle[3] = CLE_API;
        System.arraycopy(parametresCleValeur, 0, parametresAvecServiceEtCle, 4, parametresCleValeur.length);
        return recupererAstroNetData(service, parametresAvecServiceEtCle);
    }
}