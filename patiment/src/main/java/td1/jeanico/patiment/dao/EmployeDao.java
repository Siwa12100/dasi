package td1.jeanico.patiment.dao;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.metier.modele.Employe;

public class EmployeDao {

    public void create(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(employe);
    }

    public Employe update(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(employe);
    }

    public void delete(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Employe managedEmploye = em.contains(employe) ? employe : em.merge(employe);
        em.remove(managedEmploye);
    }

    public Employe findById(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Employe.class, id);
    }

    public List<Employe> findAllOrderedByNomPrenom() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT e FROM Employe e ORDER BY e.nom ASC, e.prenom ASC",
                Employe.class
        ).getResultList();
    }

    public List<Employe> findAllDisponiblesOrderedByNomPrenom() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT e FROM Employe e WHERE e.estDisponible = TRUE ORDER BY e.nom ASC, e.prenom ASC",
                Employe.class
        ).getResultList();
    }
}
