/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package td1.jeanico.patiment.metier.modeles.utilisateurs;

import java.time.LocalDate;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import td1.jeanico.patiment.metier.modeles.clients.Adresse;
import td1.jeanico.patiment.metier.modeles.clients.ProfilAstral;

/**
 *
 * @author ncolomb + jmarcillac
 */

@Entity
public class Client extends Utilisateur {

    private LocalDate dateNaissance;

    @Embedded
    private Adresse adresse;

    @Embedded
    private ProfilAstral profilAstral;

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
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
    
    public Client(String nom, String prenom, String mail, String motDePasse, String telephone, Genre genre, Adresse adresse, LocalDate dateNaissance) {
        super(mail, prenom, nom, motDePasse, telephone, genre);
        this.adresse = adresse;
        this.dateNaissance = dateNaissance;
    }
    
    public Client(String nom, String prenom, String mail, String motDePasse, String telephone, Genre genre, Adresse adresse, LocalDate dateNaissance, ProfilAstral profilAstral) {
        super(mail, prenom, nom, motDePasse, telephone, genre);
        this.adresse = adresse;
        this.dateNaissance = dateNaissance;
        this.profilAstral = profilAstral;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + ID + ", nom=" + nom + ", prenom=" + prenom + ", mail=" + mail + ", telephone=" + telephone + ", motDePasse=" + motDePasse + ", adresse=" + adresse + ", profilAstral=" + profilAstral + ", genre=" + genre + "}";
    }
}
