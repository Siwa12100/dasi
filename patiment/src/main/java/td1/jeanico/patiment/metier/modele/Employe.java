package td1.jeanico.patiment.metier.modele;

import java.util.Objects;
import javax.persistence.Entity;

@Entity
public class Employe extends Utilisateur {

    private boolean estDisponible;

    public Employe() {
    }

    public Employe(String mail, String prenom, String nom, String motDePasse, String telephone, Genre genre, boolean estDisponible) {
        super(mail, prenom, nom, motDePasse, telephone, genre);
        this.estDisponible = estDisponible;
    }

    public boolean isEstDisponible() {
        return estDisponible;
    }

    public void setEstDisponible(boolean estDisponible) {
        this.estDisponible = estDisponible;
    }

    @Override
    public String toString() {
        return "Employe{" + "id=" + ID + ", nom=" + nom + ", prenom=" + prenom + ", mail=" + mail + ", telephone=" + telephone + ", motDePasse=" + motDePasse + ", estDisponible=" + estDisponible + ", genre=" + genre + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.ID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Employe other = (Employe) obj;
        return Objects.equals(this.ID, other.ID);
    }
}
