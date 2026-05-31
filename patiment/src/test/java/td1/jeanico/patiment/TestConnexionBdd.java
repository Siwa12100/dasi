package td1.jeanico.patiment;

import org.junit.jupiter.api.Test;
import td1.jeanico.patiment.daos.JpaUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestConnexionBdd {

    // Fonctionnalite metier testee: disponibilite de la connexion JDBC pour les tests d'integration BDD.
    // Comportement attendu: la connexion configuree pour les tests BDD est joignable avant toute suite d'integration.
    @Test
    void connexionBddDevDisponible() {
        SupportTestBdd.assumeDatabaseTestsEnabled();
        SupportTestBdd.configureJdbcProperties();

        assertTrue(JpaUtil.testerConnexionJdbc(),
                "La connexion a la BDD dev a echoue");
    }
}
