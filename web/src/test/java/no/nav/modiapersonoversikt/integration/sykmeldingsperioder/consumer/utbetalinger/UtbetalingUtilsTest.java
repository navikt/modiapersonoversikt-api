package no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.utbetalinger;

import no.nav.modiapersonoversikt.integration.sykmeldingsperioder.domain.HistoriskUtbetaling;
import no.nav.modiapersonoversikt.integration.sykmeldingsperioder.domain.utbetalinger.Hovedytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.utbetalinger.UtbetalingUtils.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UtbetalingUtilsTest {

    public static final String RIKTIG_TYPE = "riktig";
    public static final String FEIL_TYPE = "feil";

    @Test
    public void utbetalingErInnenforSokeperioden() {
        WSUtbetaling utbetaling = new WSUtbetaling().withUtbetalingsdato(DateTime.now().plusDays(1));

        assertThat(utbetalingInnenforSokeperioden(utbetaling, LocalDate.now(), LocalDate.now().plusMonths(1)), is(true));
    }

    @Test
    public void utbetalingErUtenforSokeperioden() {
        WSUtbetaling utbetaling = new WSUtbetaling().withUtbetalingsdato(DateTime.now().plusMonths(2));

        assertThat(utbetalingInnenforSokeperioden(utbetaling, LocalDate.now(), LocalDate.now().plusMonths(1)), is(false));
    }

    @Test
    public void utbetalingErGyldig() {
        Hovedytelse hovedytelse = new Hovedytelse();
        hovedytelse.setHistoriskUtbetalinger(Collections.singletonList(new HistoriskUtbetaling()));

        assertThat(harGyldigUtbetaling(hovedytelse), is(true));
    }


    @Test
    public void historiskUtbetalingIkkeInitialisert() {
        Hovedytelse hovedytelse = new Hovedytelse();

        assertThat(harGyldigUtbetaling(hovedytelse), is(false));
    }

    @Test
    public void historiskUtbetalingErTom() {
        Hovedytelse hovedytelse = new Hovedytelse();
        hovedytelse.setHistoriskUtbetalinger(new ArrayList<>());

        assertThat(harGyldigUtbetaling(hovedytelse), is(false));
    }


    @Test
    public void fjernerRiktigeHistoriskeUtbetalinger() {
        Hovedytelse hovedytelse = new Hovedytelse();
        HistoriskUtbetaling riktig = new HistoriskUtbetaling().withYtelsesType(RIKTIG_TYPE);
        HistoriskUtbetaling feil = new HistoriskUtbetaling().withYtelsesType(FEIL_TYPE);
        hovedytelse.setHistoriskUtbetalinger(Arrays.asList(riktig, feil));

        Hovedytelse res = fjernHistoriskUtbetalingerMedFeilUtbetalingsType(hovedytelse, RIKTIG_TYPE);

        assertThat(res.getHistoriskUtbetalinger().contains(riktig), is(true));
        assertThat(res.getHistoriskUtbetalinger().contains(feil), is(false));

    }


}
