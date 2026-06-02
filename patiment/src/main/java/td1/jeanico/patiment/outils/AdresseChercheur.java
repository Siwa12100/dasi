package td1.jeanico.patiment.outils;

import td1.jeanico.patiment.mappers.MappeurGeocodage;
import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.webClients.ClientWebGeocodage;

public class AdresseChercheur {

    private final ClientWebGeocodage geocodageWebClient;

    /**
     * Constructeur par défaut.
     */
    public AdresseChercheur() {
        this(new ClientWebGeocodage());
    }

    /**
     * Constructeur injectable.
     * @param geocodageWebClient
     */
    public AdresseChercheur(ClientWebGeocodage geocodageWebClient) {
        this.geocodageWebClient = geocodageWebClient;
    }

    /**
     * Recherche et retourne une {@link Adresse} normalisée à partir d'un libellé
     * d'adresse en texte libre. Retourne {@code null} si aucun résultat n'est trouvé.
     * @param libelleAdresse
     * @return 
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
