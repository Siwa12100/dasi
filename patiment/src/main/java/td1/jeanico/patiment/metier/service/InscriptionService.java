/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.metier.service;

import java.util.List;
import td1.jeanico.patiment.dao.JpaUtil;
import td1.jeanico.patiment.dao.ClientDao;
import td1.jeanico.patiment.metier.modele.Client;

/**
 *
 * @author ncolomb
 */
public class InscriptionService {
    private final ClientDao clientDao;

    public InscriptionService() {
        this(new ClientDao());
    }

    public InscriptionService(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    public void inscrireClient(Client client) {
        JpaUtil.creerContextePersistance();
        try {
            JpaUtil.ouvrirTransaction();
            clientDao.create(client);
            JpaUtil.validerTransaction();
        } catch (Exception ex) {
            JpaUtil.annulerTransaction();
            throw new RuntimeException("Impossible d'inscrire le client", ex);
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }

    public List<Client> listerClients() {
        JpaUtil.creerContextePersistance();
        try {
            return clientDao.findAllOrderedByNomPrenom();
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }
}
