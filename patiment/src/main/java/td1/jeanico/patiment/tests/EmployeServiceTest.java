package td1.jeanico.patiment.tests;

import java.util.List;
import java.util.function.Supplier;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.outils.SupportPersistance;
import td1.jeanico.patiment.services.EmployeService;

public class EmployeServiceTest {

    private static int nbTests = 0;
    private static int nbSucces = 0;
    private static int sequenceMail = 1;

    private static EmployeService employeService;
    private static final EmployeDao employeDao = new EmployeDao();
    private static final PersistanceTestHelper persistanceHelper = new PersistanceTestHelper();

    private EmployeServiceTest() {
    }

    public static void lancerTestsEmployeService() {
        nbTests = 0;
        nbSucces = 0;
        sequenceMail = 1;
        employeService = new EmployeService();

        System.out.println("\n=== Lancement des tests console de EmployeService ===");

        test_RecupererEmployeParId_IdNull();
        test_RecupererEmployeParId_IdInexistant();
        test_RecupererEmployeParId_Valide();
        test_ListerEmployes_RetourneListeNonNulle();
        test_ListerEmployes_ContientEmployeCree();

        System.out.println("=== Bilan EmployeService: " + nbSucces + "/" + nbTests + " tests valides ===\n");
    }

    public static void test_RecupererEmployeParId_IdNull() {
        System.out.println("Test : Retourner null si l'id employe est null");

        Employe resultat = employeService.recupererEmployeParId(null);

        verifier("retourne null", resultat == null);
    }

    public static void test_RecupererEmployeParId_IdInexistant() {
        System.out.println("Test : Retourner null si l'id employe est inexistant");

        Employe resultat = employeService.recupererEmployeParId(-999L);

        verifier("retourne null pour id inexistant", resultat == null);
    }

    public static void test_RecupererEmployeParId_Valide() {
        System.out.println("Test : Recuperer un employe par son id");

        Employe cree = employePersistant("recup", Genre.NON_SPECIFIE, true);

        Employe resultat = employeService.recupererEmployeParId(cree.getId());

        verifier("employe recupere non nul", resultat != null);
        verifier("id recupere correct", resultat != null && cree.getId().equals(resultat.getId()));
        verifier("mail recupere correct", resultat != null && cree.getMail().equalsIgnoreCase(resultat.getMail()));
    }

    public static void test_ListerEmployes_RetourneListeNonNulle() {
        System.out.println("Test : Retourner une liste non nulle des employes");

        List<Employe> resultat = employeService.listerEmployes();

        verifier("liste retournee non nulle", resultat != null);
    }

    public static void test_ListerEmployes_ContientEmployeCree() {
        System.out.println("Test : La liste des employes contient l'employe cree");

        Employe cree = employePersistant("liste", Genre.HOMME, true);
        List<Employe> resultat = employeService.listerEmployes();

        boolean contient = resultat != null
                && resultat.stream().anyMatch(e -> e.getId() != null && e.getId().equals(cree.getId()));

        verifier("liste retournee non nulle", resultat != null);
        verifier("liste contient l'employe cree", contient);
    }

    private static Employe employePersistant(String prefixe, Genre genre, boolean disponible) {
        return persistanceHelper.transaction(() -> {
            Employe employe = new Employe(
                    mailUnique(prefixe),
                    "Prenom" + sequenceMail,
                    "Nom" + sequenceMail,
                    "motdepasse",
                    "070000000" + (sequenceMail % 10),
                    genre,
                    disponible
            );
            employeDao.creer(employe);
            return employe;
        });
    }

    private static String mailUnique(String prefixe) {
        return prefixe + "." + System.currentTimeMillis() + "." + (sequenceMail++) + "@test.fr";
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

        private <T> T transaction(Supplier<T> action) {
            return executerEnTransaction(action);
        }
    }
}