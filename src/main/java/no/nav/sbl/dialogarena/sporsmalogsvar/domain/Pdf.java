package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

public class Pdf {

    private String dokumenttittel;
    private byte[] pdfBytes;

    public Pdf(String dokumenttittel, byte[] pdfBytes) {
        this.dokumenttittel = dokumenttittel;
        this.pdfBytes = pdfBytes;
    }

    public String getDokumenttittel() {
        return dokumenttittel;
    }

    public byte[] getPdfBytes() {
        return pdfBytes;
    }

}
