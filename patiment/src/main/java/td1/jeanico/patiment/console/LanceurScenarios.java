package td1.jeanico.patiment.console;

import td1.jeanico.patiment.scenarios.InscriptionScenario;
import td1.jeanico.patiment.scenarios.ConnexionScenario;
import td1.jeanico.patiment.scenarios.ConsultationScenario;
import td1.jeanico.patiment.scenarios.StatistiqueScenario;

/**
 * Classe principale pour lancer tous les scénarios de test
 * @author ncolomb
 */
public class LanceurScenarios {
    
    public static void main(String[] args) {
        lancerScenarios();
    }
    
    /**
     * Lance tous les scénarios de test dans l'ordre
     */
    public static void lancerScenarios() {
        System.out.println("--------------------------------------");
        System.out.println("   EXECUTION DES SCÉNARIOS DE TESTS");
        System.out.println("--------------------------------------");
        
        try {
            // Lancer les scénarios dans l'ordre logique
            InscriptionScenario.lancer();
            ConnexionScenario.lancer();
            ConsultationScenario.lancer();
            StatistiqueScenario.lancer();
            
            System.out.println("--------------------------------");
            System.out.println("   FIN DES SCÉNARIOS DE TESTS");
            System.out.println("--------------------------------");
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
