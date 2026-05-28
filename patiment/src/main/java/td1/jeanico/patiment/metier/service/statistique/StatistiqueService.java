package td1.jeanico.patiment.metier.service.statistique;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import td1.jeanico.patiment.dao.ClientDao;
import td1.jeanico.patiment.dao.ConsultationDao;
import td1.jeanico.patiment.dao.EmployeDao;
import td1.jeanico.patiment.dao.MediumDao;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.modele.Consultation;
import td1.jeanico.patiment.metier.modele.Employe;
import td1.jeanico.patiment.metier.modele.Medium;
import td1.jeanico.patiment.metier.service.support.PersistenceSupport;

public class StatistiqueService extends PersistenceSupport implements IStatistiqueService {

    private final ConsultationDao consultationDao;
    private final MediumDao mediumDao;
    private final EmployeDao employeDao;
    private final ClientDao clientDao;

    public StatistiqueService() {
        this(new ConsultationDao(), new MediumDao(), new EmployeDao(), new ClientDao());
    }

    public StatistiqueService(ConsultationDao consultationDao, MediumDao mediumDao, EmployeDao employeDao, ClientDao clientDao) {
        this.consultationDao = consultationDao;
        this.mediumDao = mediumDao;
        this.employeDao = employeDao;
        this.clientDao = clientDao;
    }

    @Override
    public Map<Medium, Integer> listerNombreConsultationsParMedium() {
        return executeRead(() -> {
            Map<Medium, Integer> resultat = new LinkedHashMap<>();
            for (Medium medium : mediumDao.findAllOrderedByDenomination()) {
                resultat.put(medium, 0);
            }
            for (Consultation consultation : consultationDao.findAllOrderedByDateDesc()) {
                resultat.merge(consultation.getMedium(), 1, Integer::sum);
            }
            return resultat;
        });
    }

    @Override
    public Map<Employe, Integer> listerRepartitionClientParEmploye() {
        return executeRead(() -> {
            Map<Employe, Set<Long>> clientsDistinctsParEmploye = new LinkedHashMap<>();
            for (Employe employe : employeDao.findAllOrderedByPrenom()) {
                clientsDistinctsParEmploye.put(employe, new LinkedHashSet<>());
            }
            for (Consultation consultation : consultationDao.findAllOrderedByDateDesc()) {
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

    @Override
    public List<Map<Medium, Integer>> listerMediumsPopulaire(int nbMediums) {
        if (nbMediums <= 0) {
            return List.of();
        }
        return executeRead(() -> {
            Map<Medium, Integer> compteurs = listerNombreConsultationsParMedium();
            List<Map.Entry<Medium, Integer>> entrees = new ArrayList<>(compteurs.entrySet());
            entrees.sort(Comparator
                    .comparing((Map.Entry<Medium, Integer> entry) -> entry.getValue(), Comparator.reverseOrder())
                    .thenComparing(entry -> safe(entry.getKey().getDenomination()), String.CASE_INSENSITIVE_ORDER));

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

    @Override
    public Map<String, Integer> listerRepartitionGeographiqueClients() {
        return executeRead(() -> {
            Map<String, Integer> resultat = new TreeMap<>();
            for (Client client : clientDao.findAllOrderedByNomPrenom()) {
                String codeDepartement = client.getAdresse() == null ? null : client.getAdresse().getCodeDepartement();
                String cle = (codeDepartement == null || codeDepartement.isBlank()) ? "INCONNU" : codeDepartement;
                resultat.merge(cle, 1, Integer::sum);
            }
            return new LinkedHashMap<>(resultat);
        });
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
