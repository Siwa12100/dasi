package td1.jeanico.patiment.services;

import td1.jeanico.patiment.mappers.MappeurGeocodage;

import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.webClients.ClientWebGeocodage;

public class GeocodageService {

    private final ClientWebGeocodage geocodageWebClient;

    /**
     * Constructeur par défaut.
     */
    public GeocodageService() {
        this(new ClientWebGeocodage());
    }

    /**
     * Constructeur injectable.
     */
    public GeocodageService(ClientWebGeocodage geocodageWebClient) {
        this.geocodageWebClient = geocodageWebClient;
    }

    /**
     * Recherche et retourne une {@link Adresse} normalisée à partir d'un libellé
     * d'adresse en texte libre. Retourne {@code null} si aucun résultat n'est trouvé.
     */
    public Adresse rechercherAdresse(String libelleAdresse) {
        if (libelleAdresse == null || libelleAdresse.isBlank()) {
            return null;
        }
        try {
            // Le mapping retourne null si la réponse ne contient aucun feature exploitable.
            return MappeurGeocodage.versAdresse(geocodageWebClient.rechercherAdresse(libelleAdresse));
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
