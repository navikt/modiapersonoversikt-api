package no.nav.modiapersonoversikt.service.saf.domain;

public class Dokument {

    private String tittel;
    private String dokumentreferanse;
    private boolean kanVises;
    private boolean logiskDokument;
    private Variantformat variantformat;
    private String skjerming;
    private DokumentStatus dokumentStatus;

    public enum DokumentStatus {
        UNDER_REDIGERING,
        FERDIGSTILT,
        AVBRUTT,
        KASSERT
    }

    public enum Variantformat {
        ARKIV,
        SLADDET,
        FULLVERSJON,
        PRODUKSJON,
        PRODUKSJON_DLF
    }

    public boolean isKassert() {
        return dokumentStatus == DokumentStatus.KASSERT;
    }

    public DokumentStatus getDokumentStatus() {
        return dokumentStatus;
    }

    public void setDokumentStatus(DokumentStatus dokumentStatus) {
        this.dokumentStatus = dokumentStatus;
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

