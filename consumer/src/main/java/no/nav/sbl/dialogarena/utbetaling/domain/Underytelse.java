package no.nav.sbl.dialogarena.utbetaling.domain;

public class Underytelse {
    private String tittel;
    private int antall;
    private double belop;
    private double sats;

    public Underytelse(String tittel, int antall, double belop, double sats) {
        this.tittel = tittel;
        this.antall = antall;
        this.belop = belop;
        this.sats = sats;
    }

    public String getTittel() {
        return tittel;
    }

    public int getAntall() {
        return antall;
    }

    public double getBelop() {
        return belop;
    }

    public double getSats() {
        return sats;
    }
}
