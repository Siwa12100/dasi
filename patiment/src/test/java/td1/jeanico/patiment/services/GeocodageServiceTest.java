package td1.jeanico.patiment.services;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.webClients.ClientWebGeocodage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeocodageServiceTest {

    @Test
    void rechercherAdresseRetourneAdresseMappeeQuandLaReponseEstValide() {
        JsonObject reponse = Json.createObjectBuilder()
                .add("features", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("properties", Json.createObjectBuilder()
                                        .add("housenumber", "20")
                                        .add("street", "Rue de la Paix")
                                        .add("postcode", "75002")
                                        .add("depcode", "75")
                                        .add("city", "Paris"))))
                .build();

        GeocodageService service = new GeocodageService(new FakeClientWebGeocodage(reponse));

        Adresse adresse = service.rechercherAdresse("20 Rue de la Paix 75002 Paris");

        assertNotNull(adresse);
        assertEquals("20", adresse.getNumeroDeVoie());
        assertEquals("Rue de la Paix", adresse.getNomDeVoie());
        assertEquals("75002", adresse.getCodePostal());
        assertEquals("75", adresse.getCodeDepartement());
        assertEquals("Paris", adresse.getVille());
    }

    @Test
    void rechercherAdresseRetourneNullQuandLeLibelleEstVide() {
        FakeClientWebGeocodage client = new FakeClientWebGeocodage(Json.createObjectBuilder().build());
        GeocodageService service = new GeocodageService(client);

        Adresse adresse = service.rechercherAdresse("   ");

        assertNull(adresse);
        assertTrue(client.derniereAdresseRecherchee == null);
    }

    @Test
    void rechercherAdresseRetourneNullQuandLeClientLeveUneException() {
        GeocodageService service = new GeocodageService(new FakeClientWebGeocodage(new RuntimeException("boom")));

        Adresse adresse = service.rechercherAdresse("10 Downing Street");

        assertNull(adresse);
    }

    @Test
    void rechercherAdresseRetourneNullQuandAucunResultatExploitableNestPresent() {
        JsonObject reponseSansFeature = Json.createObjectBuilder()
                .add("features", Json.createArrayBuilder())
                .build();

        GeocodageService service = new GeocodageService(new FakeClientWebGeocodage(reponseSansFeature));

        Adresse adresse = service.rechercherAdresse("Adresse inconnue");

        assertNull(adresse);
    }

    private static final class FakeClientWebGeocodage extends ClientWebGeocodage {

        private final JsonObject reponse;
        private final RuntimeException exception;
        private String derniereAdresseRecherchee;

        private FakeClientWebGeocodage(JsonObject reponse) {
            this.reponse = reponse;
            this.exception = null;
        }

        private FakeClientWebGeocodage(RuntimeException exception) {
            this.reponse = null;
            this.exception = exception;
        }

        @Override
        public JsonObject rechercherAdresse(String libelleAdresse) {
            this.derniereAdresseRecherchee = libelleAdresse;
            if (exception != null) {
                throw exception;
            }
            return reponse;
        }
    }
}