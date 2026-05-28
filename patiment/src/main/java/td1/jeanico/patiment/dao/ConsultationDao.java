package td1.jeanico.patiment.dao;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.modele.Consultation;
import td1.jeanico.patiment.metier.modele.Employe;

public class ConsultationDao {

    public void create(Consultation consultation) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(consultation);
    }

    public Consultation update(Consultation consultation) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(consultation);
    }

    public void delete(Consultation consultation) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Consultation managedConsultation = em.contains(consultation) ? consultation : em.merge(consultation);
        em.remove(managedConsultation);
    }

    public Consultation findById(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Consultation.class, id);
    }

    public List<Consultation> findAllOrderedByDateDesc() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Consultation c ORDER BY c.date DESC",
                Consultation.class
        ).getResultList();
    }

    public List<Consultation> findByClient(Client client) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Consultation c WHERE c.client = :client ORDER BY c.date DESC",
                Consultation.class
        ).setParameter("client", client)
                .getResultList();
    }

    public List<Consultation> findByEmploye(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Consultation c WHERE c.employe = :employe ORDER BY c.date DESC",
                Consultation.class
        ).setParameter("employe", employe)
                .getResultList();
    }
}
