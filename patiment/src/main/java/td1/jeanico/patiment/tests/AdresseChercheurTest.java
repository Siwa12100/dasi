package td1.jeanico.patiment.tests;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.outils.AdresseChercheur;
import td1.jeanico.patiment.webClients.ClientWebGeocodage;

public class AdresseChercheurTest {

    private static int nbTests = 0;
    private static int nbSucces = 0;

    private AdresseChercheurTest() {
    }

    public static void lancerTestsAdresseChercheur() {
        nbTests = 0;
        nbSucces = 0;

        System.out.println("\n=== Lancement des tests console de AdresseChercheur ===");

        test_RechercherAdresse_LibelleNull();
        test_RechercherAdresse_LibelleVide();
        test_RechercherAdresse_ExceptionWebClient();
        test_RechercherAdresse_AucunResultat();
        test_RechercherAdresse_Valide();

        System.out.println("=== Bilan AdresseChercheur: " + nbSucces + "/" + nbTests + " tests valides ===\n");
    }

    public static void test_RechercherAdresse_LibelleNull() {
        System.out.println("Test : Retourner null si le libelle est null");

        ClientWebGeocodageStub webClient = new ClientWebGeocodageStub();
        AdresseChercheur chercheur = new AdresseChercheur(webClient);

        Adresse resultat = chercheur.rechercherAdresse(null);

        verifier("adresse retournee nulle", resultat == null);
        verifier("web client non appele", webClient.getNbAppels() == 0);
    }

    public static void test_RechercherAdresse_LibelleVide() {
        System.out.println("Test : Retourner null si le libelle est vide");

        ClientWebGeocodageStub webClient = new ClientWebGeocodageStub();
        AdresseChercheur chercheur = new AdresseChercheur(webClient);

        Adresse resultat = chercheur.rechercherAdresse("   ");

        verifier("adresse retournee nulle", resultat == null);
        verifier("web client non appele", webClient.getNbAppels() == 0);
    }

    public static void test_RechercherAdresse_ExceptionWebClient() {
        System.out.println("Test : Retourner null si le web client leve une exception");

        ClientWebGeocodageStub webClient = new ClientWebGeocodageStub();
        webClient.setException(new RuntimeException("erreur reseau"));
        AdresseChercheur chercheur = new AdresseChercheur(webClient);

        Adresse resultat = chercheur.rechercherAdresse("20 Rue de la Paix 75002 Paris");

        verifier("adresse retournee nulle", resultat == null);
        verifier("web client appele une fois", webClient.getNbAppels() == 1);
    }

    public static void test_RechercherAdresse_AucunResultat() {
        System.out.println("Test : Retourner null si la reponse ne contient aucun resultat");

        ClientWebGeocodageStub webClient = new ClientWebGeocodageStub();
        webClient.setReponseJson(Json.createObjectBuilder().add("features", Json.createArrayBuilder()).build());
        AdresseChercheur chercheur = new AdresseChercheur(webClient);

        Adresse resultat = chercheur.rechercherAdresse("adresse introuvable");

        verifier("adresse retournee nulle", resultat == null);
        verifier("web client appele une fois", webClient.getNbAppels() == 1);
    }

    public static void test_RechercherAdresse_Valide() {
        System.out.println("Test : Retourner une adresse normalisee sur une reponse valide");

        JsonArrayBuilder features = Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                        .add("properties", Json.createObjectBuilder()
                                .add("housenumber", "20")
                                .add("street", "Rue de la Paix")
                                .add("postcode", "75002")
                                .add("depcode", "75")
                                .add("city", "Paris")
                        )
                );

        JsonObject reponse = Json.createObjectBuilder()
                .add("features", features)
                .build();

        ClientWebGeocodageStub webClient = new ClientWebGeocodageStub();
        webClient.setReponseJson(reponse);
        AdresseChercheur chercheur = new AdresseChercheur(webClient);

        String libelle = "20 Rue de la Paix 75002 Paris";
        Adresse resultat = chercheur.rechercherAdresse(libelle);

        verifier("adresse retournee non nulle", resultat != null);
        verifier("numero de voie correct", resultat != null && "20".equals(resultat.getNumeroDeVoie()));
        verifier("nom de voie correct", resultat != null && "Rue de la Paix".equals(resultat.getNomDeVoie()));
        verifier("code postal correct", resultat != null && "75002".equals(resultat.getCodePostal()));
        verifier("departement correct", resultat != null && "75".equals(resultat.getCodeDepartement()));
        verifier("ville correcte", resultat != null && "Paris".equals(resultat.getVille()));
        verifier("libelle transmis au web client", libelle.equals(webClient.getDernierLibelle()));
    }

    private static void verifier(String message, boolean condition) {
        nbTests++;
        if (condition) {
            nbSucces++;
            System.out.println("  🟢 [OK] " + message);
        } else {
            System.out.println("  🔴 [KO] " + message);
        }
    }

    private static final class ClientWebGeocodageStub extends ClientWebGeocodage {

        private JsonObject reponseJson;
        private RuntimeException exception;
        private int nbAppels = 0;
        private String dernierLibelle;

        @Override
        public JsonObject rechercherAdresse(String libelleAdresse) {
            nbAppels++;
            dernierLibelle = libelleAdresse;
            if (exception != null) {
                throw exception;
            }
            return reponseJson;
        }

        private void setReponseJson(JsonObject reponseJson) {
            this.reponseJson = reponseJson;
        }

        private void setException(RuntimeException exception) {
            this.exception = exception;
        }

        private int getNbAppels() {
            return nbAppels;
        }

        private String getDernierLibelle() {
            return dernierLibelle;
        }
    }
}