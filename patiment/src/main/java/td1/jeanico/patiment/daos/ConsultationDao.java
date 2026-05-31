package td1.jeanico.patiment.daos;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.consultations.Consultation;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;

public class ConsultationDao {

    public void creer(Consultation consultation) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(consultation);
    }

    public Consultation mettreAJour(Consultation consultation) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(consultation);
    }

    public void supprimer(Consultation consultation) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Consultation consultationGeree = em.contains(consultation) ? consultation : em.merge(consultation);
        em.remove(consultationGeree);
    }

    public Consultation trouverParId(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Consultation.class, id);
    }

    public List<Consultation> listerParDateDesc() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Consultation c ORDER BY c.date DESC",
                Consultation.class
        ).getResultList();
    }

    public List<Consultation> trouverParClient(Client client) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Consultation c WHERE c.client = :client ORDER BY c.date DESC",
                Consultation.class
        ).setParameter("client", client)
                .getResultList();
    }

    public List<Consultation> trouverParEmploye(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Consultation c WHERE c.employe = :employe ORDER BY c.date DESC",
                Consultation.class
        ).setParameter("employe", employe)
                .getResultList();
    }

    public List<Consultation> trouverEnCoursParEmploye(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT c FROM Consultation c WHERE c.employe = :employe AND c.estTermine = FALSE ORDER BY c.date DESC",
                Consultation.class
        ).setParameter("employe", employe)
                .getResultList();
    }
}
