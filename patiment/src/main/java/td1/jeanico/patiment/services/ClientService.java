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
import java.util.List;

public class ClientService extends SupportPersistance {

    private final ClientDao clientDao;
    private final ClientWebAstroNet astroNetWebClient;
    private final GeocodageService geocodageService;

    /**
     * Constructeur par défaut pour un usage applicatif standard.
     */
    public ClientService() {
        this(new ClientDao(), new ClientWebAstroNet(), new GeocodageService());
    }
    
    public ClientService(ClientDao clientDao, ClientWebAstroNet astroNetWebClient) {
        this(clientDao, astroNetWebClient, new GeocodageService());
    }

    /**
     * Constructeur injectable pour les tests et la configuration avancée.
     * @param clientDao
     * @param astroNetWebClient
     * @param geocodageService
     */
    public ClientService(ClientDao clientDao, ClientWebAstroNet astroNetWebClient, GeocodageService geocodageService) {
        this.clientDao = clientDao;
        this.astroNetWebClient = astroNetWebClient;
        this.geocodageService = geocodageService;
    }

    /**
     * Inscrit un client si ses données sont valides et si son e-mail est unique.
     * Le profil astral est demandé à AstroNet puis persisté avec le client.
     * @param client
     * @return 
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
            inscriptionReussie = false;
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
     * Recherche un client par identifiant technique.
     * @param id
     * @return 
     */
    public Client recupererClientParId(Long id) {
        if (id == null) {
            return null;
        }
        return executerLecture(() -> clientDao.trouverParId(id));
    }
    
    /**
     * Liste les clients ordonées par nom/prénom
     * @return 
     */
    public List<Client> listerClients() {
        return executerLecture(clientDao::listerParNomPrenom);
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
                && possedeAdresseValide(client.getAdresse());
    }

    /**
     * Vérifie la complétude de l'adresse du client.
     */
    private boolean possedeAdresseValide(Adresse adresse) {
        boolean adresseValidee = false;
        try {
            Adresse retourGeoService = this.geocodageService.rechercherAdresse(
                    adresse.getNumeroDeVoie() + " " +
                    adresse.getNomDeVoie() + " " +
                    adresse.getCodePostal() + " " +
                    adresse.getVille()
            );
            if (retourGeoService != null) {
                adresseValidee = !estVide(retourGeoService.getNumeroDeVoie())
                        && !estVide(retourGeoService.getNomDeVoie())
                        && !estVide(retourGeoService.getCodePostal())
                        && !estVide(retourGeoService.getVille());
            }
        } catch (Exception e) {
            adresseValidee = false;
        }
        return adresseValidee;
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
