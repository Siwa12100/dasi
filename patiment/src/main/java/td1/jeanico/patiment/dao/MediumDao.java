package td1.jeanico.patiment.dao;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.metier.modele.Astrologue;
import td1.jeanico.patiment.metier.modele.Cartomancien;
import td1.jeanico.patiment.metier.modele.Medium;
import td1.jeanico.patiment.metier.modele.Spirite;

public class MediumDao {

    public void create(Medium medium) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(medium);
    }

    public Medium update(Medium medium) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(medium);
    }

    public void delete(Medium medium) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Medium managedMedium = em.contains(medium) ? medium : em.merge(medium);
        em.remove(managedMedium);
    }

    public Medium findById(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Medium.class, id);
    }

    public List<Medium> findAllOrderedByDenomination() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT m FROM Medium m ORDER BY m.denomination ASC",
                Medium.class
        ).getResultList();
    }

    public List<Spirite> findSpiritesOrderedByDenomination() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT s FROM Spirite s ORDER BY s.denomination ASC",
                Spirite.class
        ).getResultList();
    }

    public List<Cartomancien> findCartomanciensOrderedByDenomination() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Cartomancien c ORDER BY c.denomination ASC",
                Cartomancien.class
        ).getResultList();
    }

    public List<Astrologue> findAstrologuesOrderedByDenomination() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT a FROM Astrologue a ORDER BY a.denomination ASC",
                Astrologue.class
        ).getResultList();
    }
}
