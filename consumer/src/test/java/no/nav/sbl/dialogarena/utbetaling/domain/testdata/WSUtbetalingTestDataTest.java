package no.nav.sbl.dialogarena.utbetaling.domain.testdata;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSBilag;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSPosteringsdetaljer;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WSUtbetalingTestDataTest {

    @Test
    public void testBelopITestData() throws Exception {
        List<WSUtbetaling> utbetalinger = WSUtbetalingTestData.getWsUtbetalinger("");

        for (WSUtbetaling utbetaling : utbetalinger) {

            Double trekk = utbetaling.getTrekk();
            Double bruttobelop = utbetaling.getBruttobelop();
            Double nettobelop = utbetaling.getNettobelop();
            assertThat(nettobelop, is(bruttobelop + trekk)); // trekk er et negativt tall

            Double sum = 0.0;
            for (WSBilag bilag : utbetaling.getBilagListe()) {
                for (WSPosteringsdetaljer detalj : bilag.getPosteringsdetaljerListe()) {
                    if (!"Skatt".equalsIgnoreCase(detalj.getKontoBeskrHoved())) {
                        sum += detalj.getBelop();
                    }
                }
            }
            assertThat(bruttobelop, is(sum));
        }
    }
}
