package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain;

public enum Feilmelding {
    UKJENT_FEIL("feilmelding.ukjent"),
    DOKUMENT_IKKE_FUNNET("feilmelding.dokumentikkefunnet"),
    DOKUMENT_IKKE_TILGJENGELIG("feilmelding.dokumentikketilgjengelig"),
    DOKUMENT_SLETTET("feilmelding.dokumentslettet"),
    SIKKERHETSBEGRENSNING("feilmelding.sikkerhetsbegrensning"),
    MANGLER_DOKUMENTMETADATA("feilmelding.dokumentikkefunnet"),
    JOURNALFORT_ANNET_TEMA("feilmelding.journalfortannettema"),
    IKKE_JOURNALFORT_ELLER_ANNEN_BRUKER("feilmelding.journalfortfeil"),
    SAKSBEHANDLER_IKKE_TILGANG("feilmelding.saksbehandlerikketilgang"),
    TEMAKODE_ER_BIDRAG("feilmelding.sikkerhetsbegrensning"),
    KORRUPT_PDF("feilmelding.korruptpdf"),
    TEKNISK_FEIL("feilmelding.teknisk");

    public final String feilmeldingKey;

    Feilmelding(String feilmeldingKey) {
        this.feilmeldingKey = feilmeldingKey;
    }
}
