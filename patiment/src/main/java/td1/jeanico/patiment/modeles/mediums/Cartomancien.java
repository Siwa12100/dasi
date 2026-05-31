package td1.jeanico.patiment.modeles.mediums;

import javax.persistence.Entity;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;

@Entity
public class Cartomancien extends Medium {

    public Cartomancien() {
    }

    public Cartomancien(String denomination, Genre genre, String presentation) {
        super(denomination, genre, presentation);
    }
}
