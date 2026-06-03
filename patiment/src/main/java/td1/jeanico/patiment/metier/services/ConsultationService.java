package td1.jeanico.patiment.metier.services;

import td1.jeanico.patiment.outils.SupportPersistance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import td1.jeanico.patiment.daos.ConsultationDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.metier.modeles.consultations.Consultation;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.outils.Message;

public class ConsultationService extends SupportPersistance {

    private final ConsultationDao consultationDao;
    private final EmployeDao employeDao;

    
    public ConsultationService() {
        this(new ConsultationDao(), new EmployeDao());
    }

    /**
     * Constructeur injectable pour tests/intégration.
     * @param consultationDao
     * @param employeDao
     */
    public ConsultationService(ConsultationDao consultationDao, EmployeDao employeDao) {
        this.consultationDao = consultationDao;
        this.employeDao = employeDao;
    }
    
    /**
     * Crée une consultation en affectant un employé compatible et disponible.
     * L'affectation privilégie l'employé au portefeuille le plus faible.
     * @param client
     * @param medium
     * @return 
     */
    public Consultation demanderConsultation(Client client, Medium medium) {
        if (client == null || medium == null) {
            return null;
        }

        Consultation consultationAffecte = executerEnTransaction(() -> {
            Employe employe = employeDao.trouverEmployeCompatible(medium.getGenre());
            if (employe == null) {
                return null;
            }

            // L'employé est verrouillé comme indisponible dès la création.
            employe.setEstDisponible(false);
            employeDao.mettreAJour(employe);

            Consultation consultation = new Consultation("", LocalDateTime.now(), false, client, employe, medium);
            consultationDao.creer(consultation);
            return consultation;
        });

        if (consultationAffecte == null || consultationAffecte.getEmploye() == null) {
            return null;
        }

        Message.envoyerNotification(
                consultationAffecte.getEmploye().getTelephone(),
                "Bonjour " + securiser(consultationAffecte.getEmploye().getPrenom()) + ". Consultation requise pour "
                + formatNomComplet(client)
                + ". Medium a incarner : "
                + securiser(medium.getDenomination())
        );

        return consultationAffecte;
    }
    
    /**
     * Retourne l'historique des consultations d'un client (ordre décroissant par date).
     * @param client
     * @return 
     */
    public List<Consultation> consulterHistoriqueConsultations(Client client) {
        if (client == null) {
            return List.of();
        }
        return executerLecture(() -> {
            return consultationDao.trouverParClient(client);
        });
    }
    
    /**
     * Retourne la consultation active affectée à un employé.
     * @param employe
     * @return 
     */
    public Consultation consulterConsultationAffectee(Employe employe) {
        return executerLecture(() -> {
            List<Consultation> consultations = consultationDao.trouverEnCoursParEmploye(employe);
            return consultations.isEmpty() ? null : consultations.get(0);
        });
    }
    
    /**
     * Envoie au client une notification indiquant que le médium est prêt.
     * @param consultation
     */
    public void declarerPret(Consultation consultation) {
        if (consultation == null  || consultation.isEstTermine()) {
            return;
        }
        
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a' HH'h'mm");
        Message.envoyerNotification(
                consultation.getClient().getTelephone(),
            "Bonjour " + securiser(consultation.getClient().getPrenom()) + ". J'ai bien recu votre demande de consultation du "
                + securiser(consultation.getDate().format(pattern))
                + ". Vous pouvez des a present me contacter au "
                + securiser(consultation.getEmploye().getTelephone())
                + ". A tout de suite !\r\nMediumiquement votre, "
                + securiser(consultation.getMedium().getDenomination())
        );
    }
    
    /**
     * Termine une consultation et libère immédiatement l'employé affecté.
     * @param consultation
     * @param commentaire
     */
    public void terminerConsultation(Consultation consultation, String commentaire) {
        if (consultation == null || consultation.isEstTermine()) {
            return;
        }
        executerEnTransaction(() -> {
            // Clôture de la consultation.
            consultation.setCommentaire(commentaire == null ? "" : commentaire);
            consultation.setEstTermine(true);
            consultationDao.mettreAJour(consultation);

            // Le même employé redevient disponible pour une future demande.
            Employe employe = consultation.getEmploye();
            employe.setEstDisponible(true);
            employeDao.mettreAJour(employe);
        });
    }
    
    /**
     * Recherche une consultation par son identifiant.
     * @param id
     * @return 
     */
    public Consultation recupererConsultationParId(Long id) {
        if (id == null) {
            return null;
        }
        return executerLecture(() -> consultationDao.trouverParId(id));
    }

    /**
     * Formate l'identité client pour les messages sortants.
     */
    private String formatNomComplet(Client client) {
        String civilite = client.getGenre() == null ? "" : client.getGenre().getSuffix();
        String identite = (securiser(client.getPrenom()) + " " + securiser(client.getNom()).toUpperCase()).trim();
        if (civilite.isBlank()) {
            return identite;
        }
        return (civilite + " " + identite).trim();
    }
    
    /**
     * Sécurise les concaténations de texte.
     */
    private String securiser(String value) {
        return value == null ? "" : value;
    }
}
