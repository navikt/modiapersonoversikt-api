package no.nav.modiapersonoversikt.legacy.sak.domain.dokumentvisning;

import static java.lang.String.format;

public class DokumentResultat {
    public final String pdfUrl;
    public final String tittel;
    public final Integer antallSider;
    public final String fnr;
    public final String journalpostId;
    public final String dokumentreferanse;
    public final boolean erHoveddokument;

    public DokumentResultat(String tittel, Integer antallSider, String fnr, String journalpostId, String dokumentreferanse, boolean erHoveddokument) {
        this.tittel = tittel;
        this.antallSider = antallSider;
        this.fnr = fnr;
        this.journalpostId = journalpostId;
        this.dokumentreferanse = dokumentreferanse;
        this.erHoveddokument = erHoveddokument;
        this.pdfUrl = format("/modiabrukerdialog/rest/saksoversikt/%s/dokument/%s/%s",
                fnr,
                journalpostId,
                dokumentreferanse
        );
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getFnr() {
        return fnr;
    }

    public String getTittel() {
        return tittel;
    }

    public Integer getAntallSider() {
        return antallSider;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public String getDokumentreferanse() {
        return dokumentreferanse;
    }

    public boolean erHoveddokument() {
        return erHoveddokument;
    }
}
