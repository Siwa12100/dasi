package td1.jeanico.patiment;

import java.util.List;
import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.modeles.mediums.Astrologue;
import td1.jeanico.patiment.modeles.mediums.Cartomancien;
import td1.jeanico.patiment.modeles.mediums.Medium;
import td1.jeanico.patiment.modeles.mediums.Spirite;
import td1.jeanico.patiment.modeles.mediums.TypeMedium;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.services.MediumService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestIntegrationServiceMedium extends TestIntegrationAbstrait {

    private final MediumDao mediumDao = new MediumDao();

    // Fonctionnalite metier testee: consultation de la liste des mediums avec filtrage par type.
    // Comportement attendu: seuls les mediums du type demande sont retournes.
    @Test
    void listerMediumsParTypeFiltreCorrectementLesResultats() {
        creerJeuDeMediums();
        MediumService mediumService = new MediumService(mediumDao);

        List<Medium> mediums = mediumService.listerMediums(TypeMedium.Astrologue);

        assertEquals(1, mediums.size());
        assertTrue(mediums.get(0) instanceof Astrologue);
    }

    // Fonctionnalite metier testee: recuperation d'un medium par identifiant.
    // Comportement attendu: le medium persiste est retrouve a partir de son identifiant technique.
    @Test
    void recupererMediumParIdRetourneLeMediumPersistant() {
        Medium medium = executerEnTransaction(() -> {
            Medium mediumPersistant = new Spirite("Mme Irma", Genre.F, "Voyance spirite", "Boule");
            mediumDao.creer(mediumPersistant);
            return mediumPersistant;
        });
        MediumService mediumService = new MediumService(mediumDao);

        Medium mediumRetrouve = mediumService.recupererMediumParId(medium.getId());

        assertNotNull(mediumRetrouve);
        assertEquals("Mme Irma", mediumRetrouve.getDenomination());
    }

    private void creerJeuDeMediums() {
        executerEnTransaction(() -> {
            mediumDao.creer(new Spirite("Mme Irma", Genre.F, "Voyance spirite", "Boule"));
            mediumDao.creer(new Cartomancien("Maitre Soleil", Genre.H, "Cartes et tirages"));
            mediumDao.creer(new Astrologue("Cassandre Vega", Genre.F, "Theme astral", "ENS Astro", "2015"));
            return null;
        });
    }
}