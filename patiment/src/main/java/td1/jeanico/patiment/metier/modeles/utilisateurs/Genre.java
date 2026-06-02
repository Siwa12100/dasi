package td1.jeanico.patiment.metier.modeles.utilisateurs;

public enum Genre {
    HOMME("M"),
    FEMME("Mme"),
    NON_SPECIFIE("");

    private final String suffix;

    Genre(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
