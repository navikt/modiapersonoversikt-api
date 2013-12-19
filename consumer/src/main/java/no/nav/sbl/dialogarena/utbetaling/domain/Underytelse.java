package no.nav.sbl.dialogarena.utbetaling.domain;

import java.util.Comparator;

public class Underytelse {
    private String tittel;
    private String spesifikasjon;
    private int antall;
    private double belop;
    private double sats;

    public Underytelse(String tittel, String spesifikasjon, int antall, double belop, double sats) {
        this.tittel = tittel;
        this.spesifikasjon = spesifikasjon;
        this.antall = antall;
        this.belop = belop;
        this.sats = sats;
    }

    public String getTittel() {
        return tittel;
    }

    public String getSpesifikasjon() {
        return spesifikasjon;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Underytelse)) return false;

        Underytelse that = (Underytelse) o;

        if (antall != that.antall) return false;
        if (Double.compare(that.sats, sats) != 0) return false;
        if (tittel != null ? !tittel.equals(that.tittel) : that.tittel != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = tittel != null ? tittel.hashCode() : 0;
        result = 31 * result + antall;
        temp = sats != +0.0d ? Double.doubleToLongBits(sats) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static final class UnderytelseComparator {
        public static Comparator<Underytelse> TITTEL = new Comparator<Underytelse>() {
            @Override
            public int compare(Underytelse o1, Underytelse o2) {
                return o1.getTittel().compareTo(o2.getTittel());
            }
        };
    }

}
