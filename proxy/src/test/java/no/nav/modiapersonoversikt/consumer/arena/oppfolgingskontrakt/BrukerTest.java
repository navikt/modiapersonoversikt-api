package no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt;

import no.nav.modiapersonoversikt.arena.oppfolgingskontrakt.Bruker;
import no.nav.modiapersonoversikt.utils.DateUtils;
import java.util.Date;
import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BrukerTest {

    public static final String FORMIDLINGSGRUPPE = "12";
    public static final String INNSATSGRUPPE = "a";
    public static final Boolean MELDEPLIKT = Boolean.TRUE;
    public static final LocalDate SYKMELDT_FROM = DateUtils.toLocalDate(new Date(System.currentTimeMillis() - 25525));

    @Test
    public void testBean() {
        Bruker bruker = new Bruker();
        bruker.setFormidlingsgruppe(FORMIDLINGSGRUPPE);
        bruker.setInnsatsgruppe(INNSATSGRUPPE);
        bruker.setMeldeplikt(MELDEPLIKT);
        bruker.setSykmeldtFrom(SYKMELDT_FROM);
        assertEquals(FORMIDLINGSGRUPPE, bruker.getFormidlingsgruppe());
        assertEquals(SYKMELDT_FROM, bruker.getSykmeldtFrom());
        assertEquals(MELDEPLIKT, bruker.getMeldeplikt());
        assertEquals(INNSATSGRUPPE, bruker.getInnsatsgruppe());
    }
}
