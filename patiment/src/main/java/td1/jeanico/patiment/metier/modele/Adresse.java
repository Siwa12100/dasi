package td1.jeanico.patiment.metier.modele;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class Adresse implements Serializable {

    private String numeroDeVoie;
    private String nomDeVoie;
    private String codePostal;
    private String codeDepartement;
    private String ville;

    public Adresse() {
    }

    public Adresse(String numeroDeVoie, String nomDeVoie, String codePostal, String codeDepartement, String ville) {
        this.numeroDeVoie = numeroDeVoie;
        this.nomDeVoie = nomDeVoie;
        this.codePostal = codePostal;
        this.codeDepartement = codeDepartement;
        this.ville = ville;
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

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    @Override
    public String toString() {
        return "Adresse{" + "numeroDeVoie=" + numeroDeVoie + ", nomDeVoie=" + nomDeVoie + ", codePostal=" + codePostal + ", codeDepartement=" + codeDepartement + ", ville=" + ville + '}';
    }
}
