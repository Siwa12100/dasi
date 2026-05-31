package td1.jeanico.patiment.modeles.clients;

import java.io.Serializable;
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
    public String toString() {
        return "ProfilAstral{" + "animalTotal=" + animalTotal + ", signeZodiac=" + signeZodiac + ", couleurBonheur=" + couleurBonheur + ", signeChinois=" + signeChinois + '}';
    }
}
