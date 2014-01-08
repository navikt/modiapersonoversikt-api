package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.utbetaling.domain.transform.Mergeable;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static org.apache.commons.lang3.StringUtils.join;

public class Underytelse implements Serializable, Mergeable {
    public static final Transformer<Underytelse, Double> UTBETALT_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            double underytelseBelop = underytelse.getBelop();
            return (underytelseBelop >= 0.0 ? underytelseBelop : 0.0);
        }
    };
    public static final Transformer<Underytelse, Double> TREKK_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            double underytelseBelop = underytelse.getBelop();
            return (underytelseBelop < 0.0 ? underytelseBelop : 0.0);
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

    public static final Transformer<Mergeable, Underytelse> MERGEABLE_UNDERYTELSE = new Transformer<Mergeable, Underytelse>() {
        @Override
        public Underytelse transform(Mergeable underytelse) {
            return (Underytelse) underytelse;
        }
    };

    private String tittel;
    private String spesifikasjon;
    private int antall;
    private double belop;
    private double sats;

    public Underytelse(String tittel, String spesifikasjon, int antall, double belop, double sats) {
        this.tittel = tittel != null? tittel.trim(): "";
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof Underytelse)) {
            return false;
        }

        Underytelse that = (Underytelse) o;
        return new EqualsBuilder()
                .append(antall, that.antall)
                .append(sats, that.sats)
                .append(tittel, that.tittel)
                .append(belop, that.belop)
                .append(spesifikasjon, that.spesifikasjon)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = tittel != null ? tittel.hashCode() : 0;
        result = 31 * result + (spesifikasjon != null ? spesifikasjon.hashCode() : 0);
        result = 31 * result + antall;
        temp = belop != +0.0d ? Double.doubleToLongBits(belop) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = sats != +0.0d ? Double.doubleToLongBits(sats) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public Underytelse doMerge(List<Mergeable> skalMerges) {
        List<Underytelse> ytelser = on(skalMerges).map(MERGEABLE_UNDERYTELSE).collectIn(new ArrayList<Underytelse>());
        if(ytelser.isEmpty()) { return null; }

        Double belop = on(ytelser).map(BELOP).reduce(sumDouble);
        Set<String> spesifikasjoner = on(ytelser).map(SPESIFIKASJON).collectIn(new HashSet<String>());
        String spesifikasjon = join(spesifikasjoner, ". ");
        Underytelse ytelse = ytelser.get(0);
        return new UnderytelseBuilder().setSpesifikasjon(spesifikasjon)
                .setBelop(belop)
                .setTittel(ytelse.getTittel())
                .setAntall(ytelse.getAntall())
                .setSats(ytelse.getSats()).createUnderytelse();
    }

    public static final class UnderytelseComparator {
        public static final Comparator<Underytelse> TITTEL = new Comparator<Underytelse>() {
            @Override
            public int compare(Underytelse o1, Underytelse o2) {
                return o1.getTittel().compareTo(o2.getTittel());
            }
        };

        public static final Comparator<Underytelse> TITTEL_ANTALL_SATS = new Comparator<Underytelse>() {
            @Override
            public int compare(Underytelse o1, Underytelse o2) {
                int compareTittel = o1.getTittel().compareTo(o2.getTittel());
                if(compareTittel == 0) {
                    int compareAntall = Integer.valueOf(o1.getAntall()).compareTo(o2.getAntall());
                    int compareSats = Double.valueOf(o1.getSats()).compareTo(o2.getSats());
                    if(compareAntall != 0) {
                        return compareAntall;
                    }
                    return compareSats;
                }
                return compareTittel;
            }
        };

        public static final Comparator<Mergeable> MERGEABLE_TITTEL = new Comparator<Mergeable>() {
            @Override
            public int compare(Mergeable o1, Mergeable o2) {
                return TITTEL.compare((Underytelse) o1, (Underytelse) o2);
            }
        };

        public static final Comparator<Mergeable> MERGEABLE_TITTEL_ANTALL_SATS = new Comparator<Mergeable>() {
            @Override
            public int compare(Mergeable o1, Mergeable o2) {
                return TITTEL_ANTALL_SATS.compare((Underytelse) o1, (Underytelse) o2);
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
