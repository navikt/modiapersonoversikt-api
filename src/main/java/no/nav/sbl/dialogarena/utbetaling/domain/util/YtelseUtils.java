package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSAktoer;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSPerson;
import org.joda.time.LocalDate;

import java.util.Comparator;

import static org.joda.time.LocalDate.now;

public class YtelseUtils {

    public static LocalDate defaultStartDato() {
        return now().minusMonths(3);
    }

    public static LocalDate defaultSluttDato() {
        return now();
    }

    public static final class UtbetalingComparator {
        public static final Comparator<Record<Hovedytelse>> HOVEDYTELSE_DATO_COMPARATOR = new Comparator<Record<Hovedytelse>>() {
            @Override
            public int compare(Record<Hovedytelse> o1, Record<Hovedytelse> o2) {
                int compareDato = -o1.get(Hovedytelse.hovedytelsedato).toLocalDate().compareTo(o2.get(Hovedytelse.hovedytelsedato).toLocalDate());
                if (compareDato == 0) {
                    return o1.get(Hovedytelse.ytelse).compareToIgnoreCase(o2.get(Hovedytelse.ytelse));
                }
                return compareDato;
            }
        };
    }

    public static final Mottakertype mottakertypeForAktoer(WSAktoer wsAktoer) {
        if(wsAktoer instanceof WSPerson) {
            return Mottakertype.BRUKER;
        }
        return Mottakertype.ANNEN_MOTTAKER;
    }
}
