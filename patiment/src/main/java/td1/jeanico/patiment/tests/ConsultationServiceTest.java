package td1.jeanico.patiment.tests;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.metier.modeles.consultations.Consultation;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.metier.services.ConsultationService;
import td1.jeanico.patiment.outils.SupportPersistance;

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

    /**
     * Constructeur prive: classe utilitaire de tests statiques.
     */
    private ConsultationServiceTest() {
    }

    /**
     * Point d'entree de la suite de tests console.
     * Reinitialise les compteurs puis execute tous les scenarios.
     */
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

    /**
     * Verifie que la demande est refusee quand le client est null.
     */
    public static void test_DemanderConsultation_ClientNull() {
        System.out.println("Test : Refuser une demande de consultation si le client est null");

        Medium medium = mediumInitialise();
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (medium != null) {
            assurerEmployeCompatibleDisponible(medium);
            Consultation resultat = consultationService.demanderConsultation(null, medium);

            verifier("retourne false si client null", resultat == null);
        }
    }

    /**
     * Verifie que la demande est refusee quand le medium est null.
     */
    public static void test_DemanderConsultation_MediumNull() {
        System.out.println("Test : Refuser une demande de consultation si le medium est null");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        verifier("prerequis : client initialise retrouve", client != null);

        if (client != null) {
            Consultation resultat = consultationService.demanderConsultation(client, null);

            verifier("retourne false si medium null", resultat == null);
        }
    }

    /**
     * Verifie le parcours nominal de creation de consultation:
     * consultation creee, active, liee au bon medium, employe indisponible.
     */
    public static void test_DemanderConsultation_Valide() {
        System.out.println("Test : Creer une consultation avec un client et un medium initialises en BDD");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
            Consultation resultat = consultationService.demanderConsultation(client, medium);
            List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
            Consultation creee = (historique == null || historique.isEmpty()) ? null : historique.get(0);
            Employe employeAffecte = creee == null ? null : persistanceHelper.lecture(() -> employeDao.trouverParId(creee.getEmploye().getId()));

            verifier("retourne pas null", resultat != null);
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

    /**
     * Verifie qu'un client null retourne une liste vide non nulle.
     */
    public static void test_ConsulterHistorique_ClientNull() {
        System.out.println("Test : Retourner une liste vide si le client est null");

        List<Consultation> resultat = consultationService.consulterHistoriqueConsultations(null);

        verifier("liste retournee non nulle", resultat != null);
        verifier("liste vide", resultat != null && resultat.isEmpty());
    }

    /**
     * Verifie qu'un client sans consultation retourne un historique vide.
     */
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

    /**
     * Verifie qu'apres une demande valide, l'historique contient au moins une consultation.
     */
    public static void test_ConsulterHistorique_ClientAvecConsultation() {
        System.out.println("Test : Retourner les consultations d'un client initialise ayant une demande");

        Client client = clientInitialise(MAIL_CLIENT_HISTO);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
            Consultation demande = consultationService.demanderConsultation(client, medium);

            List<Consultation> resultat = consultationService.consulterHistoriqueConsultations(client);

            verifier("demande prealable reussie", demande != null);
            verifier("historique non nul", resultat != null);
            verifier("historique contient au moins une consultation", resultat != null && !resultat.isEmpty());
        }
    }

    // -------------------------------------------------------------------------
    // declarerPret
    // -------------------------------------------------------------------------

    /**
     * Verifie que declarerPret ignore une consultation null sans lever d'exception.
     */
    public static void test_DeclarerPret_ConsultationNull() {
        System.out.println("Test : Ne rien faire si la consultation est null");

        try {
            consultationService.declarerPret(null);
            verifier("aucune exception levee", true);
        } catch (Exception e) {
            verifier("aucune exception levee", false);
        }
    }

    /**
     * Verifie que declarerPret n'a pas d'effet bloquant sur une consultation deja terminee.
     */
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

    /**
     * Verifie que declarerPret fonctionne sur une consultation active
     * (au minimum: pas d'exception levee).
     */
    public static void test_DeclarerPret_Valide() {
        System.out.println("Test : Declarer pret sur une consultation active");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
            Consultation demande = consultationService.demanderConsultation(client, medium);

            List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
            boolean prerequis = demande != null && historique != null && !historique.isEmpty();

            verifier("demande prealable reussie", demande != null);
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

    /**
     * Verifie que terminerConsultation ignore une consultation null sans lever d'exception.
     */
    public static void test_TerminerConsultation_ConsultationNull() {
        System.out.println("Test : Ne rien faire si la consultation a terminer est null");

        try {
            consultationService.terminerConsultation(null, "commentaire");
            verifier("aucune exception levee", true);
        } catch (Exception e) {
            verifier("aucune exception levee", false);
        }
    }

    /**
     * Verifie qu'un double appel de terminaison ne leve pas d'exception.
     */
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

    /**
     * Verifie le parcours nominal de terminaison:
     * commentaire persiste, etat terminee, employe libere.
     */
    public static void test_TerminerConsultation_Valide() {
        System.out.println("Test : Terminer une consultation active avec commentaire");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
            List<Consultation> historiqueAvant = consultationService.consulterHistoriqueConsultations(client);
            Set<Long> idsAvant = extraireIdsConsultations(historiqueAvant);
            Consultation demande = consultationService.demanderConsultation(client, medium);

            List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
            Consultation consultation = trouverConsultationCreeePendantLeTest(historique, idsAvant, medium);
            boolean prerequis = demande != null && consultation != null;

            verifier("demande prealable reussie", demande != null);
            verifier("consultation creee pendant le test retrouvee", prerequis);

            if (prerequis) {
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

    /**
     * Extrait les identifiants de consultations pour comparer l'etat avant/apres un test.
     */
    private static Set<Long> extraireIdsConsultations(List<Consultation> consultations) {
        Set<Long> ids = new HashSet<>();
        if (consultations != null) {
            for (Consultation consultation : consultations) {
                if (consultation != null && consultation.getId() != null) {
                    ids.add(consultation.getId());
                }
            }
        }
        return ids;
    }

    /**
     * Retrouve la consultation creee par le test courant en excluant les ids deja presents
     * avant l'appel et en validant le medium attendu.
     */
    private static Consultation trouverConsultationCreeePendantLeTest(List<Consultation> historique, Set<Long> idsAvant, Medium medium) {
        if (historique == null || medium == null || medium.getId() == null) {
            return null;
        }

        for (Consultation consultation : historique) {
            if (consultation == null || consultation.getId() == null) {
                continue;
            }
            boolean creeePendantLeTest = !idsAvant.contains(consultation.getId());
            boolean memeMedium = consultation.getMedium() != null
                    && medium.getId().equals(consultation.getMedium().getId());
            if (creeePendantLeTest && memeMedium) {
                return consultation;
            }
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // recupererConsultationParId
    // -------------------------------------------------------------------------

    /**
     * Verifie que recupererConsultationParId retourne null si l'id est null.
     */
    public static void test_RecupererConsultationParId_IdNull() {
        System.out.println("Test : Retourner null si l'id est null");

        Consultation resultat = consultationService.recupererConsultationParId(null);

        verifier("retourne null", resultat == null);
    }

    /**
     * Verifie que recupererConsultationParId retourne null pour un id inexistant.
     */
    public static void test_RecupererConsultationParId_IdInexistant() {
        System.out.println("Test : Retourner null si l'id est inexistant");

        Consultation resultat = consultationService.recupererConsultationParId(-999L);

        verifier("retourne null pour id inexistant", resultat == null);
    }

    /**
     * Verifie que la consultation recuperee par id correspond bien a celle creee.
     */
    public static void test_RecupererConsultationParId_Valide() {
        System.out.println("Test : Recuperer une consultation par son id (donnees initialisees)");

        Client client = clientInitialise(MAIL_CLIENT_CONSULT);
        Medium medium = mediumInitialise();
        verifier("prerequis : client initialise retrouve", client != null);
        verifier("prerequis : medium initialise retrouve", medium != null);

        if (client != null && medium != null) {
            assurerEmployeCompatibleDisponible(medium);
            Consultation demande = consultationService.demanderConsultation(client, medium);

            List<Consultation> historique = consultationService.consulterHistoriqueConsultations(client);
            boolean prerequis = demande != null && historique != null && !historique.isEmpty();

            verifier("demande prealable reussie", demande != null);
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

    /**
     * Charge un client initialise en BDD via son mail.
     */
    private static Client clientInitialise(String mail) {
        return persistanceHelper.lecture(() -> clientDao.trouverParMail(mail));
    }

    /**
     * Cherche un client initialise dont l'historique est vide.
     * Sert a garantir un scenario de depart sans consultation.
     */
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

    /**
     * Charge un medium initialise en BDD par denomination.
     */
    private static Medium mediumInitialise() {
        return persistanceHelper.lecture(() -> mediumDao.listerParDenomination().stream()
                .filter(m -> m.getDenomination() != null
                && DENOMINATION_MEDIUM_INITIALISE.equalsIgnoreCase(m.getDenomination()))
                .findFirst()
                .orElse(null));
    }

    /**
     * Force la disponibilite d'un employe compatible avant une demande de consultation,
     * afin de rendre le test deterministic.
     */
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

    /**
     * Assertion console minimale: incrémente les compteurs et affiche le resultat.
     */
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

        /**
         * Execute une operation en lecture avec gestion du contexte de persistance.
         */
        private <T> T lecture(Supplier<T> action) {
            return executerLecture(action);
        }

        /**
         * Execute une operation transactionnelle avec commit/rollback.
         */
        private <T> T transaction(Supplier<T> action) {
            return executerEnTransaction(action);
        }
    }
}
