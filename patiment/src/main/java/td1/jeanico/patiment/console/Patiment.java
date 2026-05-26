/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package td1.jeanico.patiment.console;

import td1.jeanico.patiment.dao.JpaUtil;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.service.InscriptionService;

/**
 *
 * @author ncolomb
 */
public class Patiment {

    public static void main(String[] args) {
        JpaUtil.creerFabriquePersistance();
        
        
        System.out.println("Debut du projet !");
        
        Client c1 = new Client("Client", "numéro 1", "mail1", "mdp1");
        InscriptionService is = new InscriptionService();
        is.inscrireClient(c1);
        System.out.println("Le con de premier client tiens : " + c1 + "\n");
        
    }
}
