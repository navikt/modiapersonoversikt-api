package no.nav.modiapersonoversikt.legacy.sak.domain.dokumentvisning;

import java.util.ArrayList;
import java.util.List;

public class JournalpostResultat {

    private String tittel;
    private List<DokumentResultat> dokumenter = new ArrayList<>();
    private List<DokumentFeilmelding> feilendeDokumenter = new ArrayList<>();

    public String getTittel() {
        return tittel;
    }

    public List<DokumentResultat> getDokumenter() {
        return dokumenter;
    }

    public List<DokumentFeilmelding> getFeilendeDokumenter() {
        return feilendeDokumenter;
    }

    public JournalpostResultat withDokumentFeilmeldinger(List<DokumentFeilmelding> dokumentFeilmeldinger) {
        this.feilendeDokumenter = dokumentFeilmeldinger;
        return this;
    }

    public JournalpostResultat withDokumentFeilmelding(DokumentFeilmelding dokumentFeilmelding) {
        this.feilendeDokumenter.add(dokumentFeilmelding);
        return this;
    }

    public JournalpostResultat withTittel(String tittel) {
        this.tittel = tittel;
        return this;
    }

    public JournalpostResultat withDokumenter(List<DokumentResultat> dokumenter) {
        this.dokumenter = dokumenter;
        return this;
    }

    public JournalpostResultat withDokument(DokumentResultat dokument) {
        this.dokumenter.add(dokument);
        return this;
    }
}
