package td1.jeanico.patiment.metier.service.client;

import td1.jeanico.patiment.metier.modele.Client;
import td1.jeanico.patiment.metier.modele.ProfilAstral;

public interface IClientService {

    boolean inscrire(Client client);

    Client authentifier(String mail, String motDePasse);

    ProfilAstral consulterProfilAstral(Client client);

    Client consulterProfilClient(Client client);

    Client recupererClientParId(Long id);
}
