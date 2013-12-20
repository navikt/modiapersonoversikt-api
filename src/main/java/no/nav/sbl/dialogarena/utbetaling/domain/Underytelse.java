package no.nav.sbl.dialogarena.utbetaling.domain;

import org.apache.commons.collections15.Transformer;

import java.io.Serializable;
import java.util.Comparator;

public class Underytelse implements Serializable {
    public static final Transformer<Underytelse, Double> UTBETALT_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            double belop = underytelse.getBelop();
            return (belop >= 0.0 ? belop : 0.0);
        }
    };
    public static final Transformer<Underytelse, Double> TREKK_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            double belop = underytelse.getBelop();
            return (belop < 0.0 ? belop : 0.0);
        }
    };
    public static final Transformer<Underytelse, Double> BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            return underytelse.getBelop();
        }
    };
    public static final Transformer<Underytelse, String> SPESIFIKASJON = new Transformer<Underytelse, String>() {
        @Override
        public String transform(Underytelse underytelse) {
            return underytelse.getSpesifikasjon();
        }
    };

    public static final Transformer<Underytelse, String> UNDERYTELSE_TITTEL = new Transformer<Underytelse, String>() {
        @Override
        public String transform(Underytelse underytelse) {
            return underytelse.getTittel();
        }
    };
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
    public String toString() {
        return "Underytelse{" +
                "tittel='" + tittel + '\'' +
                ", belop=" + belop +
                ", antall=" + antall +
                ", sats=" + sats +
                ", spesifikasjon='" + spesifikasjon + '\'' +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Underytelse)) return false;

        Underytelse that = (Underytelse) o;

        if (antall != that.antall) return false;
        if (Double.compare(that.sats, sats) != 0) return false;
        return !(tittel != null ? !tittel.equals(that.tittel) : that.tittel != null);
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

    public static final class UnderytelseBuilder {
        private String tittel;
        private String spesifikasjon;
        private int antall;
        private double belop;
        private double sats;

        public UnderytelseBuilder setTittel(String tittel) {
            this.tittel = tittel;
            return this;
        }

        public UnderytelseBuilder setSpesifikasjon(String spesifikasjon) {
            this.spesifikasjon = spesifikasjon;
            return this;
        }

        public UnderytelseBuilder setAntall(int antall) {
            this.antall = antall;
            return this;
        }

        public UnderytelseBuilder setBelop(double belop) {
            this.belop = belop;
            return this;
        }

        public UnderytelseBuilder setSats(double sats) {
            this.sats = sats;
            return this;
        }

        public Underytelse createUnderytelse() {
            return new Underytelse(tittel, spesifikasjon, antall, belop, sats);
        }
    }

}
