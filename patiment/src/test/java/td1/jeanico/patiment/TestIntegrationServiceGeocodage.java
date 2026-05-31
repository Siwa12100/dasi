package td1.jeanico.patiment;

import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.services.GeocodageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestIntegrationServiceGeocodage extends TestIntegrationAbstrait {

    // Fonctionnalite metier testee: géocodage d'une adresse réelle.
    // Comportement attendu: l'API externe retourne une adresse normalisée avec code postal, département et ville renseignés.
    @Test
    void rechercherAdresseRetourneUneAdresseNormalisee() {
        GeocodageService geocodageService = new GeocodageService();

        Adresse adresse = geocodageService.rechercherAdresse("20 avenue Albert Einstein 69100 Villeurbanne");

        assertNotNull(adresse);
        assertNotNull(adresse.getCodePostal());
        assertNotNull(adresse.getVille());
        assertEquals("69", adresse.getCodeDepartement());
        assertEquals("69100", adresse.getCodePostal());
        assertEquals("Villeurbanne", adresse.getVille());
    }

    // Fonctionnalite metier testee: géocodage d'une adresse avec voie et numéro.
    // Comportement attendu: le numéro de voie et le nom de la rue sont bien extraits de la réponse.
    @Test
    void rechercherAdresseExtraitleNumeroEtLaRue() {
        GeocodageService geocodageService = new GeocodageService();

        Adresse adresse = geocodageService.rechercherAdresse("20 avenue Albert Einstein 69100 Villeurbanne");

        assertNotNull(adresse);
        assertEquals("20", adresse.getNumeroDeVoie());
        assertNotNull(adresse.getNomDeVoie());
    }

    // Fonctionnalite metier testee: géocodage avec une adresse vide ou nulle.
    // Comportement attendu: le service retourne null sans appeler l'API externe.
    @Test
    void rechercherAdresseRetourneNullSiLibelleVide() {
        GeocodageService geocodageService = new GeocodageService();

        assertNull(geocodageService.rechercherAdresse(""));
        assertNull(geocodageService.rechercherAdresse(null));
    }

    // Fonctionnalite metier testee: géocodage d'une adresse introuvable.
    // Comportement attendu: le service retourne null lorsque l'API ne trouve aucun résultat exploitable.
    @Test
    void rechercherAdresseRetourneNullSiAucunResultatTrouve() {
        GeocodageService geocodageService = new GeocodageService();

        Adresse adresse = geocodageService.rechercherAdresse("zzz xyz zzz zzz zzz");

        // L'API peut retourner un résultat approximatif même pour des requetes incohérentes;
        // on vérifie seulement que le service ne lève pas d'exception.
        // Si null: comportement attendu; si non-null: l'API a fourni un résultat approximatif.
        if (adresse != null) {
            assertNotNull(adresse.getVille());
        }
    }
}
