package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.WSUtbetalingTestData;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;


public class UtbetalingServiceTest {

    private UtbetalingService service;

    @Before
    public void setUp() throws Exception {
        service = new UtbetalingService();
    }

    @Test
    public void transformerUtbetaling() throws Exception {

        WSUtbetalingTestData data = new WSUtbetalingTestData();
        WSUtbetaling wsUtbetaling = data.createUtbetaling1();
        DateTime fraDateTime = new DateTime(2010, 1, 23, 0, 0);
        DateTime tilDateTime = new DateTime(2011, 1, 24, 0, 0);
        DateTime utbDato = now().minusDays(4);
        String alderspensjon = "Alderspensjon";
        String skatt = "Skatt";
        String fnr = "12345678900";
        //String meldingsTekst = "bilag1";
        //WSMelding melding = new WSMelding().withMeldingtekst(meldingsTekst);

        Utbetaling u = service.transformUtbetalinger(Arrays.asList(wsUtbetaling)).get(0);

        int compareUtbetalingsDato = DateTimeComparator.getDateOnlyInstance().compare(utbDato, u.getUtbetalingsDato());
        assertThat(compareUtbetalingsDato, is(0));
        assertThat(u.getStartDate(), is(fraDateTime));
        assertThat(u.getEndDate(), is(tilDateTime));
        assertThat(u.getNettoBelop(), is(1000.0));
        assertThat(u.getBruttoBelop(), is(1000.0));
        assertThat(u.getBeskrivelse(), is(alderspensjon + ", " + skatt));
        assertThat(u.getStatuskode(), is("12"));
        assertThat(u.getBilag().size(), is(2));
        //assertThat(u.getBilag().get(0).getMelding(), is(equalTo(melding.getMeldingtekst())));
        assertThat(u.getBilag().get(0).getPosteringsDetaljer().get(0).getHovedBeskrivelse(), is(alderspensjon));
        assertThat(u.getBilag().get(0).getPosteringsDetaljer().get(0).getKontoNr(), is(fnr));
    }
}