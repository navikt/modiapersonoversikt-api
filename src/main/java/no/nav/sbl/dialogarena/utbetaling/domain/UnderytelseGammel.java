package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.modig.lang.collections.iter.ReduceFunction;
import no.nav.modig.lang.option.Optional;
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

public class UnderytelseGammel implements Serializable {

    private String tittel;
    private double belop;
    private Optional<Double> antall;
    private Optional<Double> sats;
    private String spesifikasjon;

    public UnderytelseGammel(String tittel, String spesifikasjon, Optional<Double> antall, double belop, Optional<Double> sats) {
        this.tittel = tittel;
        this.belop = belop;
        this.antall = antall;
        this.sats = sats;
        this.spesifikasjon = spesifikasjon;
    }

    public String getTittel() {
        return tittel;
    }

    public String getSpesifikasjon() {
        return spesifikasjon;
    }

    public Optional<Double> getAntall() {
        return antall;
    }

    public double getBelop() {
        return belop;
    }

    public Optional<Double> getSats() {
        return sats;
    }

    public static final Transformer<UnderytelseGammel, Double> UTBETALT_BELOP = new Transformer<UnderytelseGammel, Double>() {
        @Override
        public Double transform(UnderytelseGammel underytelseGammel) {
            double underytelseBelop = underytelseGammel.getBelop();
            return underytelseBelop >= 0 ? underytelseBelop : 0;
        }
    };
    public static final Transformer<UnderytelseGammel, Double> TREKK_BELOP = new Transformer<UnderytelseGammel, Double>() {
        @Override
        public Double transform(UnderytelseGammel underytelseGammel) {
            double underytelseBelop = underytelseGammel.getBelop();
            return underytelseBelop < 0 ? underytelseBelop : 0;
        }
    };
    public static final Transformer<UnderytelseGammel, String> UNDERYTELSE_TITTEL = new Transformer<UnderytelseGammel, String>() {
        @Override
        public String transform(UnderytelseGammel underytelseGammel) {
            return underytelseGammel.getTittel();
        }
    };
    public static final Transformer<UnderytelseGammel, Double> UNDERYTELSE_BELOP = new Transformer<UnderytelseGammel, Double>() {
        @Override
        public Double transform(UnderytelseGammel underytelseGammel) {
            return underytelseGammel.getBelop();
        }
    };
    public static final Transformer<UnderytelseGammel, String> UNDERYTELSE_SPESIFIKASJON = new Transformer<UnderytelseGammel, String>() {
        @Override
        public String transform(UnderytelseGammel underytelseGammel) {
            return underytelseGammel.getSpesifikasjon();
        }
    };

    /**
     * Sorterer basert på beløpet, slik at negative tall havner nedenfor de positive
     */
    public static final Comparator<UnderytelseGammel> UNDERYTELSE_COMPARE_BELOP = new Comparator<UnderytelseGammel>() {
        @Override
        public int compare(UnderytelseGammel o1, UnderytelseGammel o2) {
            return Double.compare(o2.getBelop(), o1.getBelop());
        }
    };

    /**
     * Sorterer underytelser med tittel som inneholder "skatt", og er negativt, nederst.
     */
    public static final Comparator<UnderytelseGammel> UNDERYTELSE_SKATT_NEDERST = new Comparator<UnderytelseGammel>(){
        private static final String SKATT = "skatt";

        @Override
        public int compare(UnderytelseGammel o1, UnderytelseGammel o2) {
            if (o1.getTittel().toLowerCase().contains(SKATT) && o1.getBelop() <= 0) {
                return 1;
            } else if (o2.getTittel().toLowerCase().contains(SKATT) && o2.getBelop() <= 0) {
                return -1;
            }
            return 0;
        }
    };

    public static final ReduceFunction<List<UnderytelseGammel>, List<UnderytelseGammel>> SUM_UNDERYTELSER = new ReduceFunction<List<UnderytelseGammel>, List<UnderytelseGammel>>() {
        @Override
        public List<UnderytelseGammel> reduce(List<UnderytelseGammel> accumulator, List<UnderytelseGammel> ytelser) {
            if (ytelser.isEmpty()) {
                return accumulator;
            }

            Double sum = on(ytelser).map(UNDERYTELSE_BELOP).reduce(sumDouble);

            Set<String> spesifikasjoner = on(ytelser).map(UnderytelseGammel.UNDERYTELSE_SPESIFIKASJON).collectIn(new HashSet<String>());
            String spesifikasjonsMelding = join(spesifikasjoner, ". ");

            UnderytelseGammel ytelse = ytelser.get(0);
            accumulator.add(new UnderytelseGammel(ytelse.getTittel(), spesifikasjonsMelding, ytelse.getAntall(), sum, ytelse.getSats()));
            return accumulator;
        }

        @Override
        public List<UnderytelseGammel> identity() {
            return new ArrayList<>();
        }
    };
}
