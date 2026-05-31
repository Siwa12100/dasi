package td1.jeanico.patiment.outils;

import java.util.function.Supplier;
import td1.jeanico.patiment.daos.JpaUtil;

public abstract class SupportPersistance {

    /**
     * Exécute une action en lecture seule avec un contexte de persistance.
     * Si un contexte existe déjà (appel imbriqué), il est réutilisé.
     */
    protected <T> T executerLecture(Supplier<T> action) {
        boolean contexteExistant = JpaUtil.contextePersistanceDisponible();
        if (!contexteExistant) {
            JpaUtil.creerContextePersistance();
        }
        try {
            return action.get();
        } finally {
            if (!contexteExistant) {
                JpaUtil.fermerContextePersistance();
            }
        }
    }

    /**
     * Variante utilitaire pour les lectures sans valeur de retour.
     */
    protected void executerLecture(Runnable action) {
        executerLecture(() -> {
            action.run();
            return null;
        });
    }

    /**
     * Exécute une action transactionnelle (begin/commit/rollback).
     * En cas d'erreur, la transaction est annulée puis l'exception remontée.
     */
    protected <T> T executerEnTransaction(Supplier<T> action) {
        boolean contexteExistant = JpaUtil.contextePersistanceDisponible();
        if (!contexteExistant) {
            JpaUtil.creerContextePersistance();
        }
        try {
            JpaUtil.ouvrirTransaction();
            T resultat = action.get();
            JpaUtil.validerTransaction();
            return resultat;
        } catch (Exception ex) {
            // Toute erreur métier ou technique annule explicitement la transaction.
            JpaUtil.annulerTransaction();
            throw new RuntimeException("Erreur lors de l'execution du service", ex);
        } finally {
            if (!contexteExistant) {
                JpaUtil.fermerContextePersistance();
            }
        }
    }

    /**
     * Variante utilitaire pour les transactions sans valeur de retour.
     */
    protected void executerEnTransaction(Runnable action) {
        executerEnTransaction(() -> {
            action.run();
            return null;
        });
    }
}
