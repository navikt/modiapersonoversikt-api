package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.UnderytelseGammel;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.apache.commons.collections15.Transformer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.*;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.Transformers.HOVEDYTELSE_TRANSFORMER;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.Transformers.createHovedytelser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class TransformersTest {

    private List<WSUtbetaling> wsUtbetalinger;

    @Before
    public void setup() {

        WSUtbetaling wsUtbetaling1 = WSUtbetalingTestData.createUtbetaling1();
        WSUtbetaling wsUtbetaling2 = WSUtbetalingTestData.createUtbetaling2();

        wsUtbetalinger = asList(wsUtbetaling1, wsUtbetaling2);
    }

    @Test
    public void testLagUtbetalinger() throws Exception {

        List<Hovedytelse> hovedytelser = on(wsUtbetalinger).flatmap(HOVEDYTELSE_TRANSFORMER).collect();

        Transformer<Hovedytelse, String> utbetalingsmelding = new Transformer<Hovedytelse, String>() {
            @Override
            public String transform(Hovedytelse hovedytelse) {
                return hovedytelse.getUtbetalingsmelding();
            }
        };

        Transformer<Hovedytelse, Double> sumSkatt = new Transformer<Hovedytelse, Double>() {
            @Override
            public Double transform(Hovedytelse hovedytelse) {
                return hovedytelse.getSumSkatt();
            }
        };

        on(hovedytelser).map(sumSkatt).reduce(sumDouble);


        on(hovedytelser).filter(where(utbetalingsmelding, equalTo("hei"))).collect(compareWith(utbetalingsmelding));


        List<Utbetaling> utbetalinger = createHovedytelser(wsUtbetalinger, "fnr");

        assertThat(utbetalinger.size(), is(2));


        assertThat(utbetalinger.get(0).getMelding(), is("Dette er bilagsmelding 1\nDette er bilagsmelding 2"));

        List<UnderytelseGammel> underytelser1 = utbetalinger.get(0).getUnderytelser();
        assertThat(underytelser1.size(), is(4));
        assertThat(underytelser1.get(0).getTittel(), is(GRUNNBELOP));
        assertThat(underytelser1.get(0).getBelop(), is(greaterThanOrEqualTo(0D)));
        assertThat(underytelser1.get(1).getTittel(), is(GRUNNBELOP));
        assertThat(underytelser1.get(1).getBelop(), is(greaterThanOrEqualTo(0D)));
        assertThat(underytelser1.get(2).getTittel(), is(FORSKUDDSTREKK_SKATT));
        assertThat(underytelser1.get(2).getBelop(), is(lessThanOrEqualTo(0D)));
        assertThat(underytelser1.get(3).getTittel(), is(FORSKUDDSTREKK_SKATT));
        assertThat(underytelser1.get(3).getBelop(), is(lessThanOrEqualTo(0D)));


        assertThat(utbetalinger.get(1).getMelding(), is("bilag2"));

        List<UnderytelseGammel> underytelser2 = utbetalinger.get(1).getUnderytelser();
        assertThat(underytelser2.size(), is(5));
        assertThat(underytelser2.get(0).getTittel(), is(TILLEGGSYTELSE));
        assertThat(underytelser2.get(0).getBelop(), is(greaterThanOrEqualTo(0D)));
        assertThat(underytelser2.get(1).getTittel(), is(TILLEGGSYTELSE));
        assertThat(underytelser2.get(1).getBelop(), is(greaterThanOrEqualTo(0D)));
        assertThat(underytelser2.get(2).getTittel(), is(FORELDREPENGER));
        assertThat(underytelser2.get(2).getBelop(), is(greaterThanOrEqualTo(0D)));
        assertThat(underytelser2.get(3).getTittel(), is(TILLEGGSYTELSE_TILBAKEBETALT));
        assertThat(underytelser2.get(3).getBelop(), is(lessThanOrEqualTo(0D)));
        assertThat(underytelser2.get(4).getTittel(), is(FORSKUDDSTREKK_SKATT));
        assertThat(underytelser2.get(4).getBelop(), is(lessThanOrEqualTo(0D)));

    }
}
