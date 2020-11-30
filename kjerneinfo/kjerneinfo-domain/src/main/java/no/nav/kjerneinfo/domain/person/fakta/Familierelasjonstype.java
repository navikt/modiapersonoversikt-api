package no.nav.kjerneinfo.domain.person.fakta;

public enum Familierelasjonstype {

    ADOPTIVFAR("adoptivfar"),
    ADOPTIVMOR("adoptivmor"),
    FARA("far"),
    FOSTERFAR("fosterfar"),
    FOSTERMOR("fostermor"),
    GIFT("gift"),
    MEDMOR("medmor"),
    MORA("mor"),
    PARTNER("partner"),
    SAMBOER("samboer"),
    BARN("barn"),
    EKTE("ekte");
    private final String value;

    Familierelasjonstype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        String familierelasjonsType = super.toString();
        return familierelasjonsType.substring(0, 1) + familierelasjonsType.substring(1).toLowerCase();
    }

    public static Familierelasjonstype fromValue(String v) {
        for (Familierelasjonstype c : Familierelasjonstype.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
