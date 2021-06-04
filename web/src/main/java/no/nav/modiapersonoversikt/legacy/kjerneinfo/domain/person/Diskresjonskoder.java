package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person;

public enum Diskresjonskoder {
    STRENGT_FORTROLIG_ADRESSE("SPSF", "6"), FORTROLIG_ADRESSE("SPFO", "7");

    private final String value;
    private final String beskrivelse;

    Diskresjonskoder(String value, String beskrivelse) {
        this.value = value;
        this.beskrivelse = beskrivelse;
    }

    public String getValue() {
        return value;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }
}
