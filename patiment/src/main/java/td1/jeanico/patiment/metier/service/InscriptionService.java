/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.metier.service;

import java.util.ArrayList;
import td1.jeanico.patiment.dao.JpaUtil;
import td1.jeanico.patiment.dao.ClientDao;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.util.Message;
import java.util.List;
/**
 *
 * @author ncolomb
 */
public class InscriptionService {
    
    private final ClientDao clientDao;
    
    public InscriptionService() {
        this.clientDao = new ClientDao();
    }
    
    public void inscrireClient(Client client) {
        JpaUtil.creerContextePersistance();
        try {
            JpaUtil.ouvrirTransaction();
            this.clientDao.create(client);
            JpaUtil.validerTransaction();
        } catch (Exception ex) {
            System.out.println("Err");
            JpaUtil.annulerTransaction();
        }
        JpaUtil.fermerContextePersistance();
    }
    
    public List<Client> listerClient() {
        JpaUtil.creerContextePersistance();
        List<Client> lesnNouveauxClientEtToutEtTout = new ArrayList<>();
        try {
            lesnNouveauxClientEtToutEtTout = this.clientDao.lister();
        } catch (Exception ex) {
            System.out.println("Err");
        }
        JpaUtil.fermerContextePersistance();
        return lesnNouveauxClientEtToutEtTout;
    }
}
