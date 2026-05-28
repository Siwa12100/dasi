package td1.jeanico.patiment.metier.service.client;

import java.util.List;
import td1.jeanico.patiment.dao.ClientDao;
import td1.jeanico.patiment.dao.ConsultationDao;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.modele.Consultation;
import td1.jeanico.patiment.metier.modele.ProfilAstral;
import td1.jeanico.patiment.metier.service.support.IfAstroNetMapper;
import td1.jeanico.patiment.metier.service.support.PersistenceSupport;
import td1.jeanico.patiment.util.Message;
import td1.jeanico.patiment.webclient.IfAstroNetWebClient;
import jakarta.json.JsonObject;

public class ClientService extends PersistenceSupport implements IClientService {

    private final ClientDao clientDao;
    private final ConsultationDao consultationDao;
    private final IfAstroNetWebClient astroNetWebClient;

    public ClientService() {
        this(new ClientDao(), new ConsultationDao(), new IfAstroNetWebClient());
    }

    public ClientService(ClientDao clientDao, ConsultationDao consultationDao, IfAstroNetWebClient astroNetWebClient) {
        this.clientDao = clientDao;
        this.consultationDao = consultationDao;
        this.astroNetWebClient = astroNetWebClient;
    }

    @Override
    public boolean inscrire(Client client) {
        if (client == null || isBlank(client.getMail()) || isBlank(client.getMotDePasse())) {
            return false;
        }

        boolean inscriptionReussie;
        try {
            inscriptionReussie = executeInTransaction(() -> {
                if (clientDao.findByMail(client.getMail()) != null) {
                    return false;
                }
                if (client.getProfilAstral() == null) {
                    JsonObject jsonProfil = astroNetWebClient.recupererProfilAstral(client.getPrenom(), null);
                    client.setProfilAstral(IfAstroNetMapper.versProfilAstral(jsonProfil));
                }
                clientDao.create(client);
                return true;
            });
        } catch (RuntimeException ex) {
            return false;
        }

        if (inscriptionReussie) {
            Message.envoyerMail(
                    "contact@predictif.fr",
                    client.getMail(),
                    "Bienvenue chez PREDICT'IF",
                    "Bonjour " + safe(client.getPrenom()) + ",\n\nnous vous confirmons votre inscription au service PREDICT'IF." +
                    "Rendez-vous vite sur notre site pour consulter votre profil astrologique et profiter des dons incroyables " + 
                    "de nos mediums."
            );
        } else {
            Message.envoyerMail(
                    "contact@predictif.fr",
                    client.getMail(),
                    "Echec de l'inscription chez PREDICT'IF",
                    "Bonjour " + safe(client.getPrenom()) + ",\n\nL'inscription au service PREDICT'IF a malencontreusement échoué." + 
                    "... Merci de recommencer ultérieurement."
            );  
        }

        return inscriptionReussie;
    }

    @Override
    public Client authentifier(String mail, String motDePasse) {
        if (isBlank(mail) || isBlank(motDePasse)) {
            return null;
        }
        return executeRead(() -> clientDao.findByMailAndMotDePasse(mail, motDePasse));
    }

    @Override
    public ProfilAstral consulterProfilAstral(Client client) {
        if (client == null) {
            return null;
        }

        ProfilAstral profilExistant = executeRead(() -> {
            Client clientPersistant = resolveClient(client);
            return clientPersistant == null ? null : clientPersistant.getProfilAstral();
        });

        if (profilExistant != null) {
            return profilExistant;
        }

        try {
            return executeInTransaction(() -> {
                Client clientPersistant = resolveClient(client);
                if (clientPersistant == null) {
                    return null;
                }
                JsonObject jsonProfil = astroNetWebClient.recupererProfilAstral(clientPersistant.getPrenom(), null);
                ProfilAstral profilAstral = IfAstroNetMapper.versProfilAstral(jsonProfil);
                clientPersistant.setProfilAstral(profilAstral);
                clientDao.update(clientPersistant);
                return profilAstral;
            });
        } catch (RuntimeException ex) {
            return null;
        }
    }

    @Override
    public Client consulterProfilClient(Client client) {
        if (client == null) {
            return null;
        }
        return executeRead(() -> {
            Client clientPersistant = resolveClient(client);
            if (clientPersistant == null) {
                return null;
            }
            List<Consultation> consultations = consultationDao.findByClient(clientPersistant);
            clientPersistant.setHistoriqueConsultations(consultations);
            return clientPersistant;
        });
    }

    @Override
    public Client recupererClientParId(Long id) {
        if (id == null) {
            return null;
        }
        return executeRead(() -> clientDao.findById(id));
    }

    private Client resolveClient(Client client) {
        if (client.getId() != null) {
            return clientDao.findById(client.getId());
        }
        if (!isBlank(client.getMail())) {
            return clientDao.findByMail(client.getMail());
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
