package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.utbetalinger;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.HistoriskUtbetaling;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.utbetalinger.Hovedytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSYtelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSYtelsestyper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.utbetalinger.Transformers.TO_HOVEDYTELSE;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransformersTest {

    private static final Double NETTO_BELOP = 3000.0;
    private static final Double BRUTTO_BELOP = 6000.0;
    private static final Double SKATT_BELOP = 1500.0;

    @Test
    public void skalHandterePaddedeYtelsestypeVerdier() {
        List<WSUtbetaling> utbetalinger = createUtbetalinger("SYKEPENGER         ");

        List<Hovedytelse> resultat = utbetalinger.stream()
                .map(TO_HOVEDYTELSE)
                .filter(UtbetalingUtils::harGyldigUtbetaling)
                .collect(toList());

        assertThat(resultat.size(), is(1));
    }

    @Test
    public void skalHandtereNullYtelsestype() {
        List<WSUtbetaling> utbetalinger = createUtbetalinger(null);

        List<Hovedytelse> resultat = utbetalinger.stream()
                .map(TO_HOVEDYTELSE)
                .filter(UtbetalingUtils::harGyldigUtbetaling)
                .collect(toList());

        assertThat(resultat.size(), is(0));
    }

    @Test
    public void skalHenteRiktigBruttobelop() {
        List<WSUtbetaling> utbetalinger = createUtbetalinger("SYKEPENGER");

        List<Hovedytelse> resultat = utbetalinger.stream()
                .map(TO_HOVEDYTELSE)
                .filter(UtbetalingUtils::harGyldigUtbetaling)
                .collect(toList());

        Hovedytelse hovedytelse = resultat.get(0);
        List<HistoriskUtbetaling> historiskUtbetalinger = hovedytelse.getHistoriskUtbetalinger();
        HistoriskUtbetaling historiskUtbetaling = historiskUtbetalinger.get(0);

        assertThat(resultat.size(), is(1));
        assertThat(historiskUtbetalinger.size(), is(1));
        assertThat(historiskUtbetaling.getNettobelop(), is(NETTO_BELOP));
        assertThat(historiskUtbetaling.getSkattetrekk(), is(SKATT_BELOP));
        assertThat(historiskUtbetaling.getBruttobeloep(), is(BRUTTO_BELOP));
    }

    private List<WSUtbetaling> createUtbetalinger(String ytelsesType) {
        List<WSUtbetaling> utbetalinger = new ArrayList<>();
        WSYtelse sykepenger = new WSYtelse()
                .withYtelsestype(new WSYtelsestyper().withValue(ytelsesType))
                .withYtelseskomponentersum(BRUTTO_BELOP)
                .withYtelseNettobeloep(NETTO_BELOP)
                .withSkattsum(SKATT_BELOP);
        WSUtbetaling utbetaling = new WSUtbetaling().withUtbetalingNettobeloep(1000).withYtelseListe(sykepenger);
        utbetalinger.add(utbetaling);
        return utbetalinger;
    }
}
