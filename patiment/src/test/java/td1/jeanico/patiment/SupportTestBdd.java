package td1.jeanico.patiment;

import org.junit.jupiter.api.Assumptions;
import td1.jeanico.patiment.daos.JpaUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class SupportTestBdd {

    private static final String DEFAULT_JDBC_URL = "jdbc:mysql://dasi-mariadb:3306/DASI-DB?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String DEFAULT_JDBC_USER = "dasi";
    private static final String DEFAULT_JDBC_PASSWORD = "dasi";
    private static final String DEFAULT_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private SupportTestBdd() {
    }

    static void assumeDatabaseTestsEnabled() {
        Assumptions.assumeTrue(Boolean.getBoolean("patiment.runDbTests"),
                "Tests d'integration BDD desactives. Utiliser -Dpatiment.runDbTests=true");
    }

    static void configureJdbcProperties() {
        setIfBlank(JpaUtil.PROPRIETE_URL_JDBC, DEFAULT_JDBC_URL);
        setIfBlank(JpaUtil.PROPRIETE_UTILISATEUR_JDBC, DEFAULT_JDBC_USER);
        setIfBlank(JpaUtil.PROPRIETE_MOT_DE_PASSE_JDBC, DEFAULT_JDBC_PASSWORD);
        setIfBlank(JpaUtil.PROPRIETE_PILOTE_JDBC, DEFAULT_JDBC_DRIVER);
    }

    static void assertJdbcConnectionAvailable() {
        assertTrue(JpaUtil.testerConnexionJdbc(),
                "La connexion JDBC de test a echoue");
    }

    private static void setIfBlank(String key, String value) {
        String currentValue = System.getProperty(key);
        if (currentValue == null || currentValue.isBlank()) {
            System.setProperty(key, value);
        }
    }
}