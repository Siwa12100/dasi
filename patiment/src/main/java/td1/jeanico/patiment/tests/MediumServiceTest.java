/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.tests;

import td1.jeanico.patiment.metier.modeles.mediums.TypeMedium;
import td1.jeanico.patiment.metier.services.MediumService;

/**
 *
 * @author ncolomb
 */
public class MediumServiceTest {
    public static void exec() {
        MediumService service = new MediumService();
        System.out.println(service.listerMediums());
        System.out.println(service.listerMediums(TypeMedium.Astrologue));
        System.out.println(service.listerMediums(TypeMedium.Cartomancien));
        System.out.println(service.listerMediums(TypeMedium.Spirite));
        System.out.println(service.listerTypesMedium());
        System.out.println(service.recupererMediumParId(Long.valueOf(2)).toString());
    }
}
