package td1.jeanico.patiment.metier.service.support;

import java.util.function.Supplier;
import td1.jeanico.patiment.dao.JpaUtil;

public abstract class PersistenceSupport {

    protected <T> T executeRead(Supplier<T> action) {
        JpaUtil.creerContextePersistance();
        try {
            return action.get();
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }

    protected void executeRead(Runnable action) {
        executeRead(() -> {
            action.run();
            return null;
        });
    }

    protected <T> T executeInTransaction(Supplier<T> action) {
        JpaUtil.creerContextePersistance();
        try {
            JpaUtil.ouvrirTransaction();
            T result = action.get();
            JpaUtil.validerTransaction();
            return result;
        } catch (Exception ex) {
            JpaUtil.annulerTransaction();
            throw new RuntimeException("Erreur lors de l'execution du service", ex);
        } finally {
            JpaUtil.fermerContextePersistance();
        }
    }

    protected void executeInTransaction(Runnable action) {
        executeInTransaction(() -> {
            action.run();
            return null;
        });
    }
}
