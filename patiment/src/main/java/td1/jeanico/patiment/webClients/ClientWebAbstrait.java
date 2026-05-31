package td1.jeanico.patiment.webClients;

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

public abstract class ClientWebAbstrait {

    private final HttpClient httpClient;

    /**
     * Construit un client HTTP standard.
     */
    protected ClientWebAbstrait() {
        this(HttpClient.newHttpClient());
    }

    /**
     * Permet d'injecter un client HTTP personnalisé.
     */
    protected ClientWebAbstrait(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Exécute un GET et retourne le JSON de réponse.
     * Lève une RuntimeException si l'appel échoue ou si le code HTTP n'est pas 200.
     */
    protected JsonObject executerGetJson(String url, String... parametresCleValeur) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("L'URL du service web doit etre renseignee");
        }
        if (parametresCleValeur == null || parametresCleValeur.length % 2 != 0) {
            throw new IllegalArgumentException("Les parametres doivent etre fournis par paires cle/valeur");
        }

        URI requestUri = construireUri(url, parametresCleValeur);
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
            throw new RuntimeException("Impossible d'appeler le service web", ex);
        }
    }

    /**
     * Construit l'URI finale en encodant chaque paire clé/valeur.
     */
    private URI construireUri(String url, String... parametresCleValeur) {
        StringBuilder urlBuilder = new StringBuilder(url).append('?');
        for (int index = 0; index < parametresCleValeur.length; index += 2) {
            if (index > 0) {
                urlBuilder.append('&');
            }
            String cle = parametresCleValeur[index];
            String valeur = parametresCleValeur[index + 1];
            urlBuilder.append(encoder(cle)).append('=').append(encoder(valeur == null ? "" : valeur));
        }
        return URI.create(urlBuilder.toString());
    }

    /**
     * Encode une valeur pour être transmise en query-string.
     */
    private String encoder(String valeur) {
        return URLEncoder.encode(valeur, StandardCharsets.UTF_8);
    }
}