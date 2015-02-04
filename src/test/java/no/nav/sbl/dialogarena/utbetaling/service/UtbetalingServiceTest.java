package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSForespurtPeriode;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UtbetalingServiceTest {

    @Mock
    private UtbetalingV1 utbetalingV1;

    @InjectMocks
    private UtbetalingService utbetalingService = new UtbetalingService();

    @Test
    public void generererKorrektPeriode() {
        WSForespurtPeriode periode = utbetalingService.createPeriode(new LocalDate(2015, 1, 1), new LocalDate(2015, 1, 2));
        assertThat(periode.getFom(), is(new DateTime(2015, 1, 1, 0, 0)));
        assertThat(periode.getTom(), is(new DateTime(2015, 1, 2, 0, 0)));
    }

    @Test
    public void generererKorrektRequests() {
        WSHentUtbetalingsinformasjonRequest request = utbetalingService.createRequest("123456789", new LocalDate(2015, 1, 1), new LocalDate(2015, 1, 2));
        assertThat(request.getId().getIdent(), is("123456789"));
        assertThat(request.getPeriode().getFom(), is(new DateTime(2015, 1, 1, 0, 0)));
        assertThat(request.getPeriode().getTom(), is(new DateTime(2015, 1, 2, 0, 0)));
    }
}