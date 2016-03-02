package no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning;

public class DokumentResultat {

    private String pdfUrl;
    private String tittel;
    private Integer antallsider;

    public DokumentResultat(String pdfUrl, String tittel, Integer antallsider) {
        this.pdfUrl = pdfUrl;
        this.tittel = tittel;
        this.antallsider = antallsider;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }
    public String getTittel() {
        return tittel;
    }
    public Integer getAntallsider() {
        return antallsider;
    }
}
