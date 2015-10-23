package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonIkkeTilgang;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSForespurtPeriode;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createKariNordmannUtbetaling;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetalingMedValgtUtbetalingOgPosteringsdato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.EKSTRA_SOKEPERIODE;
import static org.hamcrest.Matchers.is;
import static org.joda.time.LocalDate.now;
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
        LocalDate fom = now().minusYears(1);
        LocalDate tom = now();

        doReturn(createKariNordmannUtbetaling()).when(spyService).getWSUtbetalinger(fnr, fom, tom);
        List<Record<Hovedytelse>> hovedytelser = spyService.hentUtbetalinger(fnr, fom, tom);
        assertThat(hovedytelser.size(), is(4));
    }

    @Test(expected = ApplicationException.class)
    public void haandtererIkkeGyldigPeriodeFeilFraTjenesten() throws HentUtbetalingsinformasjonPeriodeIkkeGyldig, HentUtbetalingsinformasjonPersonIkkeFunnet, HentUtbetalingsinformasjonIkkeTilgang {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        String fnr = "***REMOVED***";
        LocalDate fom = new LocalDate(2015, 1, 1);
        LocalDate tom = new LocalDate(2015, 1, 2);
        WSHentUtbetalingsinformasjonRequest request = new WSHentUtbetalingsinformasjonRequest();
        doReturn(request).when(spyService).createRequest(fnr, fom.minusDays(EKSTRA_SOKEPERIODE), tom);
        when(utbetalingV1.hentUtbetalingsinformasjon(request)).thenThrow(new HentUtbetalingsinformasjonPeriodeIkkeGyldig());

        spyService.getWSUtbetalinger(fnr, fom, tom);
    }

    @Test
    public void finnerUtbetalingerMedPosteringsdatoUtenforSokeperioden() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        String fnr = "***REMOVED***";
        DateTime posteringsdato = DateTime.now().minusMonths(2);
        DateTime utbetalingsdato = DateTime.now().minusMonths(1);
        LocalDate startdato = new LocalDate(posteringsdato.plusDays(10));
        LocalDate sluttdato = new LocalDate(utbetalingsdato.plusDays(10));
        List<WSUtbetaling> mockUtbetalingsliste = new ArrayList<>();
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingOgPosteringsdato(posteringsdato, utbetalingsdato));

        doReturn(mockUtbetalingsliste).when(spyService).getWSUtbetalinger(fnr, startdato, sluttdato);
        List<Record<Hovedytelse>> hovedytelser = spyService.hentUtbetalinger(fnr, startdato, sluttdato);

        assertThat(hovedytelser.size(), is(1));
    }

    @Test
    public void utelaterUtbetalingerMedPosteringsdatoInnenforOgMedUtbetalingsdatoUntenforSokeperioden() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        String fnr = "***REMOVED***";
        DateTime posteringsdato = DateTime.now().minusMonths(1);
        DateTime utbetalingsdato = DateTime.now().minusMonths(1).minusDays(15);
        LocalDate startdato = new LocalDate(posteringsdato.minusDays(2));
        LocalDate sluttdato = new LocalDate(utbetalingsdato.minusDays(2));
        List<WSUtbetaling> mockUtbetalingsliste = new ArrayList<>();
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingOgPosteringsdato(posteringsdato, utbetalingsdato));

        doReturn(mockUtbetalingsliste).when(spyService).getWSUtbetalinger(fnr, startdato, sluttdato);
        List<Record<Hovedytelse>> hovedytelser = spyService.hentUtbetalinger(fnr, startdato, sluttdato);

        assertThat(hovedytelser.size(), is(0));
    }

    @Test
    public void finnerRiktigAntallUtbetalingerMedUtbetalingsdatoISokeperioden() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        String fnr = "***REMOVED***";
        DateTime nedreGrense = DateTime.now().minusMonths(3);
        DateTime utbetalingsdato = DateTime.now().minusMonths(1);
        DateTime ovreGrense = DateTime.now().minusDays(3);
        List<WSUtbetaling> mockUtbetalingsliste = new ArrayList<>();
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingOgPosteringsdato(nedreGrense, utbetalingsdato));
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingOgPosteringsdato(nedreGrense.minusDays(3), utbetalingsdato));
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingOgPosteringsdato(nedreGrense.plusDays(2), ovreGrense));
        
        LocalDate startdato = new LocalDate(now().minusMonths(3));
        LocalDate sluttdato = new LocalDate(now().minusDays(3));
        
        doReturn(mockUtbetalingsliste).when(spyService).getWSUtbetalinger(fnr, startdato, sluttdato);
        List<Record<Hovedytelse>> hovedytelser = spyService.hentUtbetalinger(fnr, startdato, sluttdato);
        
        assertThat(hovedytelser.size(), is(3));
    }
}