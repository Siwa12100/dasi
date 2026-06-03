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
import td1.jeanico.patiment.metier.modeles.consultations.Consultation;

/**
 * Scénarios de tests pour les consultations
 * @author ncolomb
 */
public class ConsultationScenario {

    private static ClientService clientService;
    private static ConsultationService consultationService;
    private static MediumService mediumService;
    

    public static void lancer() {
        System.out.println("\n========== SCÉNARIOS DE CONSULTATION ==========");

        clientService = new ClientService();
        consultationService = new ConsultationService();
        mediumService = new MediumService();
        

        scenarioDemandeConsultationValideEtRedemande();
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
        // Si le client existe déjà en base (par email), le récupérer
        Client probe = new Client();
        probe.setMail("isabelle.martin@email.com");
        Client existant = clientService.consulterProfilClient(probe);
        if (existant != null) {
            return existant;
        }

        // Adresse utilisée dans d'autres scénarios (connue pour être valide)
        Adresse adresse = new Adresse("20", "Rue de la Paix", "75002", "75", "Paris");
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
        // Recharger le client persisté pour obtenir l'ID et autres champs
        Client persisted = clientService.consulterProfilClient(client);
        return persisted == null ? client : persisted;
    }

    /**
     * Test : demande de consultation valide et redemande
     */
    public static void scenarioDemandeConsultationValideEtRedemande() {
        System.out.println("\n=> Scénario : Demande de consultation valide et redemande");

        Client client = preparerClient();
        if (client == null) {
            System.out.println("   ❌ ECHEC : Impossible de préparer les données");
            return;
        }

        // Récupérer un medium disponible via le service (gère le contexte JPA)
        List<Medium> mediums = mediumService.listerMediums();
        if (mediums == null || mediums.isEmpty()) {
            System.out.println("   ⚠️  Aucun medium disponible pour le test");
            return;
        }

        Medium medium = mediums.get(0);
        System.out.println("   - Client : " + client.toString());
        System.out.println("   - Medium choisi : " + medium.toString());

        Consultation resultat = consultationService.demanderConsultation(client, medium);
        
        if (resultat == null) {
            System.out.println("   ❌ ECHEC : La demande de consultation a échoué");
            return;
        }
        
        if (resultat.getEmploye().isEstDisponible()) {
            System.out.println("   ❌ ECHEC : L'employé est toujours disponible !");
            return;
        }
        
        if (resultat.getEmploye().getGenre() != medium.getGenre()) {
            System.out.println("   ❌ ECHEC : Le genre de l'employé != genre du medium !");
            return;
        }
        
        System.out.println("   ✅ SUCCES : Consultation demandée avec succès");
        System.out.println("   Un message de succès doit apparaitre dans les logs plus haut.");
        
        resultat = consultationService.demanderConsultation(client, medium);
        
        if (resultat == null) {
            System.out.println("   ✅ SUCCES : Consultation (re)demandée a échoué");
        } else {
            System.out.println("   ❌ ECHEC : La 2ème demande de consultation a fonctionné");
        }
    }
    
    /**
     * Test : demande de consultation valide mais pas de dispo
     */
    public static void scenarioDemandeConsultationValidePasDispo() {
        System.out.println("\n=> Scénario : Demande de consultation valide mais pas de dispo");
        
        // TODO
        
    }

    /**
     * Test : demande de consultation avec client null
     */
    public static void scenarioDemandeConsultationClientNull() {
        System.out.println("\n=> Scénario : Demande de consultation avec client null");

        List<Medium> mediums = mediumService.listerMediums();
        if (mediums == null || mediums.isEmpty()) {
            System.out.println("   ⚠️  Aucun medium disponible pour le test");
            return;
        }

        Medium medium = mediums.get(0);
        Consultation resultat = consultationService.demanderConsultation(null, medium);
        
        if (resultat == null) {
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

        Consultation resultat = consultationService.demanderConsultation(client, null);
        
        if (resultat == null) {
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

        Consultation resultat = consultationService.demanderConsultation(null, null);
        
        if (resultat == null) {
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
        // Tenter de lister l'historique d'un client de test
        Client client = preparerClient();
        if (client == null) {
            System.out.println("   ⚠️  Impossible de préparer un client pour l'affichage des consultations");
            return;
        }

        var consultations = consultationService.consulterHistoriqueConsultations(client);
        if (consultations == null || consultations.isEmpty()) {
            System.out.println("   ⚠️  Aucune consultation trouvée pour le client " + client.getPrenom() + " " + client.getNom());
            return;
        }

        System.out.println("   ✅ " + consultations.size() + " consultation(s) trouvée(s) pour " + client.getPrenom() + " " + client.getNom());
        for (Consultation c : consultations) {
            System.out.println("      - " + c.getDate() + " | Medium=" + c.getMedium().getDenomination() + " | Employe=" + (c.getEmploye() == null ? "-" : c.getEmploye().getPrenom() + " " + c.getEmploye().getNom()));
        }
    }
}
