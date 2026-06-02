/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.tests;

import td1.jeanico.patiment.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.services.PredictionService;

/**
 *
 * @author ncolomb
 */
public class PredictionServiceTest {
    public static void exec() {
        PredictionService service = new PredictionService();
        ProfilAstral profilAstral = new ProfilAstral("lapin", "lapin", "rouge", "lapin");
        System.out.println(service.demandeInspiration(profilAstral, 1, 1, 1));
        System.out.println(service.demandeInspiration(profilAstral, 0, 0, 0));
    }
}
