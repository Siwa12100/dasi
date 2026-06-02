package td1.jeanico.patiment.scenarios;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.metier.services.ClientService;
import td1.jeanico.patiment.metier.services.ConsultationService;
import td1.jeanico.patiment.metier.services.StatistiqueService;
import td1.jeanico.patiment.daos.MediumDao;

/**
 * Scénarios de tests pour les statistiques
 * @author ncolomb
 */
public class StatistiqueScenario {

    private static StatistiqueService statistiqueService;
    private static ClientService clientService;
    private static ConsultationService consultationService;
    private static MediumDao mediumDao;

    public static void lancer() {
        System.out.println("\n========== SCÉNARIOS DE STATISTIQUES ==========");

        statistiqueService = new StatistiqueService();
        clientService = new ClientService();
        consultationService = new ConsultationService();
        mediumDao = new MediumDao();

        scenarioNombreClients();
        scenarioNombreConsultations();
        scenarioMediumPopulaire();
        scenarioStatistiquesConsultants();
        scenarioPredictionsMoyennes();

        System.out.println("========== FIN SCÉNARIOS DE STATISTIQUES ==========\n");
    }

    /**
     * Test : affichage du nombre total de clients
     */
    public static void scenarioNombreClients() {
        System.out.println("\n=> Scénario : Nombre total de clients");

        
    }

    /**
     * Test : affichage du nombre total de consultations
     */
    public static void scenarioNombreConsultations() {
        System.out.println("\n=> Scénario : Nombre total de consultations");

        
    }

    /**
     * Test : medium le plus demandé
     */
    public static void scenarioMediumPopulaire() {
        System.out.println("\n=> Scénario : Medium le plus populaire (le plus consulté)");

        
    }

    /**
     * Test : statistiques par consultant (employé)
     */
    public static void scenarioStatistiquesConsultants() {
        System.out.println("\n=> Scénario : Statistiques par consultant (employé)");

        
    }

    /**
     * Test : statistiques sur les prédictions
     */
    public static void scenarioPredictionsMoyennes() {
        System.out.println("\n=> Scénario : Statistiques sur les prédictions");

        
    }
}
