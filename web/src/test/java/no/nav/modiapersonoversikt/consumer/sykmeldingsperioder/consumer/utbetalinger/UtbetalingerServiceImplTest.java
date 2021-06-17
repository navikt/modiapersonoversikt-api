package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.utbetalinger;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.HistoriskUtbetaling;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.utbetalinger.Hovedytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSForespurtPeriode;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.utbetalinger.UtbetalingerMockFactory.*;
import static org.hamcrest.Matchers.is;
import static org.joda.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class UtbetalingerServiceImplTest {

    @Mock
    private UtbetalingV1 utbetalingV1;

    @InjectMocks
    private UtbetalingerServiceImpl utbetalingerService = new UtbetalingerServiceImpl();
    public static final String FNR = "11223312345";
    public static final String ANNET_FNR = "10108000398";

    @Test
    public void generererKorrektPeriode() {
        WSForespurtPeriode periode = utbetalingerService.createPeriode(new LocalDate(2015, 1, 1), new LocalDate(2015, 1, 2));
        assertThat(periode.getFom(), is(new DateTime(2015, 1, 1, 0, 0)));
        assertThat(periode.getTom(), is(new DateTime(2015, 1, 2, 0, 0)));
    }

    @Test
    public void generererKorrektRequests() {
        WSHentUtbetalingsinformasjonRequest request = utbetalingerService.createRequest("123456789", new LocalDate(2015, 1, 1), new LocalDate(2015, 1, 2));
        assertThat(request.getId().getIdent(), is("123456789"));
        assertThat(request.getPeriode().getFom(), is(new DateTime(2015, 1, 1, 0, 0)));
        assertThat(request.getPeriode().getTom(), is(new DateTime(2015, 1, 2, 0, 0)));
    }

    @Test
    public void hentUtbetalingerReturnererKorrekteHovedytelser() {
        UtbetalingerServiceImpl spyService = spy(utbetalingerService);
        LocalDate fom = now().minusYears(1);
        LocalDate tom = now();

        doReturn(createKariNordmannUtbetaling()).when(spyService).getWSUtbetalinger(FNR, fom, tom);
        List<Hovedytelse> hovedytelser = spyService.hentUtbetalinger(FNR, fom, tom, YTELSESTYPE_SYKEPENGER);
        assertThat(hovedytelser.size(), is(1));
        assertThat(hovedytelser.get(0).getHistoriskUtbetalinger().size(), is(4));

        HistoriskUtbetaling historiskUtbetaling = hovedytelser.get(0).getHistoriskUtbetalinger().get(0);
        assertThat(historiskUtbetaling.getType(), is(YTELSESTYPE_SYKEPENGER));
        assertThat(historiskUtbetaling.getNettobelop(), is(NETTOBELOP));
        assertThat(historiskUtbetaling.getSkattetrekk(), is(SKATTESUM));
        assertThat(historiskUtbetaling.getBruttobeloep(), is(NETTOBELOP+SKATTESUM));
    }

    @Test
    public void hentUtbetalingForSykepengerSkalBareHenteForSykepenger() {
        UtbetalingerServiceImpl spyService = spy(utbetalingerService);
        LocalDate fom = now().minusYears(1);
        LocalDate tom = now();
        doReturn(createUtbetalingMedSykepengerOgForeldrepenger()).when(spyService).getWSUtbetalinger(ANNET_FNR, fom, tom);

        List<Hovedytelse> hovedytelser = spyService.hentUtbetalinger(ANNET_FNR, fom, tom, YTELSESTYPE_SYKEPENGER);
        assertThat(hovedytelser.size(), is(1));
        assertThat(hovedytelser.get(0).getHistoriskUtbetalinger().size(), is(3));

        HistoriskUtbetaling historiskUtbetaling = hovedytelser.get(0).getHistoriskUtbetalinger().get(0);
        assertThat(historiskUtbetaling.getType(), is(YTELSESTYPE_SYKEPENGER));
        assertThat(historiskUtbetaling.getNettobelop(), is(NETTOBELOP));
        assertThat(historiskUtbetaling.getSkattetrekk(), is(SKATTESUM));
        assertThat(historiskUtbetaling.getBruttobeloep(), is(NETTOBELOP+SKATTESUM));
    }

    @Test
    public void hentUtbetalingForSykepengerSkalBareHenteForForeldrepenger() {
        UtbetalingerServiceImpl spyService = spy(utbetalingerService);
        String fnr = "10108000398";
        LocalDate fom = now().minusYears(1);
        LocalDate tom = now();
        doReturn(createUtbetalingMedSykepengerOgForeldrepenger()).when(spyService).getWSUtbetalinger(fnr, fom, tom);

        List<Hovedytelse> hovedytelser = spyService.hentUtbetalinger(fnr, fom, tom, YTELSESTYPE_FORELDREPENGER);
        assertThat(hovedytelser.size(), is(1));
        assertThat(hovedytelser.get(0).getHistoriskUtbetalinger().size(), is(1));
    }
}
