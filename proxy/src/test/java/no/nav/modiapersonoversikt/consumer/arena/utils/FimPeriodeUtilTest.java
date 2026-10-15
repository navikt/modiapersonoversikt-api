package no.nav.modiapersonoversikt.consumer.arena.utils;

import no.nav.modiapersonoversikt.utils.DateUtils;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimPeriode;
import java.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FimPeriodeUtilTest {

    @Test
    public void test() {
        FimPeriode periode = new FimPeriode();
        periode.setFom(DateUtils.convertDateToXmlGregorianCalendar(DateUtils.toDate(LocalDate.parse("2012-01-01"))));
        periode.setTom(DateUtils.convertDateToXmlGregorianCalendar(DateUtils.toDate(LocalDate.parse("2014-01-01"))));
        assertTrue(FimPeriodeUtil.periodeInside(periode, DateUtils.toDate(LocalDate.parse("2012-01-01")), DateUtils.toDate(LocalDate.parse("2012-02-01"))));
        assertTrue(FimPeriodeUtil.periodeInside(periode, DateUtils.toDate(LocalDate.parse("2012-01-01")), DateUtils.toDate(LocalDate.parse("2014-02-01"))));
        assertFalse(FimPeriodeUtil.periodeInside(periode, DateUtils.toDate(LocalDate.parse("2014-01-02")), DateUtils.toDate(LocalDate.parse("2014-02-01"))));

        WSPeriode periode2 = new WSPeriode();
        periode2.setFom(DateUtils.convertDateToXmlGregorianCalendar(DateUtils.toDate(LocalDate.parse("2012-01-01"))));
        periode2.setTom(DateUtils.convertDateToXmlGregorianCalendar(DateUtils.toDate(LocalDate.parse("2014-01-01"))));
        assertTrue(FimPeriodeUtil.periodeInside(periode2, DateUtils.toDate(LocalDate.parse("2012-01-01")), DateUtils.toDate(LocalDate.parse("2012-02-01"))));
        assertTrue(FimPeriodeUtil.periodeInside(periode2, DateUtils.toDate(LocalDate.parse("2012-01-01")), DateUtils.toDate(LocalDate.parse("2044-02-01"))));
        assertFalse(FimPeriodeUtil.periodeInside(periode2, DateUtils.toDate(LocalDate.parse("2014-01-02")), DateUtils.toDate(LocalDate.parse("2014-02-01"))));
    }
}
