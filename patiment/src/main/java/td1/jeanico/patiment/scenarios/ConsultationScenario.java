package td1.jeanico.patiment.scenarios;

import java.time.LocalDate;
import java.util.List;
import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.consultations.Consultation;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.mediums.Astrologue;
import td1.jeanico.patiment.metier.modeles.mediums.TypeMedium;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.metier.services.ClientService;
import td1.jeanico.patiment.metier.services.ConsultationService;
import td1.jeanico.patiment.metier.services.MediumService;
import td1.jeanico.patiment.daos.MediumDao;

/**
 * Scénarios de tests pour les consultations
 * @author ncolomb
 */
public class ConsultationScenario {

    private static ClientService clientService;
    private static ConsultationService consultationService;
    private static MediumService mediumService;
    private static MediumDao mediumDao;

    public static void lancer() {
        System.out.println("\n========== SCÉNARIOS DE CONSULTATION ==========");

        clientService = new ClientService();
        consultationService = new ConsultationService();
        mediumService = new MediumService();
        mediumDao = new MediumDao();

        scenarioDemandeConsultationValide();
        scenarioDemandeConsultationClientNull();
        scenarioDemandeConsultationMediumNull();
        scenarioDemandeConsultationClientEtMediumNull();
        scenarioListeConsultations();

        System.out.println("========== FIN SCÉNARIOS DE CONSULTATION ==========\n");
    }

    /**
     * Préparation : crée un client et un medium pour les tests
     */
    private static Client preparerClient() {
        Adresse adresse = new Adresse("30", "Rue du Commerce", "75003", "75", "Paris");
        Client client = new Client(
            "Martin",
            "Isabelle",
            "isabelle.martin@email.com",
            "consulter123",
            "0699999999",
            Genre.FEMME,
            adresse,
            LocalDate.of(1992, 8, 10)
        );
        
        boolean inscriptionReussie = clientService.inscrire(client);
        if (!inscriptionReussie) {
            System.out.println("   ⚠️  Impossible de créer le client de test");
            return null;
        }
        return client;
    }

    /**
     * Test : demande de consultation valide
     */
    public static void scenarioDemandeConsultationValide() {
        System.out.println("\n=> Scénario : Demande de consultation valide");

        Client client = preparerClient();
        if (client == null) {
            System.out.println("   ❌ ECHEC : Impossible de préparer les données");
            return;
        }

        // Récupérer un medium disponible
        List<Medium> mediums = mediumDao.lireTous();
        if (mediums == null || mediums.isEmpty()) {
            System.out.println("   ⚠️  Aucun medium disponible pour le test");
            return;
        }

        Medium medium = mediums.get(0);
        System.out.println("   - Client : " + client.getPrenom() + " " + client.getNom());
        System.out.println("   - Medium choisi : " + medium.getNom());

        boolean resultat = consultationService.demanderConsultation(client, medium);
        
        if (resultat) {
            System.out.println("   ✅ SUCCES : Consultation demandée avec succès");
        } else {
            System.out.println("   ❌ ECHEC : La demande de consultation a échoué");
        }
    }

    /**
     * Test : demande de consultation avec client null
     */
    public static void scenarioDemandeConsultationClientNull() {
        System.out.println("\n=> Scénario : Demande de consultation avec client null");

        List<Medium> mediums = mediumDao.lireTous();
        if (mediums == null || mediums.isEmpty()) {
            System.out.println("   ⚠️  Aucun medium disponible pour le test");
            return;
        }

        Medium medium = mediums.get(0);
        boolean resultat = consultationService.demanderConsultation(null, medium);
        
        if (!resultat) {
            System.out.println("   ✅ SUCCES : La demande avec client null a correctement échoué");
        } else {
            System.out.println("   ❌ ECHEC : La demande avec client null devrait échouer");
        }
    }

    /**
     * Test : demande de consultation avec medium null
     */
    public static void scenarioDemandeConsultationMediumNull() {
        System.out.println("\n=> Scénario : Demande de consultation avec medium null");

        Client client = preparerClient();
        if (client == null) {
            System.out.println("   ❌ ECHEC : Impossible de préparer les données");
            return;
        }

        boolean resultat = consultationService.demanderConsultation(client, null);
        
        if (!resultat) {
            System.out.println("   ✅ SUCCES : La demande avec medium null a correctement échoué");
        } else {
            System.out.println("   ❌ ECHEC : La demande avec medium null devrait échouer");
        }
    }

    /**
     * Test : demande de consultation avec client et medium null
     */
    public static void scenarioDemandeConsultationClientEtMediumNull() {
        System.out.println("\n=> Scénario : Demande de consultation avec client et medium null");

        boolean resultat = consultationService.demanderConsultation(null, null);
        
        if (!resultat) {
            System.out.println("   ✅ SUCCES : La demande avec arguments null a correctement échoué");
        } else {
            System.out.println("   ❌ ECHEC : La demande avec arguments null devrait échouer");
        }
    }

    /**
     * Test : affichage de la liste des consultations
     */
    public static void scenarioListeConsultations() {
        System.out.println("\n=> Scénario : Affichage de la liste des consultations");

        List<Consultation> consultations = consultationService.listerConsultations();
        
        if (consultations == null) {
            System.out.println("   ⚠️  Aucune consultation trouvée");
        } else {
            System.out.println("   ✅ " + consultations.size() + " consultation(s) trouvée(s)");
            for (Consultation consultation : consultations) {
                System.out.println("      - Consultation : Client=" + consultation.getClient().getPrenom() 
                    + ", Medium=" + consultation.getMedium().getNom()
                    + ", Date=" + consultation.getDateHeureDebut());
            }
        }
    }
}
