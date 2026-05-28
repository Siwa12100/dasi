package td1.jeanico.patiment.webclient;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;


public class IfAstroNetWebClient {

    public static final String URL = "https://servif.insa-lyon.fr/WebDataGenerator/Astro";
    public static final String CLE_API = "ASTRO-01-M0lGLURBU0ktQVNUUk8tQjAx";

    private final HttpClient httpClient;

    public IfAstroNetWebClient() {
        this(HttpClient.newHttpClient());
    }

    public IfAstroNetWebClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public JsonObject recupererProfilAstral(String prenom, LocalDate dateNaissance) {
        return appelerApi(
                "profil",
                "prenom", prenom,
                "date-naissance", dateNaissance == null ? "" : dateNaissance.toString()
        );
    }

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

    public JsonObject recupererAstroNetData(String service, String... parametresCleValeur) {
        if (service == null || service.isBlank()) {
            throw new IllegalArgumentException("Le service AstroNet doit etre renseigne");
        }
        if (parametresCleValeur == null || parametresCleValeur.length % 2 != 0) {
            throw new IllegalArgumentException("Les parametres doivent etre fournis par paires cle/valeur");
        }

        URI requestUri = construireUri(parametresCleValeur);
        HttpRequest httpRequest = HttpRequest.newBuilder(requestUri).GET().build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                throw new IOException("HTTP Error Status Code " + httpResponse.statusCode());
            }

            try (JsonReader reader = Json.createReader(new StringReader(httpResponse.body()))) {
                return reader.readObject();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Impossible d'appeler le service AstroNet", ex);
        }
    }

    private JsonObject appelerApi(String service, String... parametresCleValeur) {
        String[] parametresAvecServiceEtCle = new String[parametresCleValeur.length + 4];
        parametresAvecServiceEtCle[0] = "service";
        parametresAvecServiceEtCle[1] = service;
        parametresAvecServiceEtCle[2] = "key";
        parametresAvecServiceEtCle[3] = CLE_API;
        System.arraycopy(parametresCleValeur, 0, parametresAvecServiceEtCle, 4, parametresCleValeur.length);
        return recupererAstroNetData(service, parametresAvecServiceEtCle);
    }

    private URI construireUri(String... parametresCleValeur) {
        StringBuilder url = new StringBuilder(URL).append('?');
        for (int index = 0; index < parametresCleValeur.length; index += 2) {
            if (index > 0) {
                url.append('&');
            }
            String cle = parametresCleValeur[index];
            String valeur = parametresCleValeur[index + 1];
            url.append(encode(cle)).append('=').append(encode(valeur == null ? "" : valeur));
        }
        return URI.create(url.toString());
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}