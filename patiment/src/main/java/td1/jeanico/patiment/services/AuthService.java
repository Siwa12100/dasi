package td1.jeanico.patiment.services;

import td1.jeanico.patiment.outils.SupportPersistance;

import td1.jeanico.patiment.daos.UtilisateurDao;
import td1.jeanico.patiment.modeles.utilisateurs.Utilisateur;

public class AuthService extends SupportPersistance {

    private final UtilisateurDao utilisateurDao;

    /**
     * Constructeur par défaut pour un usage applicatif standard.
     */
    public AuthService() {
        this(new UtilisateurDao());
    }

    /**
     * Constructeur injectable pour les tests et la configuration avancée.
     * @param utilisateurDao
     */
    public AuthService(UtilisateurDao utilisateurDao) {
        this.utilisateurDao = utilisateurDao;
    }

    /**
     * Authentifie un client par couple mail/mot de passe.
     * Retourne null si les entrées sont vides ou inconnues.
     * @param mail
     * @param motDePasse
     * @return 
     */
    public Utilisateur authentifier(String mail, String motDePasse) {
        if (estVide(mail) || estVide(motDePasse)) {
            return null;
        }
        return executerLecture(() -> utilisateurDao.trouverParMailEtMotDePasse(mail, motDePasse));
    }

    /**
     * Indique si une chaîne est nulle ou vide (après trim implicite de isBlank).
     */
    private boolean estVide(String value) {
        return value == null || value.isBlank();
    }
}
