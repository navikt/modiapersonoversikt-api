package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.modig.lang.collections.iter.ReduceFunction;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse.Mottakertype;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSPerson;
import org.apache.commons.collections15.Transformer;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.*;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static org.apache.commons.lang3.StringUtils.join;
import static org.joda.time.LocalDate.now;

public class YtelseUtils {

    public static LocalDate defaultStartDato() {
        return now().minusMonths(3);
    }

    public static LocalDate defaultSluttDato() {
        return now();
    }

    public static final class UtbetalingComparator {
        public static final Comparator<Utbetaling> UTBETALING_DAG_YTELSE = new Comparator<Utbetaling>() {
            @Override
            public int compare(Utbetaling o1, Utbetaling o2) {
                int compareDato = -o1.getUtbetalingsdato().toLocalDate().compareTo(o2.getUtbetalingsdato().toLocalDate());
                if (compareDato == 0) {
                    return o1.getHovedytelse().compareToIgnoreCase(o2.getHovedytelse());
                }
                return compareDato;
            }
        };
    }

    public static final Transformer<Utbetaling, String> HOVEDYTELSE = new Transformer<Utbetaling, String>() {
        @Override
        public String transform(Utbetaling utbetaling) {
            return utbetaling.getHovedytelse();
        }
    };

    public static final Transformer<Utbetaling, List<UnderytelseGammel>> UNDERYTELSER = new Transformer<Utbetaling, List<UnderytelseGammel>>() {
        @Override
        public List<UnderytelseGammel> transform(Utbetaling utbetaling) {
            return utbetaling.getUnderytelser();
        }
    };

    public static final Transformer<Utbetaling, Interval> PERIODE = new Transformer<Utbetaling, Interval>() {
        @Override
        public Interval transform(Utbetaling utbetaling) {
            return utbetaling.getPeriode();
        }
    };

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

    public static final Mottakertype mottakertypeForAktoer(WSAktoer wsAktoer) {
        if(wsAktoer instanceof WSPerson) {
            return Mottakertype.BRUKER;
        }
        return Mottakertype.ANNEN_MOTTAKER;
    }
}
