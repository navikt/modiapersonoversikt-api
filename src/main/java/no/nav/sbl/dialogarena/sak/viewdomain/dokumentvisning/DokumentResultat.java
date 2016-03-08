package no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning;

import static java.lang.String.format;

public class DokumentResultat {
    public final String pdfUrl;
    public final String tittel;
    public final Integer antallsider;
    public final String fnr;
    public final String journalpostId;
    public final String dokumentreferanse;


    public DokumentResultat(String tittel, Integer antallsider, String fnr, String journalpostId, String dokumentreferanse) {
        this.tittel = tittel;
        this.antallsider = antallsider;
        this.fnr = fnr;
        this.journalpostId = journalpostId;
        this.dokumentreferanse = dokumentreferanse;
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

    public Integer getAntallsider() {
        return antallsider;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public String getDokumentreferanse() {
        return dokumentreferanse;
    }
}
