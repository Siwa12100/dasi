/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package td1.jeanico.patiment.console;

import td1.jeanico.patiment.metier.modele.Client;

/**
 *
 * @author ncolomb
 */
public class Patiment {

    public static void main(String[] args) {
        System.out.println("Debut du projet !");
        
        Client c1 = new Client("Client", "numéro 1", "mail1", "mdp1");
        System.out.println("Le con de premier client tiens : " + c1 + "\n");
        
    }
}
