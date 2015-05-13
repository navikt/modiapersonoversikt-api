package no.nav.sbl.dialogarena.sak.tilgang;

public enum TilgangFeilmeldinger {
    UKJENT_FEIL("Systemfeil", "Det skjedde en ukjent systemfeil."),
    AKTOER_ID_IKKE_FUNNET("AktørId ikke funnet", "Fant ikke brukeren sin aktørId."),
    SAK_IKKE_FUNNET("Sak ikke funnet", "Fant ikke saken i GSak."),
    JOURNALPOST_IKKE_FUNNET("Journalpost ikke funnet", "Fant ikke journalposten i JOARK."),
    SIKKERHETSBEGRENSNING("Sikkerhetsbegrensning", "Du har ikke tilgang til dette vedlegget."),
    IKKE_JOURNALFORT("Ikke journalført", "Vedlegget er ikke journalført enda. Prøv igjen senere."),
    FEILREGISTRERT("Feilregistrert", "Vedlegget er feilregistrert."),
    IKKE_SAKSPART("Ikke sakspart", "Innsender er ikke sakspart.");

    private final String heading;
    private final String lead;

    TilgangFeilmeldinger(String heading, String lead) {
        this.heading = heading;
        this.lead = lead;
    }

    public String lead() {
        return lead;
    }

    public String heading() {
        return heading;
    }
}
