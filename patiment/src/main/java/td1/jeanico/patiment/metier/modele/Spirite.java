package td1.jeanico.patiment.metier.modele;

import javax.persistence.Entity;

@Entity
public class Spirite extends Medium {

    private String support;

    public Spirite() {
    }

    public Spirite(String denomination, Genre genre, String presentation, String support) {
        super(denomination, genre, presentation);
        this.support = support;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }
}
