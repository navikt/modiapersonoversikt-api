package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import static java.util.Arrays.copyOf;

public class Pdf {

    private String dokumenttittel;
    private byte[] pdfBytes;

    public Pdf(String dokumenttittel, byte[] pdfBytes) {
        this.dokumenttittel = dokumenttittel;
        this.pdfBytes = copyOf(pdfBytes, pdfBytes.length);
    }

    public String getDokumenttittel() {
        return dokumenttittel;
    }

    public byte[] getPdfBytes() {
        return copyOf(pdfBytes, pdfBytes.length);
    }

}
