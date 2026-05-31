package td1.jeanico.patiment.services;

import td1.jeanico.patiment.outils.SupportPersistance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.ConsultationDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.modeles.utilisateurs.Client;
import td1.jeanico.patiment.modeles.consultations.Consultation;
import td1.jeanico.patiment.modeles.utilisateurs.Employe;
import td1.jeanico.patiment.modeles.mediums.Medium;

public class StatistiqueService extends SupportPersistance {

    private final ConsultationDao consultationDao;
    private final MediumDao mediumDao;
    private final EmployeDao employeDao;
    private final ClientDao clientDao;

    /**
     * Constructeur par défaut.
     */
    public StatistiqueService() {
        this(new ConsultationDao(), new MediumDao(), new EmployeDao(), new ClientDao());
    }

    /**
     * Constructeur injectable.
     */
    public StatistiqueService(ConsultationDao consultationDao, MediumDao mediumDao, EmployeDao employeDao, ClientDao clientDao) {
        this.consultationDao = consultationDao;
        this.mediumDao = mediumDao;
        this.employeDao = employeDao;
        this.clientDao = clientDao;
    }
    
    /**
     * Compte le nombre de consultations par médium (zéros inclus).
     */
    public Map<Medium, Integer> listerNombreConsultationsParMedium() {
        return executerLecture(() -> {
            Map<Medium, Integer> resultat = new LinkedHashMap<>();
            for (Medium medium : mediumDao.listerParDenomination()) {
                resultat.put(medium, 0);
            }
            for (Consultation consultation : consultationDao.listerParDateDesc()) {
                resultat.merge(consultation.getMedium(), 1, Integer::sum);
            }
            return resultat;
        });
    }
    
    /**
     * Calcule le nombre de clients distincts suivis par employé.
     */
    public Map<Employe, Integer> listerRepartitionClientParEmploye() {
        return executerLecture(() -> {
            Map<Employe, Set<Long>> clientsDistinctsParEmploye = new LinkedHashMap<>();
            for (Employe employe : employeDao.listerParNomPrenom()) {
                clientsDistinctsParEmploye.put(employe, new LinkedHashSet<>());
            }
            for (Consultation consultation : consultationDao.listerParDateDesc()) {
                clientsDistinctsParEmploye
                        .computeIfAbsent(consultation.getEmploye(), ignored -> new LinkedHashSet<>())
                        .add(consultation.getClient().getId());
            }

            Map<Employe, Integer> resultat = new LinkedHashMap<>();
            for (Map.Entry<Employe, Set<Long>> entry : clientsDistinctsParEmploye.entrySet()) {
                resultat.put(entry.getKey(), entry.getValue().size());
            }
            return resultat;
        });
    }
    
    /**
     * Retourne les N médiums les plus demandés, triés par volume décroissant.
     */
    public List<Map<Medium, Integer>> listerMediumsPopulaire(int nbMediums) {
        if (nbMediums <= 0) {
            return List.of();
        }
        return executerLecture(() -> {
            Map<Medium, Integer> compteurs = listerNombreConsultationsParMedium();
            List<Map.Entry<Medium, Integer>> entrees = new ArrayList<>(compteurs.entrySet());
            // Tri secondaire alpha pour stabiliser les égalités.
            entrees.sort(Comparator
                    .comparing((Map.Entry<Medium, Integer> entry) -> entry.getValue(), Comparator.reverseOrder())
                    .thenComparing(entry -> securiser(entry.getKey().getDenomination()), String.CASE_INSENSITIVE_ORDER));

            List<Map<Medium, Integer>> resultat = new ArrayList<>();
            for (int index = 0; index < Math.min(nbMediums, entrees.size()); index++) {
                Map.Entry<Medium, Integer> entry = entrees.get(index);
                Map<Medium, Integer> ligne = new LinkedHashMap<>();
                ligne.put(entry.getKey(), entry.getValue());
                resultat.add(ligne);
            }
            return resultat;
        });
    }
    
    /**
     * Retourne la répartition des clients par code département.
     */
    public Map<String, Integer> listerRepartitionGeographiqueClients() {
        return executerLecture(() -> {
            Map<String, Integer> resultat = new TreeMap<>();
            for (Client client : clientDao.listerParNomPrenom()) {
                String codeDepartement = client.getAdresse() == null ? null : client.getAdresse().getCodeDepartement();
                String cle = (codeDepartement == null || codeDepartement.isBlank()) ? "INCONNU" : codeDepartement;
                resultat.merge(cle, 1, Integer::sum);
            }
            return new LinkedHashMap<>(resultat);
        });
    }

    /**
     * Sécurise les concaténations de texte.
     */
    private String securiser(String value) {
        return value == null ? "" : value;
    }
}
