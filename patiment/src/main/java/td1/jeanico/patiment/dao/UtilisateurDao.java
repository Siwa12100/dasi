package td1.jeanico.patiment.dao;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.metier.modele.Utilisateur;

public class UtilisateurDao {

    public void create(Utilisateur utilisateur) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(utilisateur);
    }

    public Utilisateur update(Utilisateur utilisateur) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(utilisateur);
    }

    public void delete(Utilisateur utilisateur) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Utilisateur managedUtilisateur = em.contains(utilisateur) ? utilisateur : em.merge(utilisateur);
        em.remove(managedUtilisateur);
    }

    public Utilisateur findById(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Utilisateur.class, id);
    }

    public List<Utilisateur> findAllOrderedByPrenom() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT u FROM Utilisateur u ORDER BY u.prenom ASC",
                Utilisateur.class
        ).getResultList();
    }
}
