package td1.jeanico.patiment.daos;

import java.util.List;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;

public class EmployeDao {

    public void creer(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(employe);
    }

    public Employe mettreAJour(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.merge(employe);
    }

    public void supprimer(Employe employe) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        Employe employeGere = em.contains(employe) ? employe : em.merge(employe);
        em.remove(employeGere);
    }

    public Employe trouverParId(Long id) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.find(Employe.class, id);
    }

    public Employe trouverParMailEtMotDePasse(String mail, String motDePasse) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        List<Employe> resultats = em.createQuery(
                "SELECT e FROM Employe e WHERE LOWER(e.mail) = LOWER(:mail) AND e.motDePasse = :motDePasse",
                Employe.class
        ).setParameter("mail", mail)
                .setParameter("motDePasse", motDePasse)
                .setMaxResults(1)
                .getResultList();
        return resultats.isEmpty() ? null : resultats.get(0);
    }

    public List<Employe> listerParNomPrenom() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT e FROM Employe e ORDER BY e.nom ASC, e.prenom ASC",
                Employe.class
        ).getResultList();
    }

    public List<Employe> listerDisponiblesParNomPrenom() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        return em.createQuery(
                "SELECT e FROM Employe e WHERE e.estDisponible = TRUE ORDER BY e.nom ASC, e.prenom ASC",
                Employe.class
        ).getResultList();
    }
}
