package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain;

public class Dokument {

    private String tittel;
    private String dokumentreferanse;
    private boolean kanVises;
    private boolean logiskDokument;

    public String getTittel() {
        return tittel;
    }

    public String getDokumentreferanse() {
        return dokumentreferanse;
    }

    public boolean isKanVises() {
        return kanVises;
    }

    public boolean isLogiskDokument() {
        return logiskDokument;
    }

    public Dokument withTittel(final String tittel) {
        this.tittel = tittel;
        return this;
    }

    public Dokument withDokumentreferanse(final String dokumentreferanse) {
        this.dokumentreferanse = dokumentreferanse;
        return this;
    }

    public Dokument withKanVises(final boolean kanVises) {
        this.kanVises = kanVises;
        return this;
    }

    public Dokument withLogiskDokument(final boolean logiskDokument) {
        this.logiskDokument = logiskDokument;
        return this;
    }
}

