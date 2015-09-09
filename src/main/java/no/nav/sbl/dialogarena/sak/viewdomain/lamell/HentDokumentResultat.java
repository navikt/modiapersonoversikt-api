package no.nav.sbl.dialogarena.sak.viewdomain.lamell;

import no.nav.modig.lang.option.Optional;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;

public class HentDokumentResultat {

    public enum Feilmelding {
        SIKKERHETSBEGRENSNING("vedlegg.feilmelding.joark.sikkerhetsbegrensning.head", "vedlegg.feilmelding.joark.sikkerhetsbegrensning.lead"),
        IKKE_JOURNALFORT("vedlegg.feilmelding.joark.ikkejournalfort.head", "vedlegg.feilmelding.joark.ikkejournalfort.lead"),
        STATUS_UTGAAR("vedlegg.feilmelding.joark.statusutgaar.head", "vedlegg.feilmelding.joark.statusutgaar.lead"),
        UKJENT_BRUKER("vedlegg.feilmelding.joark.ukjentbruker.head", "vedlegg.feilmelding.joark.ukjentbruker.lead"),
        FEILREGISTRERT("vedlegg.feilmelding.joark.feilregistrert.head", "vedlegg.feilmelding.joark.feilregistrert.lead"),
        IKKE_SAKSPART("vedlegg.feilmelding.ikkesakspart.head", "vedlegg.feilmelding.ikkesakspart.lead"),
        INGEN_TILGANG("vedlegg.feilmelding.ikketilgang.head", "vedlegg.feilmelding.ikketilgang.lead"),
        DOKUMENT_IKKE_FUNNET("vedlegg.feilmelding.joark.dokumentikkefunnet.head", "vedlegg.feilmelding.joark.dokumentikkefunnet.lead"),
        DOKUMENT_SLETTET("vedlegg.feilmelding.joark.dokumentslettet.head", "vedlegg.feilmelding.joark.dokumentslettet.lead"),
        UGYLDIG_SAKSTEMA("vedlegg.feilmelding.joark.ugyldigsakstema.head", "vedlegg.feilmelding.joark.ugyldigsakstema.lead"),
        GENERELL_FEIL("vedlegg.feilmelding.joark.generell.head", "vedlegg.feilmelding.joark.generell.lead");

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
    public String[] argumenterTilFeilmelding;

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

    public HentDokumentResultat(boolean harTilgang, Feilmelding feilmelding, String... argumenterTilFeilmelding) {
        this(harTilgang, feilmelding);
        this.argumenterTilFeilmelding = argumenterTilFeilmelding;
    }
}