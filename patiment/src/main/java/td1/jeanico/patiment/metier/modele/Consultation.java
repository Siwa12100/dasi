package td1.jeanico.patiment.metier.modele;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Consultation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    private String commentaire;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private boolean estTermine;

    @ManyToOne(optional = false)
    private Client client;

    @ManyToOne(optional = false)
    private Employe employe;

    @ManyToOne(optional = false)
    private Medium medium;

    public Consultation() {
    }

    public Consultation(String commentaire, Date date, boolean estTermine, Client client, Employe employe, Medium medium) {
        this.commentaire = commentaire;
        this.date = date;
        this.estTermine = estTermine;
        this.client = client;
        this.employe = employe;
        this.medium = medium;
    }

    public Long getId() {
        return ID;
    }

    public void setId(Long ID) {
        this.ID = ID;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isEstTermine() {
        return estTermine;
    }

    public void setEstTermine(boolean estTermine) {
        this.estTermine = estTermine;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Consultation other = (Consultation) obj;
        return Objects.equals(this.ID, other.ID);
    }
}
