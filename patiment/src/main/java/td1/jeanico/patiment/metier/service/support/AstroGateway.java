package td1.jeanico.patiment.metier.service.support;

import java.util.List;
import java.util.Objects;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.modele.Prediction;
import td1.jeanico.patiment.metier.modele.ProfilAstral;

public class AstroGateway {

    private static final String[] ANIMAUX_TOTAUX = {
        "Loup", "Aigle", "Dauphin", "Renard", "Cerf", "Hibou", "Panthere", "Tortue"
    };
    private static final String[] SIGNES_ZODIAC = {
        "Belier", "Taureau", "Gemeaux", "Cancer", "Lion", "Vierge",
        "Balance", "Scorpion", "Sagittaire", "Capricorne", "Verseau", "Poissons"
    };
    private static final String[] COULEURS_BONHEUR = {
        "Or", "Turquoise", "Emeraude", "Ivoire", "Corail", "Indigo", "Ambre", "Argent"
    };
    private static final String[] SIGNES_CHINOIS = {
        "Rat", "Boeuf", "Tigre", "Lapin", "Dragon", "Serpent",
        "Cheval", "Chevre", "Singe", "Coq", "Chien", "Cochon"
    };
    private static final String[] PREDICTIONS_AMOUR = {
        "Une rencontre stimulante se profile.",
        "Une relation existante gagne en clarte.",
        "Le calme affectif t'aide a mieux choisir.",
        "Une initiative sincere ouvrira une porte.",
        "L'equilibre sentimental revient progressivement."
    };
    private static final String[] PREDICTIONS_SANTE = {
        "Un rythme plus regulier sera benefique.",
        "Ton energie remonte avec un meilleur sommeil.",
        "Une pause bien placee fera la difference.",
        "Ton moral soutient une bonne dynamique physique.",
        "Une vigilance douce t'evitera de te disperser."
    };
    private static final String[] PREDICTIONS_TRAVAIL = {
        "Une opportunite serieuse merite d'etre saisie.",
        "La patience rendra tes efforts visibles.",
        "Un contact utile facilitera ta progression.",
        "La periode est favorable aux decisions concretes.",
        "Une reorganisation peut devenir tres positive."
    };

    public ProfilAstral construireProfilAstral(Client client) {
        int base = Math.abs(Objects.hash(
                safe(client.getMail()),
                safe(client.getPrenom()),
                safe(client.getNom()),
                client.getAdresse() != null ? safe(client.getAdresse().getCodePostal()) : ""
        ));
        return new ProfilAstral(
                pick(ANIMAUX_TOTAUX, base),
                pick(SIGNES_ZODIAC, base / 3),
                pick(COULEURS_BONHEUR, base / 5),
                pick(SIGNES_CHINOIS, base / 7)
        );
    }

    public List<Prediction> demanderInspiration(int scoreAmour, int scoreSante, int scoreTravail) {
        Prediction prediction = new Prediction(
                predictionPourScore(scoreAmour, PREDICTIONS_AMOUR),
                predictionPourScore(scoreSante, PREDICTIONS_SANTE),
                predictionPourScore(scoreTravail, PREDICTIONS_TRAVAIL)
        );
        return List.of(prediction);
    }

    private String predictionPourScore(int score, String[] corpus) {
        int index = Math.max(0, Math.min(corpus.length - 1, score));
        return corpus[index];
    }

    private String pick(String[] valeurs, int index) {
        return valeurs[Math.floorMod(index, valeurs.length)];
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
