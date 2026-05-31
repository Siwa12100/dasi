package td1.jeanico.patiment;

import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import javax.persistence.EntityManager;
import td1.jeanico.patiment.daos.JpaUtil;

abstract class TestIntegrationAbstrait {

    @BeforeEach
    void setUpDatabaseTestContext() {
        SupportTestBdd.assumeDatabaseTestsEnabled();
        SupportTestBdd.configureJdbcProperties();
        SupportTestBdd.assertJdbcConnectionAvailable();
        JpaUtil.desactiverLog();
        JpaUtil.fermerFabriquePersistance();
        JpaUtil.creerFabriquePersistance();
        nettoyerBaseDeTest();
    }

    @AfterEach
    void tearDownDatabaseTestContext() {
        JpaUtil.fermerFabriquePersistance();
    }

    protected <T> T executerEnTransaction(Supplier<T> action) {
        JpaUtil.creerContextePersistance();
        try {
            JpaUtil.ouvrirTransaction();
            T result = action.get();
            JpaUtil.validerTransaction();
            return result;
        } catch (Exception ex) {
            JpaUtil.annulerTransaction();
            throw new IllegalStateException("Erreur lors de l'execution d'un test d'integration", ex);
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }

    protected <T> T executerLecture(Supplier<T> action) {
        JpaUtil.creerContextePersistance();
        try {
            return action.get();
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }

    private void nettoyerBaseDeTest() {
        JpaUtil.creerContextePersistance();
        try {
            JpaUtil.ouvrirTransaction();
            EntityManager entityManager = JpaUtil.obtenirContextePersistancePourTests();
            for (String table : List.of(
                    "CONSULTATION",
                    "SPIRITE",
                    "CARTOMANCIEN",
                    "ASTROLOGUE",
                    "EMPLOYE",
                    "CLIENT",
                    "UTILISATEUR"
            )) {
                entityManager.createNativeQuery("DELETE FROM " + table).executeUpdate();
            }
            JpaUtil.validerTransaction();
        } catch (Exception ex) {
            JpaUtil.annulerTransaction();
            throw new IllegalStateException("Impossible de nettoyer la base de test", ex);
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }
}