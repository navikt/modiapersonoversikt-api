package no.nav.modiapersonoversiktproxy.consumer.arena.utils;

import no.nav.modiapersonoversiktproxy.utils.DateUtils;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.FimPeriode;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FimPeriodeUtilTest {

    @Test
    public void test() {
        FimPeriode periode = new FimPeriode();
        periode.setFom(DateUtils.convertDateToXmlGregorianCalendar(DateTime.parse("2012-01-01").toDate()));
        periode.setTom(DateUtils.convertDateToXmlGregorianCalendar(DateTime.parse("2014-01-01").toDate()));
        assertTrue(FimPeriodeUtil.periodeInside(periode, DateTime.parse("2012-01-01").toDate(), DateTime.parse("2012-02-01").toDate()));
        assertTrue(FimPeriodeUtil.periodeInside(periode, DateTime.parse("2012-01-01").toDate(), DateTime.parse("2014-02-01").toDate()));
        assertFalse(FimPeriodeUtil.periodeInside(periode, DateTime.parse("2014-01-02").toDate(), DateTime.parse("2014-02-01").toDate()));

        WSPeriode periode2 = new WSPeriode();
        periode2.setFom(DateUtils.convertDateToXmlGregorianCalendar(DateTime.parse("2012-01-01").toDate()));
        periode2.setTom(DateUtils.convertDateToXmlGregorianCalendar(DateTime.parse("2014-01-01").toDate()));
        assertTrue(FimPeriodeUtil.periodeInside(periode2, DateTime.parse("2012-01-01").toDate(), DateTime.parse("2012-02-01").toDate()));
        assertTrue(FimPeriodeUtil.periodeInside(periode2, DateTime.parse("2012-01-01").toDate(), DateTime.parse("2044-02-01").toDate()));
        assertFalse(FimPeriodeUtil.periodeInside(periode2, DateTime.parse("2014-01-02").toDate(), DateTime.parse("2014-02-01").toDate()));
    }
}
