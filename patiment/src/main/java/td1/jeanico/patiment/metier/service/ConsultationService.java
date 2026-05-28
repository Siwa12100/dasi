package td1.jeanico.patiment.metier.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import td1.jeanico.patiment.dao.ClientDao;
import td1.jeanico.patiment.dao.ConsultationDao;
import td1.jeanico.patiment.dao.EmployeDao;
import td1.jeanico.patiment.dao.MediumDao;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.modele.Consultation;
import td1.jeanico.patiment.metier.modele.Employe;
import td1.jeanico.patiment.metier.modele.Medium;
import td1.jeanico.patiment.util.Message;

public class ConsultationService extends PersistenceSupport {

    private final ConsultationDao consultationDao;
    private final ClientDao clientDao;
    private final MediumDao mediumDao;
    private final EmployeDao employeDao;

    public ConsultationService() {
        this(new ConsultationDao(), new ClientDao(), new MediumDao(), new EmployeDao());
    }

    public ConsultationService(ConsultationDao consultationDao, ClientDao clientDao, MediumDao mediumDao, EmployeDao employeDao) {
        this.consultationDao = consultationDao;
        this.clientDao = clientDao;
        this.mediumDao = mediumDao;
        this.employeDao = employeDao;
    }
    
    public boolean demanderConsultation(Client client, Medium medium) {
        if (client == null || medium == null) {
            return false;
        }

        Employe[] employeSelectionne = new Employe[1];
        Consultation[] consultationCreee = new Consultation[1];
        Client[] clientPersistant = new Client[1];
        Medium[] mediumPersistant = new Medium[1];

        boolean consultationCreeeAvecSucces = executeInTransaction(() -> {
            Client clientReference = resolveClient(client);
            Medium mediumReference = resolveMedium(medium);
            if (clientReference == null || mediumReference == null) {
                return false;
            }

            List<Employe> employesDisponibles = employeDao.findAllDisponiblesOrderedByNomPrenom();
            if (employesDisponibles.isEmpty()) {
                return false;
            }

            Employe employe = choisirEmployeLeMoinsCharge(employesDisponibles, consultationDao.findAllOrderedByDateDesc());
            if (employe == null) {
                return false;
            }

            employe.setEstDisponible(false);
            employeDao.update(employe);

            Consultation consultation = new Consultation("tout s'est bien déroulé", LocalDateTime.now(), false, clientReference, employe, mediumReference);
            consultationDao.create(consultation);

            employeSelectionne[0] = employe;
            consultationCreee[0] = consultation;
            clientPersistant[0] = clientReference;
            mediumPersistant[0] = mediumReference;
            return true;
        });

        if (consultationCreeeAvecSucces) {
            Message.envoyerNotification(
                    employeSelectionne[0].getTelephone(),
                    "Nouvelle consultation affectee avec "
                            + safe(clientPersistant[0].getPrenom())
                            + " pour le medium "
                            + safe(mediumPersistant[0].getDenomination())
                            + "."
            );
        }

        return consultationCreeeAvecSucces;
    }
    
    public List<Consultation> consulterHistoriqueConsultations(Client client) {
        if (client == null) {
            return List.of();
        }
        return executeRead(() -> {
            Client clientReference = resolveClient(client);
            return clientReference == null ? List.of() : consultationDao.findByClient(clientReference);
        });
    }
    
    public Consultation consulterConsultationAffectee(Employe employe) {
        if (employe == null) {
            return null;
        }
        return executeRead(() -> {
            Employe employeReference = resolveEmploye(employe);
            if (employeReference == null) {
                return null;
            }
            List<Consultation> consultations = consultationDao.findEnCoursByEmploye(employeReference);
            return consultations.isEmpty() ? null : consultations.get(0);
        });
    }
    
    public void declarerPret(Consultation consultation) {
        if (consultation == null) {
            return;
        }
        Consultation consultationReference = executeRead(() -> resolveConsultation(consultation));
        if (consultationReference == null) {
            return;
        }
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM à hhhmm");
        Message.envoyerNotification(
                consultationReference.getClient().getTelephone(),
                "Bonjour " + safe(consultationReference.getClient().getPrenom()) + ". J'ai bien reçu votre demande de consultation du " + safe(consultationReference.getDate().format(pattern)) + ". Vous pouvez à présent me contacter au " + safe(consultationReference.getEmploye().getTelephone()) + ". A tout de suite !\r\nMédiumiquement vôtre, " + safe(consultationReference.getMedium().getGenre().getSuffix()) + " " + safe(consultationReference.getMedium().getDenomination())
        );
    }
    
    public void terminerConsultation(Consultation consultation, String commentaire) {
        if (consultation == null) {
            return;
        }
        executeInTransaction(() -> {
            Consultation consultationReference = resolveConsultation(consultation);
            if (consultationReference == null) {
                return null;
            }
            consultationReference.setCommentaire(commentaire == null ? "" : commentaire);
            consultationReference.setEstTermine(true);
            consultationDao.update(consultationReference);

            Employe employe = consultationReference.getEmploye();
            employe.setEstDisponible(true);
            employeDao.update(employe);
            return null;
        });
    }
    
    public Consultation recupererConsultationParId(Long id) {
        if (id == null) {
            return null;
        }
        return executeRead(() -> consultationDao.findById(id));
    }

    private Employe choisirEmployeLeMoinsCharge(List<Employe> employesDisponibles, List<Consultation> consultations) {
        Map<Long, Set<Long>> clientsParEmploye = new HashMap<>();
        for (Consultation consultation : consultations) {
            if (consultation.getEmploye() == null || consultation.getEmploye().getId() == null) {
                continue;
            }
            clientsParEmploye
                    .computeIfAbsent(consultation.getEmploye().getId(), ignored -> new HashSet<>())
                    .add(consultation.getClient().getId());
        }

        Employe meilleurEmploye = null;
        int plusPetitPortefeuille = Integer.MAX_VALUE;
        for (Employe employe : employesDisponibles) {
            int taillePortefeuille = clientsParEmploye.getOrDefault(employe.getId(), Set.of()).size();
            if (meilleurEmploye == null
                    || taillePortefeuille < plusPetitPortefeuille
                    || (taillePortefeuille == plusPetitPortefeuille
                    && safe(employe.getPrenom()).compareToIgnoreCase(safe(meilleurEmploye.getPrenom())) < 0)) {
                meilleurEmploye = employe;
                plusPetitPortefeuille = taillePortefeuille;
            }
        }
        return meilleurEmploye;
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

    private Medium resolveMedium(Medium medium) {
        return medium.getId() == null ? null : mediumDao.findById(medium.getId());
    }

    private Employe resolveEmploye(Employe employe) {
        return employe.getId() == null ? null : employeDao.findById(employe.getId());
    }

    private Consultation resolveConsultation(Consultation consultation) {
        return consultation.getId() == null ? null : consultationDao.findById(consultation.getId());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
