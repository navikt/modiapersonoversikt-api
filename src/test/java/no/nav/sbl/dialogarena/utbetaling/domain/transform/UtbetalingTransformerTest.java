package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.FORELDREPENGER;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.FORSKUDDSTREKK_SKATT;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.GRUNNBELOP;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.TILLEGGSYTELSE;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.TILLEGGSYTELSE_TILBAKEBETALT;
import static no.nav.sbl.dialogarena.utbetaling.domain.transform.UtbetalingTransformer.lagUtbetalinger;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class UtbetalingTransformerTest {

    private List<WSUtbetaling> wsUtbetalinger;

    @Before
    public void setup() {

        WSUtbetaling wsUtbetaling1 = WSUtbetalingTestData.createUtbetaling1();
        WSUtbetaling wsUtbetaling2 = WSUtbetalingTestData.createUtbetaling2();

        wsUtbetalinger = asList(wsUtbetaling1, wsUtbetaling2);
    }

    @Test
    public void testLagUtbetalinger() throws Exception {
        List<Utbetaling> utbetalinger = lagUtbetalinger(wsUtbetalinger, "fnr");

        assertThat(utbetalinger.size(), is(2));


        assertThat(utbetalinger.get(0).getMelding(), is("Dette er bilagsmelding 1. Dette er bilagsmelding 2"));

        List<Underytelse> underytelser1 = utbetalinger.get(0).getUnderytelser();
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

        List<Underytelse> underytelser2 = utbetalinger.get(1).getUnderytelser();
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
