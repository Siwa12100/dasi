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

        try {
            List<Client> clients = statistiqueService.obtenirListeClients();
            if (clients != null) {
                System.out.println("   ✅ Nombre de clients : " + clients.size());
                if (clients.size() <= 5) {
                    for (Client client : clients) {
                        System.out.println("      - " + client.getPrenom() + " " + client.getNom() 
                            + " (" + client.getMail() + ")");
                    }
                }
            } else {
                System.out.println("   ⚠️  Impossible de récupérer la liste des clients");
            }
        } catch (Exception e) {
            System.out.println("   ❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test : affichage du nombre total de consultations
     */
    public static void scenarioNombreConsultations() {
        System.out.println("\n=> Scénario : Nombre total de consultations");

        try {
            List<?> consultations = statistiqueService.obtenirStatistiquesConsultations();
            if (consultations != null) {
                System.out.println("   ✅ Nombre de consultations : " + consultations.size());
            } else {
                System.out.println("   ⚠️  Aucune consultation enregistrée");
            }
        } catch (Exception e) {
            System.out.println("   ❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test : medium le plus demandé
     */
    public static void scenarioMediumPopulaire() {
        System.out.println("\n=> Scénario : Medium le plus populaire (le plus consulté)");

        try {
            Map<?, ?> statsConsultants = statistiqueService.obtenirStatistiquesParMedium();
            if (statsConsultants != null && !statsConsultants.isEmpty()) {
                System.out.println("   ✅ Statistiques par medium disponibles");
                System.out.println("      Nombre de types de mediums : " + statsConsultants.size());
                
                // Afficher les statistiques
                for (Map.Entry<?, ?> entry : statsConsultants.entrySet()) {
                    System.out.println("      - " + entry.getKey() + " : " + entry.getValue());
                }
            } else {
                System.out.println("   ⚠️  Aucune statistique de medium disponible");
            }
        } catch (Exception e) {
            System.out.println("   ❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test : statistiques par consultant (employé)
     */
    public static void scenarioStatistiquesConsultants() {
        System.out.println("\n=> Scénario : Statistiques par consultant (employé)");

        try {
            List<Employe> employes = statistiqueService.obtenirListeEmployes();
            if (employes != null) {
                System.out.println("   ✅ Nombre de consultants : " + employes.size());
                if (employes.size() <= 5) {
                    for (Employe employe : employes) {
                        System.out.println("      - " + employe.getPrenom() + " " + employe.getNom()
                            + " (ID: " + employe.getId() + ")");
                    }
                }
            } else {
                System.out.println("   ⚠️  Aucun consultant trouvé");
            }
        } catch (Exception e) {
            System.out.println("   ❌ ERREUR : " + e.getMessage());
        }
    }

    /**
     * Test : statistiques sur les prédictions
     */
    public static void scenarioPredictionsMoyennes() {
        System.out.println("\n=> Scénario : Statistiques sur les prédictions");

        try {
            Map<?, ?> statsPredictions = statistiqueService.obtenirStatistiquesParClient();
            if (statsPredictions != null) {
                System.out.println("   ✅ Statistiques de prédictions disponibles");
                System.out.println("      Nombre d'entrées : " + statsPredictions.size());
                
                if (statsPredictions.size() <= 5) {
                    for (Map.Entry<?, ?> entry : statsPredictions.entrySet()) {
                        System.out.println("      - " + entry.getKey() + " : " + entry.getValue());
                    }
                }
            } else {
                System.out.println("   ⚠️  Aucune statistique de prédiction disponible");
            }
        } catch (Exception e) {
            System.out.println("   ❌ ERREUR : " + e.getMessage());
        }
    }
}
