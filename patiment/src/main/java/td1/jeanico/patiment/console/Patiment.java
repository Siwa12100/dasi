/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package td1.jeanico.patiment.console;

import java.time.LocalDate;
import td1.jeanico.patiment.daos.JpaUtil;
import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;

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
            case 3 -> lancerApplication();
            default -> System.out.println("Choix de lancement invalide."); 
        }

        JpaUtil.fermerFabriquePersistance();
    }

    public static void lancerApplication() {
        System.out.println("Lancement de l'application...");

        System.out.println("Debut du projet !");
        Adresse adresse = new Adresse("1", "Rue de la Paix", "69001", "69", "Lyon");
        Client c1 = new Client("Client", "numero 1", "mail1", "mdp1", "0600000000", Genre.NON_SPECIFIE, adresse, LocalDate.of(1990, 1, 1));
        System.out.println("Le con de premier client tiens : " + c1 + "\n");
        // Ajouter le code pour lancer l'application ici
    }
}
