package td1.jeanico.patiment.daos;

import java.util.List;
import javax.persistence.EntityManager;

import td1.jeanico.patiment.metier.modeles.mediums.Astrologue;
import td1.jeanico.patiment.metier.modeles.mediums.Cartomancien;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.mediums.Spirite;

public class MediumDao {

    public void creer(Medium medium) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(medium);
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

    public List<Spirite> listerSpiritesParDenomination() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT s FROM Spirite s ORDER BY s.denomination ASC",
                Spirite.class
        ).getResultList();
    }

    public List<Cartomancien> listerCartomanciensParDenomination() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Cartomancien c ORDER BY c.denomination ASC",
                Cartomancien.class
        ).getResultList();
    }

    public List<Astrologue> listerAstrologuesParDenomination() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT a FROM Astrologue a ORDER BY a.denomination ASC",
                Astrologue.class
        ).getResultList();
    }
}
