/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.daos;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.modeles.utilisateurs.Utilisateur;

/**
 *
 * @author ncolomb
 */
public class UtilisateurDao {
    public Utilisateur trouverParMailEtMotDePasse(String mail, String motDePasse) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        List<Utilisateur> resultats = em.createQuery(
                "SELECT c FROM Utilisateur c WHERE LOWER(c.mail) = LOWER(:mail) AND c.motDePasse = :motDePasse",
                Utilisateur.class
        ).setParameter("mail", mail)
                .setParameter("motDePasse", motDePasse)
                .setMaxResults(1)
                .getResultList();
        return resultats.isEmpty() ? null : resultats.get(0);
    }
}
