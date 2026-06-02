package td1.jeanico.patiment.tests;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.modeles.consultations.Consultation;
import td1.jeanico.patiment.modeles.mediums.Cartomancien;
import td1.jeanico.patiment.modeles.mediums.Medium;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.outils.SupportPersistance;
import td1.jeanico.patiment.services.ConsultationService;

public class ConsultationServiceTest {

    private static int nbTests = 0;
    private static int nbSucces = 0;
    private static int sequenceMail = 1;

    private static ConsultationService consultationService;
    private static final ClientDao clientDao = new ClientDao();
    private static final EmployeDao employeDao = new EmployeDao();
    private static final MediumDao mediumDao = new MediumDao();
    private static final PersistanceTestHelper persistanceHelper = new PersistanceTestHelper();

    private ConsultationServiceTest() {
    }

    public static void lancerTestsConsultationService() {
        nbTests = 0;
        nbSucces = 0;
        sequenceMail = 1;
        consultationService = new ConsultationService();

        System.out.println("\n=== Lancement des tests console de ConsultationService ===");

        test_DemanderConsultation_ClientNull();
        test_DemanderConsultation_MediumNull();
        test_DemanderConsultation_Valide();
        test_ConsulterHistorique_ClientNull();
        test_ConsulterHistorique_ClientSansConsultation();
        test_ConsulterHistorique_ClientAvecConsultation();
        test_DeclarerPret_ConsultationNull();
        test_DeclarerPret_ConsultationTerminee();
        test_DeclarerPret_Valide();
        test_TerminerConsultation_ConsultationNull();
        test_TerminerConsultation_DejaTerminee();
        test_TerminerConsultation_Valide();
        test_RecupererConsultationParId_IdNull();
        test_RecupererConsultationParId_IdInexistant();
        test_RecupererConsultationParId_Valide();

        System.out.println("=== Bilan ConsultationService: " + nbSucces + "/" + nbTests + " tests valides ===\n");
    }

    // -------------------------------------------------------------------------
    // demanderConsultation
    // -------------------------------------------------------------------------

    public static void test_DemanderConsultation_ClientNull() {
        System.out.println("Test : Refuser une demande de consultation si le client est null");

        Medium medium = mediumPersistant(Genre.NON_SPECIFIE);
        boolean resultat = consultationService.demanderConsultation(null, medium);

        verifier("retourne false si client null", !resultat);
    }

    public static void test_DemanderConsultation_MediumNull() {
        System.out.println("Test : Refuser une demande de consultation si le medium est null");

        Client client = clientPersistant("consult");

        boolean resultat = consultationService.demanderConsultation(client, null);

        verifier("retourne false si medium null", !resultat);
    }

    public static void test_DemanderConsultation_Valide() {
        System.out.println("Test : Creer une consultation avec un client et un medium valides");

        Client client = clientPersistant("consult");
        Medium medium = mediumPersistant(Genre.NON_SPECIFIE);

        boolean resultat = consultationService.demanderConsultation(client, medium);
        List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
        Consultation creee = (historique == null || historique.isEmpty()) ? null : historique.get(0);
        Employe employeAffecte = creee == null ? null : persistanceHelper.lecture(() -> employeDao.trouverParId(creee.getEmploye().getId()));

        verifier("retourne true", resultat);
        verifier("consultation persistée", creee != null);
        verifier("consultation active a la creation", creee != null && !creee.isEstTermine());
        verifier("medium associe correct", creee != null && creee.getMedium() != null
                && medium.getId().equals(creee.getMedium().getId()));
        verifier("employe marque indisponible", employeAffecte != null && !employeAffecte.isEstDisponible());
    }

    // -------------------------------------------------------------------------
    // consulterHistoriqueConsultations
    // -------------------------------------------------------------------------

    public static void test_ConsulterHistorique_ClientNull() {
        System.out.println("Test : Retourner une liste vide si le client est null");

        List<Consultation> resultat = consultationService.consulterHistoriqueConsultations(null);

        verifier("liste retournee non nulle", resultat != null);
        verifier("liste vide", resultat != null && resultat.isEmpty());
    }

    public static void test_ConsulterHistorique_ClientSansConsultation() {
        System.out.println("Test : Retourner une liste vide pour un client sans consultation");

        Client client = clientPersistant("histo");

        List<Consultation> resultat = consultationService.consulterHistoriqueConsultations(client);

        verifier("liste retournee non nulle", resultat != null);
        verifier("liste vide pour nouveau client", resultat != null && resultat.isEmpty());
    }

    public static void test_ConsulterHistorique_ClientAvecConsultation() {
        System.out.println("Test : Retourner les consultations d'un client ayant une demande");

        Client client = clientPersistant("histo");
        Medium medium = mediumPersistant(Genre.NON_SPECIFIE);
        boolean demande = consultationService.demanderConsultation(client, medium);

        List<Consultation> resultat = consultationService.consulterHistoriqueConsultations(client);

        verifier("demande prealable reussie", demande);
        verifier("historique non nul", resultat != null);
        verifier("historique contient au moins une consultation", resultat != null && !resultat.isEmpty());
    }

    // -------------------------------------------------------------------------
    // declarerPret
    // -------------------------------------------------------------------------

    public static void test_DeclarerPret_ConsultationNull() {
        System.out.println("Test : Ne rien faire si la consultation est null");

        try {
            consultationService.declarerPret(null);
            verifier("aucune exception levee", true);
        } catch (Exception e) {
            verifier("aucune exception levee", false);
        }
    }

    public static void test_DeclarerPret_ConsultationTerminee() {
        System.out.println("Test : Ne rien faire si la consultation est deja terminee");

        Client client = clientPersistant("pret");
        Medium medium = mediumPersistant(Genre.NON_SPECIFIE);
        consultationService.demanderConsultation(client, medium);

        List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
        boolean prerequis = historique != null && !historique.isEmpty();

        if (prerequis) {
            Consultation consultation = historique.get(0);
            consultationService.terminerConsultation(consultation, "terminee");
            try {
                consultationService.declarerPret(consultation);
                verifier("aucune exception levee sur consultation terminee", true);
            } catch (Exception e) {
                verifier("aucune exception levee sur consultation terminee", false);
            }
        } else {
            verifier("prerequis : consultation existante trouvee", false);
        }
    }

    public static void test_DeclarerPret_Valide() {
        System.out.println("Test : Declarer pret sur une consultation active");

        Client client = clientPersistant("pret");
        Medium medium = mediumPersistant(Genre.NON_SPECIFIE);
        boolean demande = consultationService.demanderConsultation(client, medium);

        List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
        boolean prerequis = demande && historique != null && !historique.isEmpty();

        verifier("demande prealable reussie", demande);
        verifier("consultation trouvee dans historique", prerequis);

        if (prerequis) {
            Consultation consultation = historique.get(0);
            try {
                consultationService.declarerPret(consultation);
                verifier("aucune exception levee", true);
            } catch (Exception e) {
                verifier("aucune exception levee", false);
            }
        }
    }

    // -------------------------------------------------------------------------
    // terminerConsultation
    // -------------------------------------------------------------------------

    public static void test_TerminerConsultation_ConsultationNull() {
        System.out.println("Test : Ne rien faire si la consultation a terminer est null");

        try {
            consultationService.terminerConsultation(null, "commentaire");
            verifier("aucune exception levee", true);
        } catch (Exception e) {
            verifier("aucune exception levee", false);
        }
    }

    public static void test_TerminerConsultation_DejaTerminee() {
        System.out.println("Test : Ne rien faire si la consultation est deja terminee");

        Client client = clientPersistant("term");
        Medium medium = mediumPersistant(Genre.NON_SPECIFIE);
        consultationService.demanderConsultation(client, medium);

        List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
        boolean prerequis = historique != null && !historique.isEmpty();

        if (prerequis) {
            Consultation consultation = historique.get(0);
            consultationService.terminerConsultation(consultation, "premier appel");
            try {
                consultationService.terminerConsultation(consultation, "deuxieme appel");
                verifier("aucune exception levee sur double terminaison", true);
            } catch (Exception e) {
                verifier("aucune exception levee sur double terminaison", false);
            }
        } else {
            verifier("prerequis : consultation existante trouvee", false);
        }
    }

    public static void test_TerminerConsultation_Valide() {
        System.out.println("Test : Terminer une consultation active avec commentaire");

        Client client = clientPersistant("term");
        Medium medium = mediumPersistant(Genre.NON_SPECIFIE);
        boolean demande = consultationService.demanderConsultation(client, medium);

        List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
        boolean prerequis = demande && historique != null && !historique.isEmpty();

        verifier("demande prealable reussie", demande);
        verifier("consultation trouvee dans historique", prerequis);

        if (prerequis) {
            Consultation consultation = historique.get(0);
            Employe employeAvant = persistanceHelper.lecture(() -> employeDao.trouverParId(consultation.getEmploye().getId()));
            consultationService.terminerConsultation(consultation, "tres bonne seance");

            Consultation recuperee = consultationService.recupererConsultationParId(consultation.getId());
            Employe employeApres = recuperee == null
                    ? null
                    : persistanceHelper.lecture(() -> employeDao.trouverParId(recuperee.getEmploye().getId()));

            verifier("employe etait indisponible pendant la consultation", employeAvant != null && !employeAvant.isEstDisponible());
            verifier("consultation marquee comme terminee", recuperee != null && recuperee.isEstTermine());
            verifier("commentaire enregistre", recuperee != null && "tres bonne seance".equals(recuperee.getCommentaire()));
            verifier("employe libere apres terminaison", employeApres != null && employeApres.isEstDisponible());
        }
    }

    // -------------------------------------------------------------------------
    // recupererConsultationParId
    // -------------------------------------------------------------------------

    public static void test_RecupererConsultationParId_IdNull() {
        System.out.println("Test : Retourner null si l'id est null");

        Consultation resultat = consultationService.recupererConsultationParId(null);

        verifier("retourne null", resultat == null);
    }

    public static void test_RecupererConsultationParId_IdInexistant() {
        System.out.println("Test : Retourner null si l'id est inexistant");

        Consultation resultat = consultationService.recupererConsultationParId(-999L);

        verifier("retourne null pour id inexistant", resultat == null);
    }

    public static void test_RecupererConsultationParId_Valide() {
        System.out.println("Test : Recuperer une consultation par son id");

        Client client = clientPersistant("recup");
        Medium medium = mediumPersistant(Genre.NON_SPECIFIE);
        boolean demande = consultationService.demanderConsultation(client, medium);

        List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
        boolean prerequis = demande && historique != null && !historique.isEmpty();

        verifier("demande prealable reussie", demande);
        verifier("consultation trouvee dans historique", prerequis);

        if (prerequis) {
            Consultation consultation = historique.get(0);
            Consultation recuperee = consultationService.recupererConsultationParId(consultation.getId());

            verifier("consultation recuperee non nulle", recuperee != null);
            verifier("id recupere correct", recuperee != null && consultation.getId().equals(recuperee.getId()));
        }
    }

    // -------------------------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------------------------

    private static String mailUnique(String prefixe) {
        return prefixe + "." + System.currentTimeMillis() + "." + (sequenceMail++) + "@test.fr";
    }

    private static Client clientValide(String mail) {
        return new Client(
                "Nom" + sequenceMail,
                "Prenom" + sequenceMail,
                mail,
                "motdepasse",
                "060000000" + (sequenceMail % 10),
                Genre.NON_SPECIFIE,
                new Adresse("1", "Rue de test", "69001", "69", "Lyon"),
                LocalDate.of(1995, 1, 1)
        );
    }

    private static Client clientPersistant(String prefixe) {
        return persistanceHelper.transaction(() -> {
            Client client = clientValide(mailUnique(prefixe));
            clientDao.creer(client);
            return client;
        });
    }

    private static Medium mediumPersistant(Genre genre) {
        return persistanceHelper.transaction(() -> {
            Employe employe = new Employe(
                    mailUnique("employe"),
                    "Emp" + sequenceMail,
                    "Test" + sequenceMail,
                    "motdepasse",
                    "070000000" + (sequenceMail % 10),
                    genre,
                    true
            );
            employeDao.creer(employe);

            Medium medium = new Cartomancien(
                    "Medium " + System.currentTimeMillis() + " " + sequenceMail,
                    genre,
                    "Medium de test"
            );
            mediumDao.creer(medium);
            return medium;
        });
    }

    private static void verifier(String message, boolean condition) {
        nbTests++;
        if (condition) {
            nbSucces++;
            System.out.println("  🟢 [OK] " + message);
        } else {
            System.out.println("  🔴 [KO] " + message);
        }
    }

    private static final class PersistanceTestHelper extends SupportPersistance {

        private <T> T lecture(Supplier<T> action) {
            return executerLecture(action);
        }

        private <T> T transaction(Supplier<T> action) {
            return executerEnTransaction(action);
        }
    }
}
