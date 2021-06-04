package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain;



public enum Kommunikasjonsretning {
    INN, UT, INTERN;

    public static final String JOURNALPOST_INNGAAENDE = "I";
    public static final String JOURNALPOST_UTGAAENDE = "U";
    public static final String JOURNALPOST_INTERN = "N";

    public static Kommunikasjonsretning fraJournalpostretning(String kommunikasjonsretning) {
        switch (kommunikasjonsretning) {
            case JOURNALPOST_INNGAAENDE:
                return Kommunikasjonsretning.INN;
            case JOURNALPOST_UTGAAENDE:
                return Kommunikasjonsretning.UT;
            case JOURNALPOST_INTERN:
                return Kommunikasjonsretning.INTERN;
            default:
                throw new IllegalArgumentException(String.format("Kommunikasjonsretningen fra Journalpost er ikke definert: %s", kommunikasjonsretning));
        }
    }
}
