package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.modig.lang.collections.iter.ReduceFunction;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static org.apache.commons.lang3.StringUtils.join;

public class Underytelse implements Serializable {

    private String tittel;
    private String spesifikasjon;
    private int antall;
    private double belop;
    private double sats;

    public Underytelse(String tittel, String spesifikasjon, int antall, double belop, double sats) {
        this.tittel = tittel != null ? tittel.trim() : "";
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

    public static final Transformer<Underytelse, Double> UTBETALT_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            double underytelseBelop = underytelse.getBelop();
            return underytelseBelop >= 0 ? underytelseBelop : 0;
        }
    };
    public static final Transformer<Underytelse, Double> TREKK_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            double underytelseBelop = underytelse.getBelop();
            return underytelseBelop < 0 ? underytelseBelop : 0;
        }
    };
    public static final Transformer<Underytelse, String> UNDERYTELSE_TITTEL = new Transformer<Underytelse, String>() {
        @Override
        public String transform(Underytelse underytelse) {
            return underytelse.getTittel();
        }
    };
    public static final Transformer<Underytelse, Double> UNDERYTELSE_BELOP = new Transformer<Underytelse, Double>() {
        @Override
        public Double transform(Underytelse underytelse) {
            return underytelse.getBelop();
        }
    };
    public static final Transformer<Underytelse, String> UNDERYTELSE_SPESIFIKASJON = new Transformer<Underytelse, String>() {
        @Override
        public String transform(Underytelse underytelse) {
            return underytelse.getSpesifikasjon();
        }
    };

    /**
     * Sorterer basert på beløpet, slik at negative tall havner nedenfor de positive
     */
    public static final Comparator<Underytelse> UNDERYTELSE_COMPARE_BELOP = new Comparator<Underytelse>() {
        @Override
        public int compare(Underytelse o1, Underytelse o2) {
            return Double.compare(o2.getBelop(), o1.getBelop());
        }
    };

    /**
     * Sorterer underytelser med tittel som inneholder "skatt", og er negativt, nederst.
     */
    public static final Comparator<Underytelse> UNDERYTELSE_SKATT_NEDERST = new Comparator<Underytelse>(){
        private static final String SKATT = "skatt";

        @Override
        public int compare(Underytelse o1, Underytelse o2) {
            if (o1.getTittel().toLowerCase().contains(SKATT) && o1.getBelop() <= 0) {
                return 1;
            } else if (o2.getTittel().toLowerCase().contains(SKATT) && o2.getBelop() <= 0) {
                return -1;
            }
            return 0;
        }
    };

    public static final ReduceFunction<List<Underytelse>, List<Underytelse>> SUM_UNDERYTELSER = new ReduceFunction<List<Underytelse>, List<Underytelse>>() {
        @Override
        public List<Underytelse> reduce(List<Underytelse> accumulator, List<Underytelse> ytelser) {
            if (ytelser.isEmpty()) {
                return accumulator;
            }

            Double sum = on(ytelser).map(UNDERYTELSE_BELOP).reduce(sumDouble);

            Set<String> spesifikasjoner = on(ytelser).map(Underytelse.UNDERYTELSE_SPESIFIKASJON).collectIn(new HashSet<String>());
            String spesifikasjonsMelding = join(spesifikasjoner, ". ");

            Underytelse ytelse = ytelser.get(0);
            accumulator.add(new Underytelse(ytelse.getTittel(), spesifikasjonsMelding, ytelse.getAntall(), sum, ytelse.getSats()));
            return accumulator;
        }

        @Override
        public List<Underytelse> identity() {
            return new ArrayList<>();
        }
    };
}
