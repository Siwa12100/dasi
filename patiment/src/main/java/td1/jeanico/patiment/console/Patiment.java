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

    private static void definirConnexionParDefaut() {
        setIfBlank(JpaUtil.PROPRIETE_URL_JDBC, "jdbc:mysql://dasi-mariadb:3306/DASI-DB?zeroDateTimeBehavior=CONVERT_TO_NULL");
        setIfBlank(JpaUtil.PROPRIETE_UTILISATEUR_JDBC, "dasi");
        setIfBlank(JpaUtil.PROPRIETE_MOT_DE_PASSE_JDBC, "dasi");
        setIfBlank(JpaUtil.PROPRIETE_PILOTE_JDBC, "com.mysql.cj.jdbc.Driver");
    }

    private static void setIfBlank(String key, String value) {
        String currentValue = System.getProperty(key);
        if (currentValue == null || currentValue.isBlank()) {
            System.setProperty(key, value);
        }
    }

    public static void main(String[] args) {
        definirConnexionParDefaut();

        boolean connexionBddOk = JpaUtil.testerConnexionJdbc();
        if (connexionBddOk) {
            System.out.println("[Patiment] Connexion BDD OK");
        } else {
            System.out.println("[Patiment] Connexion BDD KO");
        }

        JpaUtil.creerFabriquePersistance();
        System.out.println("Debut du projet !");
        Adresse adresse = new Adresse("1", "Rue de la Paix", "69001", "69", "Lyon");
        Client c1 = new Client("Client", "numero 1", "mail1", "mdp1", "0600000000", Genre.NON_SPECIFIE, adresse, LocalDate.of(1990, 1, 1));
        System.out.println("Le con de premier client tiens : " + c1 + "\n");
    }
}
