package td1.jeanico.patiment.metier.service.consultation;

import java.util.List;
import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.modele.Consultation;
import td1.jeanico.patiment.metier.modele.Employe;
import td1.jeanico.patiment.metier.modele.Medium;

public interface IConsultationService {

    boolean demanderConsultation(Client client, Medium medium);

    List<Consultation> consulterHistoriqueConsultations(Client client);

    Consultation consulterConsultationAffectee(Employe employe);

    void declarerPret(Consultation consultation);

    void terminerConsultation(Consultation consultation, String commentaire);

    Consultation recupererConsultationParId(Long id);
}
