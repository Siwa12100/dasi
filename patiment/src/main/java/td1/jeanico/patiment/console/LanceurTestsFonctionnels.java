package td1.jeanico.patiment.console;

import td1.jeanico.patiment.tests.AuthServiceTest;
import td1.jeanico.patiment.tests.AdresseChercheurTest;
import td1.jeanico.patiment.tests.ClientServiceTest;
import td1.jeanico.patiment.tests.ConsultationServiceTest;
import td1.jeanico.patiment.tests.EmployeServiceTest;
import td1.jeanico.patiment.tests.MediumServiceTest;
import td1.jeanico.patiment.tests.PredictionServiceTest;
import td1.jeanico.patiment.tests.StatistiqueServiceTest;

public class LanceurTestsFonctionnels {
    

    public static void lancerTestsFonctionnels() {
        System.out.println("Lancement des tests fonctionnels...");
        
        MediumServiceTest.exec();
        PredictionServiceTest.exec();
        StatistiqueServiceTest.exec();

        ClientServiceTest.lancerTestsClientService();
        AdresseChercheurTest.lancerTestsAdresseChercheur();
        AuthServiceTest.lancerTestsAuthService();
        EmployeServiceTest.lancerTestsEmployeService();
        ConsultationServiceTest.lancerTestsConsultationService();
        EmployeServiceTest.lancerTestsEmployeService();
        System.out.println("Fin des tests fonctionnels.");
    }
}
