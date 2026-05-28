package td1.jeanico.patiment.metier.service.client;

import java.util.List;
import td1.jeanico.patiment.dao.ClientDao;
import td1.jeanico.patiment.dao.ConsultationDao;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.modele.ProfilAstral;
import td1.jeanico.patiment.metier.service.support.AstroGateway;
import td1.jeanico.patiment.metier.service.support.PersistenceSupport;
import td1.jeanico.patiment.util.Message;

public class ClientService extends PersistenceSupport implements IClientService {

    private final ClientDao clientDao;
    private final ConsultationDao consultationDao;
    private final AstroGateway astroGateway;

    public ClientService() {
        this(new ClientDao(), new ConsultationDao(), new AstroGateway());
    }

    public ClientService(ClientDao clientDao, ConsultationDao consultationDao, AstroGateway astroGateway) {
        this.clientDao = clientDao;
        this.consultationDao = consultationDao;
        this.astroGateway = astroGateway;
    }

    @Override
    public boolean inscrire(Client client) {
        if (client == null || isBlank(client.getMail()) || isBlank(client.getMotDePasse())) {
            return false;
        }

        boolean inscriptionReussie = executeInTransaction(() -> {
            if (clientDao.findByMail(client.getMail()) != null) {
                return false;
            }
            if (client.getProfilAstral() == null) {
                client.setProfilAstral(astroGateway.construireProfilAstral(client));
            }
            clientDao.create(client);
            return true;
        });

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

        return executeInTransaction(() -> {
            Client clientPersistant = resolveClient(client);
            if (clientPersistant == null) {
                return null;
            }
            ProfilAstral profilAstral = astroGateway.construireProfilAstral(clientPersistant);
            clientPersistant.setProfilAstral(profilAstral);
            clientDao.update(clientPersistant);
            return profilAstral;
        });
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
            List<td1.jeanico.patiment.metier.modele.Consultation> consultations = consultationDao.findByClient(clientPersistant);
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
