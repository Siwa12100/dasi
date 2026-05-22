/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.metier.service;

import td1.jeanico.patiment.dao.JpaUtil;
import td1.jeanico.patiment.dao.ClientDao;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.util.Message;

/**
 *
 * @author ncolomb
 */
public class InscriptionService {
    public void inscrireClient(Client client) {
        JpaUtil.creerContextePersistance();
        try {
            JpaUtil.ouvrirTransaction();
        } catch (Exception ex) {
            System.out.println("Err");
        }
        ClientDao.create(client);
    }
}
