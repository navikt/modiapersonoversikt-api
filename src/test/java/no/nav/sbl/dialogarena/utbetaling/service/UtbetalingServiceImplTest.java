package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonIkkeTilgang;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPersonIkkeFunnet;
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

import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createKariNordmannUtbetaling;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UtbetalingServiceImplTest {

    @Mock
    private UtbetalingV1 utbetalingV1;

    @InjectMocks
    private UtbetalingServiceImpl utbetalingService = new UtbetalingServiceImpl();

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

    @Test
    public void hentUtbetalingerReturnererKorrekteHovedytelser() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        String fnr = "***REMOVED***";
        LocalDate fom = new LocalDate(2015, 1, 1);
        LocalDate tom = new LocalDate(2015, 1, 2);

        doReturn(createKariNordmannUtbetaling()).when(spyService).getWSUtbetalinger(fnr, fom, tom);
        List<Record<Hovedytelse>> hovedytelser = spyService.hentUtbetalinger(fnr, fom, tom);
        assertThat(hovedytelser.size(), is(5));
    }

    @Test(expected = ApplicationException.class)
    public void haandtererIkkeGyldigPeriodeFeilFraTjenesten() throws HentUtbetalingsinformasjonPeriodeIkkeGyldig, HentUtbetalingsinformasjonPersonIkkeFunnet, HentUtbetalingsinformasjonIkkeTilgang {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        String fnr = "***REMOVED***";
        LocalDate fom = new LocalDate(2015, 1, 1);
        LocalDate tom = new LocalDate(2015, 1, 2);
        WSHentUtbetalingsinformasjonRequest request = new WSHentUtbetalingsinformasjonRequest();
        doReturn(request).when(spyService).createRequest(fnr, fom, tom);
        when(utbetalingV1.hentUtbetalingsinformasjon(request)).thenThrow(new HentUtbetalingsinformasjonPeriodeIkkeGyldig());

        spyService.getWSUtbetalinger(fnr, fom, tom);
    }
}