/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package td1.jeanico.patiment.console;

import td1.jeanico.patiment.daos.JpaUtil;

/**
 *
 * @author ncolomb
 */
public class Patiment {
    public static void main(String[] args) {
        JpaUtil.creerFabriquePersistance();
        LanceurInitialisationBdd bdInitialiseur = new LanceurInitialisationBdd();

        int choixLancement = 1;
        switch (choixLancement) {
            case 1 -> {
                bdInitialiseur.lancementInitialisationBdd();
                LanceurScenarios.lancerScenarios();
            }
            case 2 -> {
                bdInitialiseur.lancementInitialisationBdd();
                LanceurTestsFonctionnels.lancerTestsFonctionnels();
            }
            case 3 -> {
                bdInitialiseur.lancementInitialisationBdd();
                LanceurAppConsole.lancerApplication();
            }
            default -> System.out.println("Choix de lancement invalide.");
        }

        JpaUtil.fermerFabriquePersistance();
    }
}
