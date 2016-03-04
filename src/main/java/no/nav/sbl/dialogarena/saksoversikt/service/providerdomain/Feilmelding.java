package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain;

public enum Feilmelding {
    UKJENT_FEIL("feilmelding.ukjent"),
    DOKUMENT_IKKE_FUNNET("feilmelding.dokumentikkefunnet"),
    DOKUMENT_IKKE_TILGJENGELIG("feilmelding.dokumentikketilgjengelig"),
    DOKUMENT_SLETTET("feilmelding.dokumentslettet"),
    SIKKERHETSBEGRENSNING("feilmelding.sikkerhetsbegrensning"),
    JOURNALFORT_ANNET_TEMA("feilmelding.journalfortannettema"),
    KORRUPT_PDF("feilmelding.korruptpdf"),
    SAKSBEHANDLER_IKKE_TILGANG("feilmelding.saksbehandlerikketilgang"),
    TEMAKODE_ER_BIDRAG("feilmelding.temakode.bidrag");

    public final String feilmeldingKey;

    Feilmelding(String feilmeldingKey) {
        this.feilmeldingKey = feilmeldingKey;
    }
}
