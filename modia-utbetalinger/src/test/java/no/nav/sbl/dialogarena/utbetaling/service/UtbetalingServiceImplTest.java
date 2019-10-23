package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.ApplicationException;
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
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createKariNordmannUtbetaling;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.EKSTRA_SOKEPERIODE;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.getHovedytelseListe;
import static org.hamcrest.Matchers.is;
import static org.joda.time.LocalDate.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UtbetalingServiceImplTest {
    public static final int NUMBER_OF_DAYS_TO_SHOW = 30;

    public static final String FNR = "12345";
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
        WSHentUtbetalingsinformasjonRequest request = utbetalingService.createRequest(FNR, new LocalDate(2015, 1, 1), new LocalDate(2015, 1, 2));
        assertThat(request.getId().getIdent(), is(FNR));
        assertThat(request.getPeriode().getFom(), is(new DateTime(2015, 1, 1, 0, 0)));
        assertThat(request.getPeriode().getTom(), is(new DateTime(2015, 1, 2, 0, 0)));
    }

    @Test
    public void hentUtbetalingerReturnererKorrekteHovedytelser() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        LocalDate fom = now().minusYears(1);
        LocalDate tom = now();

        doReturn(createKariNordmannUtbetaling()).when(spyService).getWSUtbetalinger(FNR, fom, tom);
        List<WSUtbetaling> utbetalingerIPerioden = spyService.hentWSUtbetalinger(FNR, fom, tom);
        List<Hovedytelse> hovedytelser = getHovedytelseListe(utbetalingerIPerioden);
        assertThat(hovedytelser.size(), is(4));
    }

    @Test(expected = ApplicationException.class)
    public void haandtererIkkeGyldigPeriodeFeilFraTjenesten() throws HentUtbetalingsinformasjonPeriodeIkkeGyldig, HentUtbetalingsinformasjonPersonIkkeFunnet, HentUtbetalingsinformasjonIkkeTilgang {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        LocalDate fom = LocalDate.now().withDayOfYear(1);
        LocalDate tom = LocalDate.now().withDayOfYear(2);
        WSHentUtbetalingsinformasjonRequest request = new WSHentUtbetalingsinformasjonRequest();
        doReturn(request).when(spyService).createRequest(FNR, fom.minusDays(EKSTRA_SOKEPERIODE), tom);
        when(utbetalingV1.hentUtbetalingsinformasjon(request)).thenThrow(new HentUtbetalingsinformasjonPeriodeIkkeGyldig());

        spyService.getWSUtbetalinger(FNR, fom, tom);
    }

    @Test
    public void finnerUtbetalingerMedPosteringsdatoUtenforSokeperioden() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        DateTime posteringsdato = DateTime.now().minusDays(2 * NUMBER_OF_DAYS_TO_SHOW);
        DateTime utbetalingsdato = DateTime.now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate startdato = new LocalDate(posteringsdato.plusDays(10));
        LocalDate sluttdato = new LocalDate(utbetalingsdato.plusDays(10));
        List<WSUtbetaling> mockUtbetalingsliste = new ArrayList<>();
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato( utbetalingsdato, null, posteringsdato));

        doReturn(mockUtbetalingsliste).when(spyService).getWSUtbetalinger(FNR, startdato, sluttdato);
        List<WSUtbetaling> utbetalingerIPerioden = spyService.hentWSUtbetalinger(FNR, startdato, sluttdato);
        List<Hovedytelse> hovedytelser = getHovedytelseListe(utbetalingerIPerioden);

        assertThat(hovedytelser.size(), is(2));
    }

    @Test
    public void utelaterUtbetalingerMedPosteringsdatoInnenforOgMedUtbetalingsdatoUtenforSokeperioden() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);

        DateTime posteringsdato = DateTime.now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        DateTime utbetalingsdato = DateTime.now().minusDays(NUMBER_OF_DAYS_TO_SHOW).minusDays(15);
        LocalDate startdato = new LocalDate(posteringsdato.minusDays(2));
        LocalDate sluttdato = new LocalDate(utbetalingsdato.minusDays(2));
        List<WSUtbetaling> mockUtbetalingsliste = new ArrayList<>();
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(utbetalingsdato, null, posteringsdato));

        doReturn(mockUtbetalingsliste).when(spyService).getWSUtbetalinger(FNR, startdato, sluttdato);

        List<WSUtbetaling> filtrertWSUtbetalingListe = spyService.hentWSUtbetalinger(FNR, startdato, sluttdato);
        List<Hovedytelse> hovedytelser = getHovedytelseListe(filtrertWSUtbetalingListe);

        assertThat(hovedytelser.size(), is(0));
    }

    @Test
    public void returnererUtbetalingerMedPosteringsdatoInnenforSokeperiodenOgUtenUtbetalingsdato() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);

        DateTime posteringsdato = DateTime.now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate startdato = new LocalDate(posteringsdato.minusDays(2));
        LocalDate sluttdato = new LocalDate(posteringsdato.plusDays(2));
        List<WSUtbetaling> mockUtbetalingsliste = new ArrayList<>();
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(null, null, posteringsdato));

        doReturn(mockUtbetalingsliste).when(spyService).getWSUtbetalinger(FNR, startdato, sluttdato);

        List<WSUtbetaling> filtrertWSUtbetalingListe = spyService.hentWSUtbetalinger(FNR, startdato, sluttdato);
        List<Hovedytelse> hovedytelser = getHovedytelseListe(filtrertWSUtbetalingListe);

        assertThat(hovedytelser.size(), is(2));
    }

    @Test
    public void returnererUtbetalingerMedForfallsdatoInnenforSokeperiodenOgUtenUtbetalingsdato() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);

        DateTime forfallsdato = DateTime.now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate startdato = new LocalDate(forfallsdato.minusDays(2));
        LocalDate sluttdato = new LocalDate(forfallsdato.plusDays(2));
        List<WSUtbetaling> mockUtbetalingsliste = new ArrayList<>();
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(null, forfallsdato, null));
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(null, forfallsdato, null));

        doReturn(mockUtbetalingsliste).when(spyService).getWSUtbetalinger(FNR, startdato, sluttdato);

        List<WSUtbetaling> filtrertWSUtbetalingListe = spyService.hentWSUtbetalinger(FNR, startdato, sluttdato);
        List<Hovedytelse> hovedytelser = getHovedytelseListe(filtrertWSUtbetalingListe);

        assertThat(hovedytelser.size(), is(4));
    }

    @Test
    public void finnerRiktigAntallUtbetalingerMedUtbetalingsdatoISokeperioden() {
        UtbetalingServiceImpl spyService = spy(utbetalingService);
        DateTime nedreGrense = DateTime.now().minusDays(3 * NUMBER_OF_DAYS_TO_SHOW);
        DateTime utbetalingsdato = DateTime.now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        DateTime ovreGrense = DateTime.now().minusDays(3);
        List<WSUtbetaling> mockUtbetalingsliste = new ArrayList<>();
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(utbetalingsdato, null, nedreGrense));
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(utbetalingsdato, null, nedreGrense.minusDays(3)));
        mockUtbetalingsliste.add(createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(ovreGrense, null, nedreGrense.plusDays(2)));

        LocalDate startdato = new LocalDate(now().minusDays(3 * NUMBER_OF_DAYS_TO_SHOW));
        LocalDate sluttdato = new LocalDate(now().minusDays(3));

        doReturn(mockUtbetalingsliste).when(spyService).getWSUtbetalinger(FNR, startdato, sluttdato);
        List<WSUtbetaling> utbetalingerIPerioden = spyService.hentWSUtbetalinger(FNR, startdato, sluttdato);
        List<Hovedytelse> hovedytelser = getHovedytelseListe(utbetalingerIPerioden);

        assertThat(hovedytelser.size(), is(6));
    }
}