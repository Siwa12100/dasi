package td1.jeanico.patiment.console;

import td1.jeanico.patiment.tests.AuthServiceTest;
import td1.jeanico.patiment.tests.ClientServiceTest;
import td1.jeanico.patiment.tests.ConsultationServiceTest;
import td1.jeanico.patiment.tests.EmployeServiceTest;

public class LanceurTestsFonctionnels {
    

    public static void lancerTestsFonctionnels() {
        System.out.println("Lancement des tests fonctionnels...");
        ClientServiceTest.lancerTestsClientService();
        AuthServiceTest.lancerTestsAuthService();
        EmployeServiceTest.lancerTestsEmployeService();
        ConsultationServiceTest.lancerTestsConsultationService();
        EmployeServiceTest.lancerTestsEmployeService();
        System.out.println("Fin des tests fonctionnels.");
    }
}
