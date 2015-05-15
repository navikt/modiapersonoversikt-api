package no.nav.sbl.dialogarena.sak.viewdomain.lamell;

public class VedleggResultat {

   public enum Feilmelding {
        UKJENT_FEIL("Systemfeil", "Det skjedde en ukjent systemfeil."),
        AKTOER_ID_IKKE_FUNNET("AktørId ikke funnet", "Fant ikke brukeren sin aktørId."),
        SAK_IKKE_FUNNET("Sak ikke funnet", "Fant ikke saken i GSak."),
        JOURNALPOST_IKKE_FUNNET("Journalpost ikke funnet", "Fant ikke journalposten i JOARK."),
        SIKKERHETSBEGRENSNING("Sikkerhetsbegrensning", "Du har ikke tilgang til dette vedlegget."),
        IKKE_JOURNALFORT("Ikke journalført", "Vedlegget er ikke journalført enda. Prøv igjen senere."),
        FEILREGISTRERT("Feilregistrert", "Vedlegget er feilregistrert."),
        IKKE_SAKSPART("Ikke sakspart", "Innsender er ikke sakspart."),
        DOKUMENT_IKKE_FUNNET("Dokument ikke funnet", "Dokumentet ble ikke funnet"),
        DOKUMENT_SLETTET("Dokument slettet", "Vedlegget er slettet");

        public final String heading;
        public final String lead;

        Feilmelding(String heading, String lead) {
            this.heading = heading;
            this.lead = lead;
        }
    }

    public byte[] pdfSomBytes;
    public boolean harTilgang;
    public Feilmelding feilmelding;

    public VedleggResultat(boolean harTilgang) {
        this.harTilgang = harTilgang;
    }

    public VedleggResultat(boolean harTilgang, Feilmelding feilmelding) {
        this.harTilgang = harTilgang;
        this.feilmelding = feilmelding;
    }

    public VedleggResultat(boolean harTilgang, byte[] pdfSomBytes) {
        this.harTilgang = harTilgang;
        this.pdfSomBytes = pdfSomBytes;
    }

    public boolean eksisterer() {
        return pdfSomBytes.length > 0;
    }
}