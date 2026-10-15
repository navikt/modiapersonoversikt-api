package no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt;

import no.nav.modiapersonoversikt.arena.oppfolgingskontrakt.SYFOPunkt;
import no.nav.modiapersonoversikt.utils.DateUtils;
import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.Date;

public class SYFOPunktTest {

    public static final LocalDate DATE = DateUtils.toLocalDate(new Date(System.currentTimeMillis() + 897987));
    public static final boolean FAST_OPPFOLGINGSPUNKT = false;
    public static final String STATUSMELDING = "statusmelding";
    public static final String HENDELSE = "Hendelse";

    @Test
    public void testBean() {
        SYFOPunkt syfoPunkt = new SYFOPunkt();
        syfoPunkt.setDato(DATE);
        syfoPunkt.setFastOppfolgingspunkt(FAST_OPPFOLGINGSPUNKT);
        syfoPunkt.setStatus(STATUSMELDING);
        syfoPunkt.setSyfoHendelse(HENDELSE);
        assertEquals(DATE, syfoPunkt.getDato());
        assertEquals(HENDELSE, syfoPunkt.getSyfoHendelse());
        assertEquals(STATUSMELDING, syfoPunkt.getStatus());
        assertEquals(FAST_OPPFOLGINGSPUNKT, syfoPunkt.isFastOppfolgingspunkt());
    }
}
