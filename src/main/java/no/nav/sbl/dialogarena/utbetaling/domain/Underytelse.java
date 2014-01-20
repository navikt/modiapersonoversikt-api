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

import static java.lang.Double.doubleToLongBits;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static org.apache.commons.lang3.StringUtils.join;

public class Underytelse implements Serializable, Mergeable<Underytelse> {
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

    public static Double getBrutto(List<Underytelse> underytelser) {
        return on(underytelser).map(UTBETALT_BELOP).reduce(sumDouble);
    }

    public static Double getTrekk(List<Underytelse> underytelser) {
        return on(underytelser).map(TREKK_BELOP).reduce(sumDouble);
    }

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
        temp = belop != +0.0d ? doubleToLongBits(belop) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = sats != +0.0d ? doubleToLongBits(sats) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public Underytelse doMerge(List<Mergeable> skalMerges) {
        List<Underytelse> ytelser = on(skalMerges).map(MERGEABLE_UNDERYTELSE).collectIn(new ArrayList<Underytelse>());
        if(ytelser.isEmpty()) { return null; }

        Double sum = on(ytelser).map(BELOP).reduce(sumDouble);
        Set<String> spesifikasjoner = on(ytelser).map(SPESIFIKASJON).collectIn(new HashSet<String>());
        String spesifikasjonsMelding = join(spesifikasjoner, ". ");
        Underytelse ytelse = ytelser.get(0);

        return new Underytelse(ytelse.getTittel(), spesifikasjonsMelding, ytelse.getAntall(), sum, ytelse.getSats());
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

        public static final Comparator<Mergeable<Underytelse>> MERGEABLE_TITTEL = new Comparator<Mergeable<Underytelse>>() {
            @Override
            public int compare(Mergeable o1, Mergeable o2) {
                return TITTEL.compare((Underytelse) o1, (Underytelse) o2);
            }
        };

        public static final Comparator<Mergeable<Underytelse>> MERGEABLE_TITTEL_ANTALL_SATS = new Comparator<Mergeable<Underytelse>>() {
            @Override
            public int compare(Mergeable<Underytelse> o1, Mergeable<Underytelse> o2) {
                return TITTEL_ANTALL_SATS.compare((Underytelse) o1, (Underytelse) o2);
            }
        };
        /**
         * Sorterer basert på beløpet, slik at negative tall havner nedenfor de positive
         */
        public static final Comparator<Mergeable<Underytelse>> MERGEABLE_BELOP = new Comparator<Mergeable<Underytelse>>() {
            @Override
            public int compare(Mergeable<Underytelse> o1, Mergeable<Underytelse> o2) {
                return Double.compare(((Underytelse)o2).getBelop(), ((Underytelse)o1).getBelop());
            }
        };

        private static final String SKATT = "skatt";
        /**
         * Sorterer underytelser med tittel som inneholder "skatt", og er negativt, nederst.
         */
        public static final Comparator<Mergeable<Underytelse>> MERGEABLE_SKATT_NEDERST = new Comparator<Mergeable<Underytelse>>(){

            @Override
            public int compare(Mergeable<Underytelse> o1, Mergeable<Underytelse> o2) {
                if (((Underytelse)o1).getTittel().toLowerCase().contains(SKATT) && ((Underytelse)o1).getBelop() <= 0) {
                    return 1;
                } else if (((Underytelse)o2).getTittel().toLowerCase().contains(SKATT) && ((Underytelse)o2).getBelop() <= 0) {
                    return -1;
                }
                return 0;
            }
        };
    }
}
