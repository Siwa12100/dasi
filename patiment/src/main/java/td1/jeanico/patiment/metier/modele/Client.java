/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.metier.modele;

import java.util.List;
import java.util.Objects;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 *
 * @author ncolomb + jmarcillac
 */

@Entity
public class Client extends Utilisateur {

    protected String nom;

    @Embedded
    private Adresse adresse;

    @Embedded
    private ProfilAstral profilAstral;

    @Transient
    private List<Consultation> historiqueConsultations;

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

    public List<Consultation> getHistoriqueConsultations() {
        return historiqueConsultations;
    }

    public void setHistoriqueConsultations(List<Consultation> historiqueConsultations) {
        this.historiqueConsultations = historiqueConsultations;
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
