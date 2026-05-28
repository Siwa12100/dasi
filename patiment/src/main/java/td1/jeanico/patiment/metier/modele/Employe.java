package td1.jeanico.patiment.metier.modele;

import javax.persistence.Entity;

@Entity
public class Employe extends Utilisateur {

    private boolean estDisponible;

    public Employe() {
    }

    public Employe(String mail, String prenom, String motDePasse, String telephone, boolean estDisponible) {
        super(mail, prenom, motDePasse, telephone);
        this.estDisponible = estDisponible;
    }

    public boolean isEstDisponible() {
        return estDisponible;
    }

    public void setEstDisponible(boolean estDisponible) {
        this.estDisponible = estDisponible;
    }
}
