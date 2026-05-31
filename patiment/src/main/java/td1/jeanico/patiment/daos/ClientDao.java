/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.daos;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.modeles.utilisateurs.Client;

/**
 *
 * @author ncolomb
 */
public class ClientDao {

    public void creer(Client cl) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(cl);
    }

    public Client mettreAJour(Client cl) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(cl);
    }

    public void supprimer(Client cl) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Client clientGere = em.contains(cl) ? cl : em.merge(cl);
        em.remove(clientGere);
    }

    public Client trouverParId(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Client.class, id);
    }

    public Client trouverParMail(String mail) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        List<Client> resultats = em.createQuery(
                "SELECT c FROM Client c WHERE LOWER(c.mail) = LOWER(:mail)",
                Client.class
        ).setParameter("mail", mail)
                .setMaxResults(1)
                .getResultList();
        return resultats.isEmpty() ? null : resultats.get(0);
    }

    public Client trouverParMailEtMotDePasse(String mail, String motDePasse) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        List<Client> resultats = em.createQuery(
                "SELECT c FROM Client c WHERE LOWER(c.mail) = LOWER(:mail) AND c.motDePasse = :motDePasse",
                Client.class
        ).setParameter("mail", mail)
                .setParameter("motDePasse", motDePasse)
                .setMaxResults(1)
                .getResultList();
        return resultats.isEmpty() ? null : resultats.get(0);
    }

    public List<Client> listerParNomPrenom() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Client c ORDER BY c.nom ASC, c.prenom ASC",
                Client.class
        ).getResultList();
    }
}
