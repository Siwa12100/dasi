/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.metier.modele;

import java.util.Objects;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

/**
 *
 * @author ncolomb + jmarcillac
 */

@Entity
public class Client extends Utilisateur {

    protected String nom;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "numeroDeVoie", column = @Column(name = "adresse_numero_de_voie", nullable = false)),
        @AttributeOverride(name = "nomDeVoie", column = @Column(name = "adresse_nom_de_voie", nullable = false)),
        @AttributeOverride(name = "codePostal", column = @Column(name = "adresse_code_postal", nullable = false)),
        @AttributeOverride(name = "codeDepartement", column = @Column(name = "adresse_code_departement", nullable = false))
    })
    private Adresse adresse;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "animalTotal", column = @Column(name = "profil_animal_total")),
        @AttributeOverride(name = "signeZodiac", column = @Column(name = "profil_signe_zodiac")),
        @AttributeOverride(name = "couleurBonheur", column = @Column(name = "profil_couleur_bonheur")),
        @AttributeOverride(name = "signeChinois", column = @Column(name = "profil_signe_chinois"))
    })
    private ProfilAstral profilAstral;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public ProfilAstral getProfilAstral() {
        return profilAstral;
    }

    public void setProfilAstral(ProfilAstral profilAstral) {
        this.profilAstral = profilAstral;
    }
    
    public Client() {
    }
    
    public Client(String nom, String prenom, String mail, String motDePasse) {
        super(mail, prenom, motDePasse, null);
        this.nom = nom;
    }

    public Client(String nom, String prenom, String mail, String motDePasse, String telephone, Adresse adresse) {
        super(mail, prenom, motDePasse, telephone);
        this.nom = nom;
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + ID + ", nom=" + nom + ", prenom=" + prenom + ", mail=" + mail + ", telephone=" + telephone + ", adresse=" + adresse + ", profilAstral=" + profilAstral + '}';
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
        final Client other = (Client) obj;
        return Objects.equals(this.ID, other.ID);
    }
}
