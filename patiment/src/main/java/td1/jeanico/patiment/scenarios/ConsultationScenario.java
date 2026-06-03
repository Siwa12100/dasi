package td1.jeanico.patiment.scenarios;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.consultations.Consultation;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.mediums.TypeMedium;
import td1.jeanico.patiment.metier.modeles.mediums.Spirite;
import td1.jeanico.patiment.metier.modeles.mediums.Cartomancien;
import td1.jeanico.patiment.metier.modeles.mediums.Astrologue;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.metier.services.ClientService;
import td1.jeanico.patiment.metier.services.ConsultationService;
import td1.jeanico.patiment.metier.services.EmployeService;
import td1.jeanico.patiment.metier.services.MediumService;

/**
 * Scénarios de tests pour les consultations
 * @author ncolomb
 */
public class ConsultationScenario {

    private static ClientService clientService;
    private static ConsultationService consultationService;
    private static EmployeService employeService;
    private static MediumService mediumService;
    

    public static void lancer() {
        System.out.println("\n========== SCÉNARIOS DE CONSULTATION ==========");

        clientService = new ClientService();
        consultationService = new ConsultationService();
        employeService = new EmployeService();
        mediumService = new MediumService();
        

        scenarioDemandeConsultationEtHistorique();
        scenarioDemandeConsultationValidePasDispo();
        scenarioFiltrageMediumsParType();
        scenarioDemandeConsultationClientNull();
        scenarioDemandeConsultationMediumNull();
        scenarioDemandeConsultationClientEtMediumNull();
        scenarioListeConsultations();

        System.out.println("========== FIN SCÉNARIOS DE CONSULTATION ==========\n");
    }

    /**
     * Préparation : crée un client et un medium pour les tests
     */
    private static Client preparerNouveauClient() {
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
     * Test : demande de consultation valide et redemande, en vérifiant avec l'historique
     */
    public static void scenarioDemandeConsultationEtHistorique() {
        System.out.println("\n=> Scénario : Demande de consultation valide et redemande");

        Client client = preparerNouveauClient();
        if (client == null) {
            System.out.println("   ❌ ECHEC : Impossible de préparer les données");
            return;
        }

        // Récupérer un medium disponible via le service
        List<Medium> mediums = mediumService.listerMediums();
        if (mediums == null || mediums.isEmpty()) {
            System.out.println("   ⚠️  Aucun medium disponible pour le test");
            return;
        }

        Medium medium = mediums.get(0);
        System.out.println("   - Client : " + client.toString());
        System.out.println("   - Medium choisi : " + medium.toString());
        
        // Tester qu'il n'a bien pas d'historique
        var consultations = consultationService.consulterHistoriqueConsultations(client);
        if (!consultations.isEmpty()) {
            System.out.println("   ❌ ECHEC : Le nouveau client ne devrait pas avoir de consultation");
            return;
        } else {
            System.out.println("   ✅ SUCCES : Le nouveau client n'a aucune consultation");
        }

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
        
        Consultation resultat2 = consultationService.demanderConsultation(client, medium);
        
        if (resultat2 == null) {
            System.out.println("   ✅ SUCCES : Consultation (re)demandée a échoué");
        } else {
            System.out.println("   ❌ ECHEC : La 2ème demande de consultation a fonctionné");
        }
        
        // Tester que le client a bien 1 consultation et qu'elle est correcte
        consultations = consultationService.consulterHistoriqueConsultations(client);
        if (consultations.size() != 1 || !Objects.equals(consultations.get(0).getId(), resultat.getId())) {
            System.out.println("   ❌ ECHEC : Le nouveau client devrait avoir 1 consultation avec le même id qui celui précédement créé");
        } else {
            System.out.println("   ✅ SUCCES : Le nouveau client a bien 1 consultation correcte");
        }
        
        
        // Tester que l'employé a bien 1 consultation et qu'elle est correcte
        Consultation consultation = consultationService.consulterConsultationAffectee(resultat.getEmploye());
        if (consultation == null || !Objects.equals(consultation.getId(), resultat.getId())) {
            System.out.println("   ❌ ECHEC : L'employé devrait avoir 1 consultation avec le même id qui celui précédement créé");
        } else {
            System.out.println("   ✅ SUCCES : L'employé a bien 1 consultation correcte");
        }
        
        // Tester que l'employé est bien indisponible
        if (resultat.getEmploye().isEstDisponible() == true) {
            System.out.println("   ❌ ECHEC : L'employé devrait être indisponible");
        } else {
            System.out.println("   ✅ SUCCES : L'employé est bien indisponible");
        }
    }
    
    /**
     * Test : demande de consultation valide mais pas de dispo
     */
    public static void scenarioDemandeConsultationValidePasDispo() {
        System.out.println("\n=> Scénario : Demande de consultation valide mais pas de dispo");

        Client client = preparerNouveauClient();
        if (client == null) {
            System.out.println("   ❌ ECHEC : Impossible de préparer les données");
            return;
        }

        Medium medium = trouverMediumAvecLePlusDEmployesDisponibles();
        if (medium == null) {
            System.out.println("   ⚠️  Aucun medium ne permet de faire plusieurs consultations");
            return;
        }

        int disponibles = compterEmployesCompatiblesDisponibles(medium);
        if (disponibles < 1) {
            System.out.println("   ⚠️  Pas assez d'employés compatibles disponibles pour tester le cas d'épuisement");
            System.out.println("   ℹ️  Employés compatibles disponibles : " + disponibles);
            return;
        }

        System.out.println("   - Medium choisi : " + medium.getDenomination() + " (genre=" + medium.getGenre() + ")");
        System.out.println("   - Employés compatibles disponibles au départ : " + disponibles);

        List<Consultation> consultations = new ArrayList<>();
        for (int i = 0; i < disponibles; i++) {
            Consultation consultation = consultationService.demanderConsultation(client, medium);
            if (consultation == null) {
                System.out.println("   ❌ ECHEC : Une consultation intermédiaire a échoué avant l'épuisement complet");
                return;
            }
            consultations.add(consultation);
            System.out.println("   ✅ Consultation " + (i + 1) + " créée avec l'employé "
                    + consultation.getEmploye().getPrenom() + " " + consultation.getEmploye().getNom());
        }

        Consultation consultationSupplementaire = consultationService.demanderConsultation(client, medium);
        if (consultationSupplementaire == null) {
            System.out.println("   ✅ SUCCES : La demande supplémentaire a bien échoué car aucun employé compatible n'est disponible");
        } else {
            System.out.println("   ❌ ECHEC : Une consultation a encore été créée alors que les employés compatibles devaient être épuisés");
        }

        System.out.println("   - Total de consultations créées pendant le scénario : " + consultations.size());
    }

    /**
     * Test : filtrer les mediums par type et vérifier que le medium sélectionné réapparaît
     */
    public static void scenarioFiltrageMediumsParType() {
        System.out.println("\n=> Scénario : Filtrage des mediums par type");
        
        List<Medium> tousLesMedias = mediumService.listerMediums();
        if (tousLesMedias == null || tousLesMedias.isEmpty()) {
            System.out.println("   ⚠️  Aucun medium disponible pour le test");
            return;
        }
        System.out.println("   - Nombre total de mediums : " + tousLesMedias.size());

        Medium mediumSelectionne = tousLesMedias.get(0);
        System.out.println("   - Medium sélectionné : " + mediumSelectionne.getDenomination() + " (" + mediumSelectionne.getClass().getSimpleName() + ")");

        TypeMedium typeMediumSelectionne = determinerTypeMedium(mediumSelectionne);
        if (typeMediumSelectionne == null) {
            System.out.println("   ❌ ECHEC : Impossible de déterminer le type du medium");
            return;
        }
        System.out.println("   - Type détecté : " + typeMediumSelectionne);

        List<Medium> mediumsFiltres = mediumService.listerMediums(typeMediumSelectionne);
        if (mediumsFiltres == null || mediumsFiltres.isEmpty()) {
            System.out.println("   ❌ ECHEC : Aucun medium trouvé après filtrage par type " + typeMediumSelectionne);
            return;
        }

        System.out.println("   - Nombre de mediums du type " + typeMediumSelectionne + " : " + mediumsFiltres.size());
        boolean mediumTrouveDansFiltre = false;
        for (Medium m : mediumsFiltres) {
            if (m != null && m.getId() != null && m.getId().equals(mediumSelectionne.getId())) {
                mediumTrouveDansFiltre = true;
                break;
            }
        }

        if (mediumTrouveDansFiltre) {
            System.out.println("   ✅ SUCCES : Le medium " + mediumSelectionne.getDenomination() + " réapparaît dans la liste filtrée");
        } else {
            System.out.println("   ❌ ECHEC : Le medium " + mediumSelectionne.getDenomination() + " n'a pas réapparu dans la liste filtrée !");
        }
        
        typeMediumSelectionne = TypeMedium.Astrologue;
        mediumsFiltres = mediumService.listerMediums(typeMediumSelectionne);
        if (mediumsFiltres == null || mediumsFiltres.size() != 7) {
            System.out.println("   ❌ ECHEC : Nombre de medium incorrecte pour le type " + typeMediumSelectionne);
            return;
        } else {
            System.out.println("   ✅ SUCCES : Nombre de medium correcte pour le type " + typeMediumSelectionne);
        }
        
        typeMediumSelectionne = TypeMedium.Cartomancien;
        mediumsFiltres = mediumService.listerMediums(typeMediumSelectionne);
        if (mediumsFiltres == null || mediumsFiltres.size() != 7) {
            System.out.println("   ❌ ECHEC : Nombre de medium incorrecte pour le type " + typeMediumSelectionne);
            return;
        } else {
            System.out.println("   ✅ SUCCES : Nombre de medium correcte pour le type " + typeMediumSelectionne);
        }
        
        typeMediumSelectionne = TypeMedium.Spirite;
        mediumsFiltres = mediumService.listerMediums(typeMediumSelectionne);
        if (mediumsFiltres == null || mediumsFiltres.size() != 6) {
            System.out.println("   ❌ ECHEC : Nombre de medium incorrecte pour le type " + typeMediumSelectionne);
        } else {
            System.out.println("   ✅ SUCCES : Nombre de medium correcte pour le type " + typeMediumSelectionne);
        }
    }

    /**
     * Détermine le TypeMedium d'un medium en fonction de sa classe
     */
    private static TypeMedium determinerTypeMedium(Medium medium) {
        if (medium instanceof Spirite) {
            return TypeMedium.Spirite;
        } else if (medium instanceof Cartomancien) {
            return TypeMedium.Cartomancien;
        } else if (medium instanceof Astrologue) {
            return TypeMedium.Astrologue;
        }
        return null;
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

        Client client = preparerNouveauClient();
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
        Client client = preparerNouveauClient();
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

    private static Medium trouverMediumAvecLePlusDEmployesDisponibles() {
        List<Medium> mediums = mediumService.listerMediums();
        if (mediums == null || mediums.isEmpty()) {
            return null;
        }

        Medium meilleurMedium = null;
        int meilleurScore = -1;
        for (Medium medium : mediums) {
            int score = compterEmployesCompatiblesDisponibles(medium);
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurMedium = medium;
            }
        }
        return meilleurMedium;
    }

    private static int compterEmployesCompatiblesDisponibles(Medium medium) {
        if (medium == null || medium.getGenre() == null) {
            return 0;
        }
        List<Employe> employes = employeService.listerEmployes();
        if (employes == null || employes.isEmpty()) {
            return 0;
        }

        int compteur = 0;
        for (Employe employe : employes) {
            if (employe != null
                    && employe.isEstDisponible()
                    && employe.getGenre() == medium.getGenre()) {
                compteur++;
            }
        }
        return compteur;
    }
}
