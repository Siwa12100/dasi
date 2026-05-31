package td1.jeanico.patiment.modeles.utilisateurs;

public enum Genre {
    H {
        @Override
        public String getSuffix() {
            return "M";
        }
    },
    F {
        @Override
        public String getSuffix() {
            return "Mme";
        }
    },
    NON_SPECIFIE {
        @Override
        public String getSuffix() {
            return "";
        }
    };
    
    public abstract String getSuffix();
}
