package td1.jeanico.patiment.scenarios;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.metier.services.EmployeService;
import td1.jeanico.patiment.metier.services.MediumService;
import td1.jeanico.patiment.metier.services.StatistiqueService;

/**
 * Scénarios de tests pour les statistiques
 * @author ncolomb
 */
public class StatistiqueScenario {

    private static StatistiqueService statistiqueService;
    private static MediumService mediumService;
    private static EmployeService employeService;

    public static void lancer() {
        System.out.println("\n========== SCÉNARIOS DE STATISTIQUES ==========");

        statistiqueService = new StatistiqueService();
        mediumService = new MediumService();
        employeService = new EmployeService();

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
        
        if (repartition.get("01") != 48) {
            System.out.println("   ❌ ECHEC : Calcul répartition des clients dans le 01 est incorrecte (déterminé manuellement pour la SEED 42) !");
            return;
        } else {
            System.out.println("   ✅ SUCCES : Calcul répartition des clients dans le 01 est correcte (déterminé manuellement pour la SEED 42) !");
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
        
        Medium medMaitreSoleil = mediumService.recupererMediumParId(Long.valueOf(14)); // Id valid ONLY for SEED 42
        
        if (consultationsParMedium.get(medMaitreSoleil) != 10) {
            System.out.println("   ❌ ECHEC : Calcul nombre consultation pour medium Maitre Soleil est incorrecte (déterminé manuellement pour la SEED 42) !");
            return;
        } else {
            System.out.println("   ✅ SUCCES : Calcul nombre consultation pour medium Maitre Soleil est correcte (déterminé manuellement pour la SEED 42) !");
        }

        int totalConsultations = 0;
        for (Integer valeur : consultationsParMedium.values()) {
            totalConsultations += valeur == null ? 0 : valeur;
        }

        System.out.println("   ✅ Répartition des " + totalConsultations + " consultations par medium : ");
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
        
        Medium medMaitreSoleil = mediumService.recupererMediumParId(Long.valueOf(14)); // Id valid ONLY for SEED 42
        
        if (topMediums.get(0).get(medMaitreSoleil) == 10) {
            System.out.println("   ✅ SUCCES : Calcul top medium #1 est correcte (déterminé manuellement pour la SEED 42) !");
        } else {
            System.out.println("   ❌ ECHEC : Calcul top medium #1 est incorrecte (déterminé manuellement pour la SEED 42) !");
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
        
        Employe empEliseRouge = employeService.recupererEmployeParId(Long.valueOf(12)); // Id valid ONLY for SEED 42
        
        if (repartition.get(empEliseRouge) == 5) {
            System.out.println("   ✅ SUCCES : Calcul nb client distinct pour Elise ROUGE est correcte (déterminé manuellement pour la SEED 42) !");
        } else {
            System.out.println("   ❌ ECHEC : Calcul nb client distinct pour Elise ROUGE EST incorrecte (déterminé manuellement pour la SEED 42) !");
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
