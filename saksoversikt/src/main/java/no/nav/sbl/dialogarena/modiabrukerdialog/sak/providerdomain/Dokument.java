package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain;

public class Dokument {

    private String tittel;
    private String dokumentreferanse;
    private boolean kanVises;
    private boolean logiskDokument;
    private Variantformat variantformat;


    public enum Variantformat {
        ARKIV, SLADDET
    }

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

    public Variantformat getVariantformat() {
        return variantformat;
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

    public Dokument withVariantformat(final Variantformat variantformat) {
        this.variantformat = variantformat;
        return this;
    }

    public Dokument setTittel(String tittel) {
        this.tittel = tittel;
        return this;
    }

    public Dokument setDokumentreferanse(String dokumentreferanse) {
        this.dokumentreferanse = dokumentreferanse;
        return this;
    }

    public Dokument setKanVises(boolean kanVises) {
        this.kanVises = kanVises;
        return this;
    }

    public Dokument setLogiskDokument(boolean logiskDokument) {
        this.logiskDokument = logiskDokument;
        return this;
    }

    public Dokument setVariantformat(Variantformat variantformat) {
        this.variantformat = variantformat;
        return this;
    }
}

