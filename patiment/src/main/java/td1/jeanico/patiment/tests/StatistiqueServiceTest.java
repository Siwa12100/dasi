/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.tests;

import td1.jeanico.patiment.services.StatistiqueService;

/**
 *
 * @author ncolomb
 */
public class StatistiqueServiceTest {
    public static void exec() {
        StatistiqueService service = new StatistiqueService();
        System.out.println(service.listerNombreConsultationsParMedium());
        System.out.println(service.listerRepartitionClientParEmploye());
        System.out.println(service.listerMediumsPopulaire(2));
        System.out.println(service.listerRepartitionGeographiqueClients());
    }
}
