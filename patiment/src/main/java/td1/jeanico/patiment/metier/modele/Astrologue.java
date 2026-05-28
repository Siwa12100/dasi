package td1.jeanico.patiment.metier.modele;

import javax.persistence.Entity;

@Entity
public class Astrologue extends Medium {

    private String formation;
    private String promotion;

    public Astrologue() {
    }

    public Astrologue(String denomination, Genre genre, String presentation, String formation, String promotion) {
        super(denomination, genre, presentation);
        this.formation = formation;
        this.promotion = promotion;
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }
}
