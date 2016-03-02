package no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning;

import java.util.List;

public class JournalpostResultat {

    private String tittel;
    private List<DokumentResultat> dokumenter;
    private List<DokumentFeilmelding> feilendeDokumenter;

    public String getTittel() {
        return tittel;
    }

    public List<DokumentResultat> getDokumenter() {
        return dokumenter;
    }

    public List<DokumentFeilmelding> getFeilendeDokumenter() {
        return feilendeDokumenter;
    }

    public JournalpostResultat withDokumentFeilmelding(List<DokumentFeilmelding> dokumentFeilmelding) {
        this.feilendeDokumenter = dokumentFeilmelding;
        return this;
    }

    public JournalpostResultat withTittel(String tittel) {
        this.tittel = tittel;
        return this;
    }

    public JournalpostResultat withDokument(List<DokumentResultat> dokumenter) {
        this.dokumenter = dokumenter;
        return this;
    }
}
