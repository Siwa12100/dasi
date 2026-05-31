package td1.jeanico.patiment.modeles.mediums;

import javax.persistence.Entity;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;

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
