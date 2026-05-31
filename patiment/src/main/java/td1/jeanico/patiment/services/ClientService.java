package td1.jeanico.patiment.services;

import td1.jeanico.patiment.mappers.MappeurAstroNet;

import td1.jeanico.patiment.outils.SupportPersistance;

import java.time.LocalDate;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.modeles.clients.Adresse;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.clients.ProfilAstral;
import td1.jeanico.patiment.outils.Message;
import td1.jeanico.patiment.webClients.ClientWebAstroNet;
import jakarta.json.JsonObject;

public class ClientService extends SupportPersistance {

    private final ClientDao clientDao;
    private final ClientWebAstroNet astroNetWebClient;

    /**
     * Constructeur par défaut pour un usage applicatif standard.
     */
    public ClientService() {
        this(new ClientDao(), new ClientWebAstroNet());
    }

    /**
     * Constructeur injectable pour les tests et la configuration avancée.
     */
    public ClientService(ClientDao clientDao, ClientWebAstroNet astroNetWebClient) {
        this.clientDao = clientDao;
        this.astroNetWebClient = astroNetWebClient;
    }

    /**
     * Inscrit un client si ses données sont valides et si son e-mail est unique.
     * Le profil astral est demandé à AstroNet puis persisté avec le client.
     */
    public boolean inscrire(Client client) {
        if (!aInformationsInscriptionValides(client)) {
            return false;
        }

        boolean inscriptionReussie;
        try {
            inscriptionReussie = executerEnTransaction(() -> {
                // Un e-mail déjà présent bloque l'inscription.
                if (clientDao.trouverParMail(client.getMail()) != null) {
                    return false;
                }
                client.setProfilAstral(recupererProfilAstralDepuisAstroNet(client.getPrenom(), client.getDateNaissance()));
                clientDao.creer(client);
                return true;
            });
        } catch (RuntimeException ex) {
            return false;
        }

        // Une notification est systématiquement envoyée (succès ou échec).
        if (inscriptionReussie) {
            Message.envoyerMail(
                    "contact@predict.if",
                    client.getMail(),
                    "Bienvenue chez PREDICT'IF",
                    "Bonjour " + securiser(client.getPrenom()) + ",\n\nnous vous confirmons votre inscription au service PREDICT'IF." +
                    "Rendez-vous vite sur notre site pour consulter votre profil astrologique et profiter des dons incroyables " + 
                    "de nos mediums."
            );
        } else {
            Message.envoyerMail(
                    "contact@predict.if",
                    client.getMail(),
                    "Echec de l'inscription chez PREDICT'IF",
                    "Bonjour " + securiser(client.getPrenom()) + ",\n\nL'inscription au service PREDICT'IF a malencontreusement échoué." + 
                    "... Merci de recommencer ultérieurement."
            );  
        }

        return inscriptionReussie;
    }

    /**
     * Authentifie un client par couple mail/mot de passe.
     * Retourne null si les entrées sont vides ou inconnues.
     */
    public Client authentifier(String mail, String motDePasse) {
        if (estVide(mail) || estVide(motDePasse)) {
            return null;
        }
        return executerLecture(() -> clientDao.trouverParMailEtMotDePasse(mail, motDePasse));
    }

    /**
     * Retourne le profil astral d'un client.
     * Si absent en base, il est récupéré via AstroNet puis sauvegardé.
     */
    public ProfilAstral consulterProfilAstral(Client client) {
        if (client == null) {
            return null;
        }

        ProfilAstral profilExistant = executerLecture(() -> {
            Client clientPersistant = resoudreClient(client);
            return clientPersistant == null ? null : clientPersistant.getProfilAstral();
        });

        if (profilExistant != null) {
            return profilExistant;
        }

        try {
            return executerEnTransaction(() -> {
                Client clientPersistant = resoudreClient(client);
                if (clientPersistant == null) {
                    return null;
                }
                ProfilAstral profilAstral = recupererProfilAstralDepuisAstroNet(
                        clientPersistant.getPrenom(),
                        clientPersistant.getDateNaissance()
                );
                clientPersistant.setProfilAstral(profilAstral);
                clientDao.mettreAJour(clientPersistant);
                return profilAstral;
            });
        } catch (RuntimeException ex) {
            return null;
        }
    }

    /**
     * Recharge le client depuis la base avec les données persistées les plus récentes.
     */
    public Client consulterProfilClient(Client client) {
        if (client == null) {
            return null;
        }
        return executerLecture(() -> resoudreClient(client));
    }

    /**
     * Recherche un client par identifiant technique.
     */
    public Client recupererClientParId(Long id) {
        if (id == null) {
            return null;
        }
        return executerLecture(() -> clientDao.trouverParId(id));
    }

    /**
     * Résout un client soit par son id, soit par son mail.
     */
    private Client resoudreClient(Client client) {
        if (client.getId() != null) {
            return clientDao.trouverParId(client.getId());
        }
        if (!estVide(client.getMail())) {
            return clientDao.trouverParMail(client.getMail());
        }
        return null;
    }

    /**
     * Interroge AstroNet puis transforme la réponse JSON en objet métier.
     */
    private ProfilAstral recupererProfilAstralDepuisAstroNet(String prenom, LocalDate dateNaissance) {
        JsonObject jsonProfil = astroNetWebClient.recupererProfilAstral(prenom, dateNaissance);
        return MappeurAstroNet.versProfilAstral(jsonProfil);
    }

    /**
     * Vérifie les prérequis minimum pour autoriser l'inscription.
     */
    private boolean aInformationsInscriptionValides(Client client) {
        return client != null
                && !estVide(client.getNom())
                && !estVide(client.getPrenom())
                && !estVide(client.getMail())
                && !estVide(client.getMotDePasse())
                && !estVide(client.getTelephone())
                && client.getDateNaissance() != null
                && aAdresseValide(client.getAdresse());
    }

    /**
     * Vérifie la complétude de l'adresse du client.
     */
    private boolean aAdresseValide(Adresse adresse) {
        return adresse != null
                && !estVide(adresse.getNumeroDeVoie())
                && !estVide(adresse.getNomDeVoie())
                && !estVide(adresse.getCodePostal())
                && !estVide(adresse.getCodeDepartement())
                && !estVide(adresse.getVille());
    }

    /**
     * Indique si une chaîne est nulle ou vide (après trim implicite de isBlank).
     */
    private boolean estVide(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Sécurise les concaténations de texte (évite les null).
     */
    private String securiser(String value) {
        return value == null ? "" : value;
    }
}
