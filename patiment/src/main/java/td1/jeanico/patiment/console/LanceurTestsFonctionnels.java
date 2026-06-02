package td1.jeanico.patiment.console;

import td1.jeanico.patiment.tests.MediumServiceTest;
import td1.jeanico.patiment.tests.PredictionServiceTest;
import td1.jeanico.patiment.tests.StatistiqueServiceTest;

public class LanceurTestsFonctionnels {
    

    public static void lancerTestsFonctionnels() {
        System.out.println("Lancement des tests fonctionnels...");

        MediumServiceTest.exec();
        PredictionServiceTest.exec();
        StatistiqueServiceTest.exec();
    }
}
