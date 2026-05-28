package td1.jeanico.patiment.metier.modele;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class ProfilAstral implements Serializable {

    private String animalTotal;
    private String signeZodiac;
    private String couleurBonheur;
    private String signeChinois;

    public ProfilAstral() {
    }

    public ProfilAstral(String animalTotal, String signeZodiac, String couleurBonheur, String signeChinois) {
        this.animalTotal = animalTotal;
        this.signeZodiac = signeZodiac;
        this.couleurBonheur = couleurBonheur;
        this.signeChinois = signeChinois;
    }

    public String getAnimalTotal() {
        return animalTotal;
    }

    public void setAnimalTotal(String animalTotal) {
        this.animalTotal = animalTotal;
    }

    public String getSigneZodiac() {
        return signeZodiac;
    }

    public void setSigneZodiac(String signeZodiac) {
        this.signeZodiac = signeZodiac;
    }

    public String getCouleurBonheur() {
        return couleurBonheur;
    }

    public void setCouleurBonheur(String couleurBonheur) {
        this.couleurBonheur = couleurBonheur;
    }

    public String getSigneChinois() {
        return signeChinois;
    }

    public void setSigneChinois(String signeChinois) {
        this.signeChinois = signeChinois;
    }

    @Override
    public int hashCode() {
        return Objects.hash(animalTotal, signeZodiac, couleurBonheur, signeChinois);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProfilAstral other = (ProfilAstral) obj;
        return Objects.equals(this.animalTotal, other.animalTotal)
                && Objects.equals(this.signeZodiac, other.signeZodiac)
                && Objects.equals(this.couleurBonheur, other.couleurBonheur)
                && Objects.equals(this.signeChinois, other.signeChinois);
    }
}
