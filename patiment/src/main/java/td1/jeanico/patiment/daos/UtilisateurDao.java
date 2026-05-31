package td1.jeanico.patiment.daos;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.modeles.utilisateurs.Utilisateur;

public class UtilisateurDao {

    public void creer(Utilisateur utilisateur) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(utilisateur);
    }

    public Utilisateur mettreAJour(Utilisateur utilisateur) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(utilisateur);
    }

    public void supprimer(Utilisateur utilisateur) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Utilisateur utilisateurGere = em.contains(utilisateur) ? utilisateur : em.merge(utilisateur);
        em.remove(utilisateurGere);
    }

    public Utilisateur trouverParId(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Utilisateur.class, id);
    }

    public List<Utilisateur> listerParPrenom() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT u FROM Utilisateur u ORDER BY u.prenom ASC",
                Utilisateur.class
        ).getResultList();
    }
}
