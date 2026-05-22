/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.dao;

import javax.persistence.EntityManager;
import td1.jeanico.patiment.metier.modele.Client;

/**
 *
 * @author ncolomb
 */
public class ClientDao {
    // TODO: est-ce bien static ?
    public static void create(Client cl) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(cl);
    }
    
}
