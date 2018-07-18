package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain;



public enum Kommunikasjonsretning {
    INN, UT, INTERN;

    public static final String JOURNALPOST_INNGAAENDE = "I";
    public static final String JOURNALPOST_UTGAAENDE = "U";
    public static final String JOURNALPOST_INTERN = "N";

    public static Kommunikasjonsretning fraJournalpostretning(String kommunikasjonsretning) {
        if (kommunikasjonsretning.equals(JOURNALPOST_INNGAAENDE)) {
            return Kommunikasjonsretning.INN;
        } else if (kommunikasjonsretning.equals(JOURNALPOST_UTGAAENDE)){
            return Kommunikasjonsretning.UT;
        } else if (kommunikasjonsretning.equals(JOURNALPOST_INTERN)){
            return Kommunikasjonsretning.INTERN;
        } else {
            throw new IllegalArgumentException(String.format("Kommunikasjonsretningen fra Journalpost er ikke definert: %s",kommunikasjonsretning));
        }
    }
}
