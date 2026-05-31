package td1.jeanico.patiment.services;

import td1.jeanico.patiment.outils.SupportPersistance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.ConsultationDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.consultations.Consultation;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.modeles.utilisateurs.Genre;
import td1.jeanico.patiment.modeles.mediums.Medium;
import td1.jeanico.patiment.outils.Message;

public class ConsultationService extends SupportPersistance {

    private final ConsultationDao consultationDao;
    private final ClientDao clientDao;
    private final MediumDao mediumDao;
    private final EmployeDao employeDao;

    /**
     * Constructeur par défaut.
     */
    public ConsultationService() {
        this(new ConsultationDao(), new ClientDao(), new MediumDao(), new EmployeDao());
    }

    /**
     * Constructeur injectable pour tests/intégration.
     */
    public ConsultationService(ConsultationDao consultationDao, ClientDao clientDao, MediumDao mediumDao, EmployeDao employeDao) {
        this.consultationDao = consultationDao;
        this.clientDao = clientDao;
        this.mediumDao = mediumDao;
        this.employeDao = employeDao;
    }
    
    /**
     * Crée une consultation en affectant un employé compatible et disponible.
     * L'affectation privilégie l'employé au portefeuille le plus faible.
     */
    public boolean demanderConsultation(Client client, Medium medium) {
        if (client == null || medium == null) {
            return false;
        }

        Employe[] employeSelectionne = new Employe[1];
        Consultation[] consultationCreee = new Consultation[1];
        Client[] clientPersistant = new Client[1];
        Medium[] mediumPersistant = new Medium[1];

        boolean consultationCreeeAvecSucces = executerEnTransaction(() -> {
            Client clientReference = resoudreClient(client);
            Medium mediumReference = resoudreMedium(medium);
            if (clientReference == null || mediumReference == null) {
                return false;
            }

            // Compatibilité de genre + disponibilité avant toute affectation.
            List<Employe> employesDisponibles = filtrerEmployesEligibles(
                    employeDao.listerDisponiblesParNomPrenom(),
                    mediumReference
            );
            if (employesDisponibles.isEmpty()) {
                return false;
            }

            Employe employe = choisirEmployeLeMoinsCharge(employesDisponibles, consultationDao.listerParDateDesc());
            if (employe == null) {
                return false;
            }

            // L'employé est verrouillé comme indisponible dès la création.
            employe.setEstDisponible(false);
            employeDao.mettreAJour(employe);

            Consultation consultation = new Consultation("", LocalDateTime.now(), false, clientReference, employe, mediumReference);
            consultationDao.creer(consultation);

            employeSelectionne[0] = employe;
            consultationCreee[0] = consultation;
            clientPersistant[0] = clientReference;
            mediumPersistant[0] = mediumReference;
            return true;
        });

        if (consultationCreeeAvecSucces) {
            Message.envoyerNotification(
                    employeSelectionne[0].getTelephone(),
                "Bonjour " + securiser(employeSelectionne[0].getPrenom()) + ". Consultation requise pour "
                    + formatNomComplet(clientPersistant[0])
                    + ". Medium a incarner : "
                    + securiser(mediumPersistant[0].getDenomination())
            );
        }

        return consultationCreeeAvecSucces;
    }
    
    /**
     * Retourne l'historique des consultations d'un client (ordre décroissant par date).
     */
    public List<Consultation> consulterHistoriqueConsultations(Client client) {
        if (client == null) {
            return List.of();
        }
        return executerLecture(() -> {
            Client clientReference = resoudreClient(client);
            return clientReference == null ? List.of() : consultationDao.trouverParClient(clientReference);
        });
    }
    
    /**
     * Retourne la consultation active affectée à un employé.
     */
    public Consultation consulterConsultationAffectee(Employe employe) {
        if (employe == null) {
            return null;
        }
        return executerLecture(() -> {
            Employe employeReference = resoudreEmploye(employe);
            if (employeReference == null) {
                return null;
            }
            List<Consultation> consultations = consultationDao.trouverEnCoursParEmploye(employeReference);
            return consultations.isEmpty() ? null : consultations.get(0);
        });
    }
    
    /**
     * Envoie au client une notification indiquant que le médium est prêt.
     */
    public void declarerPret(Consultation consultation) {
        if (consultation == null) {
            return;
        }
        Consultation consultationReference = executerLecture(() -> resoudreConsultation(consultation));
        if (consultationReference == null || consultationReference.isEstTermine()) {
            return;
        }
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a' HH'h'mm");
        Message.envoyerNotification(
                consultationReference.getClient().getTelephone(),
            "Bonjour " + securiser(consultationReference.getClient().getPrenom()) + ". J'ai bien recu votre demande de consultation du "
                + securiser(consultationReference.getDate().format(pattern))
                + ". Vous pouvez des a present me contacter au "
                + securiser(consultationReference.getEmploye().getTelephone())
                + ". A tout de suite !\r\nMediumiquement votre, "
                + securiser(consultationReference.getMedium().getDenomination())
        );
    }
    
    /**
     * Termine une consultation et libère immédiatement l'employé affecté.
     */
    public void terminerConsultation(Consultation consultation, String commentaire) {
        if (consultation == null) {
            return;
        }
        executerEnTransaction(() -> {
            Consultation consultationReference = resoudreConsultation(consultation);
            if (consultationReference == null || consultationReference.isEstTermine()) {
                return null;
            }
            // Clôture de la consultation.
            consultationReference.setCommentaire(commentaire == null ? "" : commentaire);
            consultationReference.setEstTermine(true);
            consultationDao.mettreAJour(consultationReference);

            // Le même employé redevient disponible pour une future demande.
            Employe employe = consultationReference.getEmploye();
            employe.setEstDisponible(true);
            employeDao.mettreAJour(employe);
            return null;
        });
    }
    
    /**
     * Recherche une consultation par son identifiant.
     */
    public Consultation recupererConsultationParId(Long id) {
        if (id == null) {
            return null;
        }
        return executerLecture(() -> consultationDao.trouverParId(id));
    }

    /**
     * Choisit l'employé avec le moins de consultations, puis ordre alpha en cas d'égalité.
     */
    private Employe choisirEmployeLeMoinsCharge(List<Employe> employesDisponibles, List<Consultation> consultations) {
        Map<Long, Integer> consultationsParEmploye = new HashMap<>();
        for (Consultation consultation : consultations) {
            if (consultation.getEmploye() == null || consultation.getEmploye().getId() == null) {
                continue;
            }
            consultationsParEmploye.merge(consultation.getEmploye().getId(), 1, Integer::sum);
        }

        Employe meilleurEmploye = null;
        int plusPetitPortefeuille = Integer.MAX_VALUE;
        for (Employe employe : employesDisponibles) {
            int taillePortefeuille = consultationsParEmploye.getOrDefault(employe.getId(), 0);
            if (meilleurEmploye == null
                    || taillePortefeuille < plusPetitPortefeuille
                    || (taillePortefeuille == plusPetitPortefeuille && comparerEmployes(employe, meilleurEmploye) < 0)) {
                meilleurEmploye = employe;
                plusPetitPortefeuille = taillePortefeuille;
            }
        }
        return meilleurEmploye;
    }

    /**
     * Filtre les employés compatibles avec le genre du médium.
     */
    private List<Employe> filtrerEmployesEligibles(List<Employe> employesDisponibles, Medium medium) {
        List<Employe> employesEligibles = new ArrayList<>();
        for (Employe employe : employesDisponibles) {
            if (estGenreCompatible(employe, medium)) {
                employesEligibles.add(employe);
            }
        }
        return employesEligibles;
    }

    /**
     * Vérifie la compatibilité de genre entre employé et médium.
     */
    private boolean estGenreCompatible(Employe employe, Medium medium) {
        Genre genreMedium = medium == null ? null : medium.getGenre();
        if (genreMedium == null || genreMedium == Genre.NON_SPECIFIE) {
            return true;
        }
        return employe != null && employe.getGenre() == genreMedium;
    }

    /**
     * Comparator métier sur nom/prénom.
     */
    private int comparerEmployes(Employe premier, Employe second) {
        int comparaisonNom = securiser(premier.getNom()).compareToIgnoreCase(securiser(second.getNom()));
        if (comparaisonNom != 0) {
            return comparaisonNom;
        }
        return securiser(premier.getPrenom()).compareToIgnoreCase(securiser(second.getPrenom()));
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
     * Résout un client à partir de son identifiant ou de son mail.
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
     * Résout un médium persisté à partir de son id.
     */
    private Medium resoudreMedium(Medium medium) {
        return medium.getId() == null ? null : mediumDao.trouverParId(medium.getId());
    }

    /**
     * Résout un employé persisté à partir de son id.
     */
    private Employe resoudreEmploye(Employe employe) {
        return employe.getId() == null ? null : employeDao.trouverParId(employe.getId());
    }

    /**
     * Résout une consultation persistée à partir de son id.
     */
    private Consultation resoudreConsultation(Consultation consultation) {
        return consultation.getId() == null ? null : consultationDao.trouverParId(consultation.getId());
    }

    /**
     * Indique si une chaîne est nulle ou vide.
     */
    private boolean estVide(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Sécurise les concaténations de texte.
     */
    private String securiser(String value) {
        return value == null ? "" : value;
    }
}
