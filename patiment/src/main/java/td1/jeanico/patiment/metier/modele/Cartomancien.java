package td1.jeanico.patiment.metier.modele;

import javax.persistence.Entity;

@Entity
public class Cartomancien extends Medium {

    public Cartomancien() {
    }

    public Cartomancien(String denomination, Genre genre, String presentation) {
        super(denomination, genre, presentation);
    }
}
