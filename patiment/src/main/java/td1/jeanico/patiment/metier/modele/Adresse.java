package td1.jeanico.patiment.metier.modele;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class Adresse implements Serializable {

    private String numeroDeVoie;
    private String nomDeVoie;
    private String codePostal;
    private String codeDepartement;

    public Adresse() {
    }

    public Adresse(String numeroDeVoie, String nomDeVoie, String codePostal, String codeDepartement) {
        this.numeroDeVoie = numeroDeVoie;
        this.nomDeVoie = nomDeVoie;
        this.codePostal = codePostal;
        this.codeDepartement = codeDepartement;
    }

    public String getNumeroDeVoie() {
        return numeroDeVoie;
    }

    public void setNumeroDeVoie(String numeroDeVoie) {
        this.numeroDeVoie = numeroDeVoie;
    }

    public String getNomDeVoie() {
        return nomDeVoie;
    }

    public void setNomDeVoie(String nomDeVoie) {
        this.nomDeVoie = nomDeVoie;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroDeVoie, nomDeVoie, codePostal, codeDepartement);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Adresse other = (Adresse) obj;
        return Objects.equals(this.numeroDeVoie, other.numeroDeVoie)
                && Objects.equals(this.nomDeVoie, other.nomDeVoie)
                && Objects.equals(this.codePostal, other.codePostal)
                && Objects.equals(this.codeDepartement, other.codeDepartement);
    }
}
