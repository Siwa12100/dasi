package td1.jeanico.patiment.daos;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.modeles.mediums.Astrologue;
import td1.jeanico.patiment.modeles.mediums.Cartomancien;
import td1.jeanico.patiment.modeles.mediums.Medium;
import td1.jeanico.patiment.modeles.mediums.Spirite;

public class MediumDao {

    public void creer(Medium medium) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(medium);
    }

    public Medium mettreAJour(Medium medium) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(medium);
    }

    public void supprimer(Medium medium) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Medium mediumGere = em.contains(medium) ? medium : em.merge(medium);
        em.remove(mediumGere);
    }

    public Medium trouverParId(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Medium.class, id);
    }

    public List<Medium> listerParDenomination() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT m FROM Medium m ORDER BY m.denomination ASC",
                Medium.class
        ).getResultList();
    }

    public List<Spirite> listerSpirites() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT s FROM Spirite s ORDER BY s.denomination ASC",
                Spirite.class
        ).getResultList();
    }

    public List<Cartomancien> listerCartomanciens() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Cartomancien c ORDER BY c.denomination ASC",
                Cartomancien.class
        ).getResultList();
    }

    public List<Astrologue> listerAstrologues() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT a FROM Astrologue a ORDER BY a.denomination ASC",
                Astrologue.class
        ).getResultList();
    }
}
