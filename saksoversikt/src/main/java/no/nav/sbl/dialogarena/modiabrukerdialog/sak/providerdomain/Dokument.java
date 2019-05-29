package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain;

public class Dokument {

    private String tittel;
    private String dokumentreferanse;
    private boolean kanVises;
    private boolean logiskDokument;
    private Variantformat variantformat;
    private String skjerming;

    public enum Variantformat {
        ARKIV,
        SLADDET,
        FULLVERSJON,
        PRODUKSJON,
        PRODUKSJON_DLF
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

    public String getSkjerming() {
        return skjerming;
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

    public Dokument withSkjerming(String skjerming) {
        this.skjerming = skjerming;
        return this;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public void setDokumentreferanse(String dokumentreferanse) {
        this.dokumentreferanse = dokumentreferanse;
    }

    public void setKanVises(boolean kanVises) {
        this.kanVises = kanVises;
    }

    public void setLogiskDokument(boolean logiskDokument) {
        this.logiskDokument = logiskDokument;
    }

    public void setVariantformat(Variantformat variantformat) {
        this.variantformat = variantformat;
    }

    public void setSkjerming(String skjerming) {
        this.skjerming = skjerming;
    }
}

