package td1.jeanico.patiment.metier.services;

import td1.jeanico.patiment.outils.SupportPersistance;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import td1.jeanico.patiment.daos.ClientDao;
import td1.jeanico.patiment.daos.ConsultationDao;
import td1.jeanico.patiment.daos.EmployeDao;
import td1.jeanico.patiment.daos.MediumDao;
import td1.jeanico.patiment.metier.modeles.consultations.Consultation;
import td1.jeanico.patiment.metier.modeles.mediums.Medium;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Client;
import td1.jeanico.patiment.metier.modeles.utilisateurs.Employe;

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
     * @param consultationDao
     * @param mediumDao
     * @param employeDao
     * @param clientDao
     */
    public StatistiqueService(ConsultationDao consultationDao, MediumDao mediumDao, EmployeDao employeDao, ClientDao clientDao) {
        this.consultationDao = consultationDao;
        this.mediumDao = mediumDao;
        this.employeDao = employeDao;
        this.clientDao = clientDao;
    }
    
    /**
     * Compte le nombre de consultations par médium (zéros inclus).
     * @return 
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
     * @return 
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
     * @param nbMediums
     * @return 
     */
    public List<Map<Medium, Integer>> listerMediumsPopulaire(int nbMediums) {
        // Garde-fou: une demande nulle ou négative renvoie une liste vide.
        if (nbMediums <= 0) {
            return List.of();
        }
        // On trie d'abord par nombre de consultations (descendant),
        // puis par dénomination pour obtenir un ordre déterministe en cas d'égalité.
        Comparator<Map.Entry<Medium, Integer>> comparateur = Comparator
                .comparing((Map.Entry<Medium, Integer> entry) -> entry.getValue(), Comparator.reverseOrder())
                .thenComparing(entry -> securiser(entry.getKey().getDenomination()), String.CASE_INSENSITIVE_ORDER);

        // Pipeline: tri -> limite aux N premiers -> format attendu (une map par ligne).
        return listerNombreConsultationsParMedium().entrySet().stream()
                .sorted(comparateur)
                .limit(nbMediums)
                .map(entry -> {
                    // Le format de sortie historique est une liste de maps unitaires.
                    Map<Medium, Integer> ligne = new HashMap<>();
                    ligne.put(entry.getKey(), entry.getValue());
                    return ligne;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Retourne la répartition des clients par code département.
     * @return 
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
