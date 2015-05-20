package no.nav.sbl.dialogarena.sak.viewdomain.lamell;

import no.nav.modig.lang.option.Optional;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;

public class HentDokumentResultat {

   public enum Feilmelding {
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

    public Optional<byte[]> pdfSomBytes;
    public boolean harTilgang;
    public Feilmelding feilmelding;

    public HentDokumentResultat(boolean harTilgang) {
        this.harTilgang = harTilgang;
    }

    public HentDokumentResultat(boolean harTilgang, Feilmelding feilmelding) {
        this.harTilgang = harTilgang;
        this.feilmelding = feilmelding;
        this.pdfSomBytes = none();
    }

    public HentDokumentResultat(boolean harTilgang, byte[] pdfSomBytes) {
        this.harTilgang = harTilgang;
        this.pdfSomBytes = optional(pdfSomBytes);
    }
}