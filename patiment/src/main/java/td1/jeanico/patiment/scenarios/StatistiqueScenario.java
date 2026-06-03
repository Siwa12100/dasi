package td1.jeanico.patiment.scenarios;

import java.util.List;
import java.util.Map;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.metier.services.StatistiqueService;

/**
 * Scénarios de tests pour les statistiques
 * @author ncolomb
 */
public class StatistiqueScenario {

    private static StatistiqueService statistiqueService;

    public static void lancer() {
        System.out.println("\n========== SCÉNARIOS DE STATISTIQUES ==========");

        statistiqueService = new StatistiqueService();

        scenarioNombreClientsEtRepartitionGeographique();
        scenarioNombreConsultations();
        scenarioMediumPopulaire();
        scenarioStatistiquesConsultants();

        System.out.println("========== FIN SCÉNARIOS DE STATISTIQUES ==========\n");
    }

    /**
     * Test : affichage du nombre total de clients
     */
    public static void scenarioNombreClientsEtRepartitionGeographique() {
        System.out.println("\n=> Scénario : Nombre total de clients et repartition géographique");

        Map<String, Integer> repartition = statistiqueService.listerRepartitionGeographiqueClients();
        if (repartition == null) {
            System.out.println("   ❌ ECHEC : Impossible de récupérer la répartition des clients");
            return;
        }

        int totalClients = 0;
        for (Integer valeur : repartition.values()) {
            totalClients += valeur == null ? 0 : valeur;
        }

        System.out.println("   ✅ Nombre total de clients : " + totalClients);
        System.out.println("   - Répartition par département :");
        for (Map.Entry<String, Integer> entry : repartition.entrySet()) {
            System.out.println("      * " + entry.getKey() + " : " + entry.getValue());
        }
    }

    /**
     * Test : affichage du nombre total de consultations
     */
    public static void scenarioNombreConsultations() {
        System.out.println("\n=> Scénario : Nombre total de consultations");

        Map<Medium, Integer> consultationsParMedium = statistiqueService.listerNombreConsultationsParMedium();
        if (consultationsParMedium == null) {
            System.out.println("   ❌ ECHEC : Impossible de récupérer les statistiques de consultation");
            return;
        }

        int totalConsultations = 0;
        for (Integer valeur : consultationsParMedium.values()) {
            totalConsultations += valeur == null ? 0 : valeur;
        }

        System.out.println("   ✅ Nombre total de consultations : " + totalConsultations);
        for (Map.Entry<Medium, Integer> entry : consultationsParMedium.entrySet()) {
            System.out.println("      - " + entry.getKey().getDenomination() + " : " + entry.getValue());
        }
    }

    /**
     * Test : medium le plus demandé
     */
    public static void scenarioMediumPopulaire() {
        System.out.println("\n=> Scénario : Medium le plus populaire (le plus consulté)");

        int topCount = 3;
        List<Map<Medium, Integer>> topMediums = statistiqueService.listerMediumsPopulaire(topCount);

        if (topMediums == null) {
            System.out.println("   ❌ ECHEC : Impossible de récupérer le top des médiums");
            return;
        }

        if (topMediums.isEmpty()) {
            System.out.println("   ⚠️  Aucun médium trouvé pour établir un classement");
            return;
        }

        System.out.println("   ✅ Top " + topMediums.size() + " des médiums les plus consultés :");
        int rang = 1;
        for (Map<Medium, Integer> ligne : topMediums) {
            for (Map.Entry<Medium, Integer> entry : ligne.entrySet()) {
                System.out.println("      " + rang + ". " + entry.getKey().getDenomination() + " : " + entry.getValue());
                rang++;
            }
        }
    }

    /**
     * Test : statistiques par consultant (employé)
     */
    public static void scenarioStatistiquesConsultants() {
        System.out.println("\n=> Scénario : Statistiques par consultant (employé)");

        Map<Employe, Integer> repartition = statistiqueService.listerRepartitionClientParEmploye();
        if (repartition == null) {
            System.out.println("   ❌ ECHEC : Impossible de récupérer les statistiques par employé");
            return;
        }

        System.out.println("   ✅ Répartition des clients distincts par employé :");
        for (Map.Entry<Employe, Integer> entry : repartition.entrySet()) {
            Employe employe = entry.getKey();
            String identite = employe == null
                    ? "INCONNU"
                    : employe.getPrenom() + " " + employe.getNom();
            System.out.println("      - " + identite + " : " + entry.getValue());
        }
    }
}
