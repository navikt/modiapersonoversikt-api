package no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.mock;

import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.WSOppfoelgingskontrakt;
import no.nav.modiapersonoversikt.utils.DateUtils;
import java.time.LocalDate;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OppfolgingkontraktMockFactoryTest {
    @Test
    public void testCreateOppfoelgingskontrakter() {
        String fnr = "22222222222";
        Date fra = DateUtils.toDate(LocalDate.now().minusMonths(32));
        Date til = DateUtils.toDate(LocalDate.now().plusMonths(32));
        List<? extends WSOppfoelgingskontrakt> oppfoelgingskontrakter = OppfolgingkontraktMockFactory.createOppfoelgingskontrakter(fnr, fra, til);
        assertEquals(oppfoelgingskontrakter.size(), 1);
    }
}
