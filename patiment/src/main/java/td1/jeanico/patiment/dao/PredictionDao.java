package td1.jeanico.patiment.dao;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.metier.modele.Prediction;

public class PredictionDao {

    public void create(Prediction prediction) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(prediction);
    }

    public Prediction update(Prediction prediction) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(prediction);
    }

    public void delete(Prediction prediction) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Prediction managedPrediction = em.contains(prediction) ? prediction : em.merge(prediction);
        em.remove(managedPrediction);
    }

    public Prediction findById(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Prediction.class, id);
    }

    public List<Prediction> findAllOrderedById() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT p FROM Prediction p ORDER BY p.ID ASC",
                Prediction.class
        ).getResultList();
    }
}
