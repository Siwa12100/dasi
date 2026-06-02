package td1.jeanico.patiment.tests;

import java.util.List;
import java.util.function.Supplier;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.modeles.consultations.Consultation;
import td1.jeanico.patiment.modeles.mediums.Medium;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.outils.SupportPersistance;
import td1.jeanico.patiment.services.ConsultationService;

public class ConsultationServiceTest {

    private static final String MAIL_CLIENT_CONSULT = "arthur@free.fr";
    private static final String MAIL_CLIENT_SANS_CONSULT = "robin@orange.fr";
    private static final String MAIL_CLIENT_HISTO = "maxime@sfr.fr";
    private static final String DENOMINATION_MEDIUM_INITIALISE = "Mlle Arcane";

    private static int nbTests = 0;
    private static int nbSucces = 0;
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

        Medium medium = mediumInitialise();
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (medium != null) {
            assurerEmployeCompatibleDisponible(medium);
            boolean resultat = consultationService.demanderConsultation(null, medium);

            verifier("retourne false si client null", !resultat);
        }
    }

    public static void test_DemanderConsultation_MediumNull() {
        System.out.println("Test : Refuser une demande de consultation si le medium est null");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        verifier("prerequis : client initialise retrouve", client != null);

        if (client != null) {
            boolean resultat = consultationService.demanderConsultation(client, null);

            verifier("retourne false si medium null", !resultat);
        }
    }

    public static void test_DemanderConsultation_Valide() {
        System.out.println("Test : Creer une consultation avec un client et un medium initialises en BDD");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
            boolean resultat = consultationService.demanderConsultation(client, medium);
            List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
            Consultation creee = (historique == null || historique.isEmpty()) ? null : historique.get(0);
            Employe employeAffecte = creee == null ? null : persistanceHelper.lecture(() -> employeDao.trouverParId(creee.getEmploye().getId()));

            verifier("retourne true", resultat);
            verifier("consultation persistee", creee != null);
            verifier("consultation active a la creation", creee != null && !creee.isEstTermine());
            verifier("medium associe correct", creee != null && creee.getMedium() != null
                    && medium.getId().equals(creee.getMedium().getId()));
            verifier("employe marque indisponible", employeAffecte != null && !employeAffecte.isEstDisponible());
        }
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
        System.out.println("Test : Retourner une liste vide pour un client initialise sans consultation");

        Client client = clientInitialiseSansConsultation();
        verifier("prerequis : client initialise retrouve", client != null);

        if (client != null) {
            List<Consultation> resultat = consultationService.consulterHistoriqueConsultations(client);

            verifier("liste retournee non nulle", resultat != null);
            verifier("liste vide pour client initialise sans consultation", resultat != null && resultat.isEmpty());
        }
    }

    public static void test_ConsulterHistorique_ClientAvecConsultation() {
        System.out.println("Test : Retourner les consultations d'un client initialise ayant une demande");

        Client client = clientInitialise(MAIL_CLIENT_HISTO);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
            boolean demande = consultationService.demanderConsultation(client, medium);

            List<Consultation> resultat = consultationService.consulterHistoriqueConsultations(client);

            verifier("demande prealable reussie", demande);
            verifier("historique non nul", resultat != null);
            verifier("historique contient au moins une consultation", resultat != null && !resultat.isEmpty());
        }
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

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
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
    }

    public static void test_DeclarerPret_Valide() {
        System.out.println("Test : Declarer pret sur une consultation active");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
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

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
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
    }

    public static void test_TerminerConsultation_Valide() {
        System.out.println("Test : Terminer une consultation active avec commentaire");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
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
        System.out.println("Test : Recuperer une consultation par son id (donnees initialisees)");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
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
    }

    // -------------------------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------------------------

    private static Client clientInitialise(String mail) {
        return persistanceHelper.lecture(() -> clientDao.trouverParMail(mail));
    }

    private static Client clientInitialiseSansConsultation() {
        List<String> mailsCandidats = List.of(MAIL_CLIENT_SANS_CONSULT, MAIL_CLIENT_HISTO, MAIL_CLIENT_CONSULT);
        for (String mail : mailsCandidats) {
            Client client = clientInitialise(mail);
            if (client != null) {
                List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
                if (historique != null && historique.isEmpty()) {
                    return client;
                }
            }
        }
        return null;
    }

    private static Medium mediumInitialise() {
        return persistanceHelper.lecture(() -> mediumDao.listerParDenomination().stream()
                .filter(m -> m.getDenomination() != null
                && DENOMINATION_MEDIUM_INITIALISE.equalsIgnoreCase(m.getDenomination()))
                .findFirst()
                .orElse(null));
    }

    private static void assurerEmployeCompatibleDisponible(Medium medium) {
        if (medium == null || medium.getGenre() == null) {
            return;
        }

        persistanceHelper.transaction(() -> {
            List<Employe> employes = employeDao.listerParNomPrenom();
            for (Employe employe : employes) {
                if (employe.getGenre() == medium.getGenre() && !employe.isEstDisponible()) {
                    employe.setEstDisponible(true);
                    employeDao.mettreAJour(employe);
                    break;
                }
            }
            return null;
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
