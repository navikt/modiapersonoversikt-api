package no.nav.sbl.dialogarena.sak.viewdomain.lamell;

import no.nav.modig.lang.option.Optional;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;

public class HentDokumentResultat {

    public enum Feilmelding {
        SIKKERHETSBEGRENSNING("vedlegg.feilmelding.joark.sikkerhetsbegrensning.head", "vedlegg.feilmelding.joark.sikkerhetsbegrensning.lead"),
        IKKE_JOURNALFORT("vedlegg.feilmelding.joark.ikkejournalfort.head", "vedlegg.feilmelding.joark.ikkejournalfort.lead"),
        FEILREGISTRERT("vedlegg.feilmelding.joark.feilregistrert.head", "vedlegg.feilmelding.joark.feilregistrert.lead"),
        IKKE_SAKSPART("vedlegg.feilmelding.ikkesakspart.head", "vedlegg.feilmelding.ikkesakspart.lead"),
        DOKUMENT_IKKE_FUNNET("vedlegg.feilmelding.joark.dokumentikkefunnet.head", "vedlegg.feilmelding.joark.dokumentikkefunnet.lead"),
        DOKUMENT_SLETTET("vedlegg.feilmelding.joark.dokumentslettet.head", "vedlegg.feilmelding.joark.dokumentslettet.lead");

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