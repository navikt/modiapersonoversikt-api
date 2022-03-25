package no.nav.modiapersonoversikt.consumer.kontrakter.domain.ytelse;

import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.Vedtak;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VedtakTest {

    public static final LocalDate ACTIVE_FROM = new LocalDate(System.currentTimeMillis() - 891569941);
    public static final LocalDate ACTIVE_TO = new LocalDate(System.currentTimeMillis() + 891555641);
    public static final LocalDate VEDTAKSDATO = new LocalDate(System.currentTimeMillis() + 891524141);
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String AKTIVITETSFASE = "aktivitetsfase";

    @Test
    public void testBean() {
        Vedtak vedtak = new Vedtak();
        vedtak.setActiveFrom(ACTIVE_FROM);
        vedtak.setActiveTo(ACTIVE_TO);
        vedtak.setVedtaksdato(VEDTAKSDATO);
        vedtak.setVedtakstatus(STATUS);
        vedtak.setVedtakstype(TYPE);
        vedtak.setAktivitetsfase(AKTIVITETSFASE);
        assertEquals(ACTIVE_FROM, vedtak.getActiveFrom());
        assertEquals(ACTIVE_TO, vedtak.getActiveTo());
        assertEquals(VEDTAKSDATO, vedtak.getVedtaksdato());
        assertEquals(STATUS, vedtak.getVedtakstatus());
        assertEquals(TYPE, vedtak.getVedtakstype());
        assertEquals(AKTIVITETSFASE, vedtak.getAktivitetsfase());
    }
}
