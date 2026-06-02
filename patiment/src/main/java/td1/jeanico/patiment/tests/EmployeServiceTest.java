package td1.jeanico.patiment.tests;

import java.util.List;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.services.EmployeService;

public class EmployeServiceTest {

    private static final String MAIL_EMPLOYE_INITIALISE = "anna@predictif.fr";

    private static int nbTests = 0;
    private static int nbSucces = 0;
    private static EmployeService employeService;

    private EmployeServiceTest() {
    }

    public static void lancerTestsEmployeService() {
        nbTests = 0;
        nbSucces = 0;
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
        System.out.println("Test : Recuperer un employe initialise en BDD par son id");

        Employe employeInitialise = employeInitialise();
        verifier("prerequis : employe initialise retrouve", employeInitialise != null);

        if (employeInitialise != null) {
            Employe resultat = employeService.recupererEmployeParId(employeInitialise.getId());

            verifier("employe recupere non nul", resultat != null);
            verifier("id recupere correct", resultat != null && employeInitialise.getId().equals(resultat.getId()));
            verifier("mail recupere correct", resultat != null && employeInitialise.getMail().equalsIgnoreCase(resultat.getMail()));
        }
    }

    public static void test_ListerEmployes_RetourneListeNonNulle() {
        System.out.println("Test : Retourner une liste non nulle des employes");

        List<Employe> resultat = employeService.listerEmployes();

        verifier("liste retournee non nulle", resultat != null);
    }

    public static void test_ListerEmployes_ContientEmployeCree() {
        System.out.println("Test : La liste des employes contient un employe initialise en BDD");

        Employe employeInitialise = employeInitialise();
        List<Employe> resultat = employeService.listerEmployes();

        boolean contient = resultat != null
                && employeInitialise != null
                && resultat.stream().anyMatch(e -> e.getId() != null && e.getId().equals(employeInitialise.getId()));

        verifier("prerequis : employe initialise retrouve", employeInitialise != null);
        verifier("liste retournee non nulle", resultat != null);
        verifier("liste contient l'employe initialise", contient);
    }

    private static Employe employeInitialise() {
        return employeService.listerEmployes().stream()
                .filter(e -> e.getMail() != null && MAIL_EMPLOYE_INITIALISE.equalsIgnoreCase(e.getMail()))
                .findFirst()
                .orElse(null);
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

}