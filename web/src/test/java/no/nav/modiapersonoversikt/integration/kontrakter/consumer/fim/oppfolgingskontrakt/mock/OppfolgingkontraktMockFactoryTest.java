package no.nav.modiapersonoversikt.integration.kontrakter.consumer.fim.oppfolgingskontrakt.mock;

import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.WSOppfoelgingskontrakt;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OppfolgingkontraktMockFactoryTest {
    @Test
    public void testCreateOppfoelgingskontrakter() {
        String fnr = "22222222222";
        Date fra = LocalDate.now().minusMonths(32).toDate();
        Date til = LocalDate.now().plusMonths(32).toDate();
        List<? extends WSOppfoelgingskontrakt> oppfoelgingskontrakter = OppfolgingkontraktMockFactory.createOppfoelgingskontrakter(fnr, fra, til);
        assertEquals(oppfoelgingskontrakter.size(), 1);
    }
}
