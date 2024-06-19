package no.nav.modiapersonoversiktproxy.consumer.infortrygd.domain.foreldrepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.foreldrepenger.Adopsjon;
import org.joda.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AdopsjonTest {

    public static final LocalDate OMSORGSOVERTAKELSE = new LocalDate(2013, 1, 1);

    @Test
    public void testBean() {
        Adopsjon adopsjon = new Adopsjon();
        adopsjon.setOmsorgsovertakelse(OMSORGSOVERTAKELSE);

        assertEquals(OMSORGSOVERTAKELSE, adopsjon.getOmsorgsovertakelse());

        Adopsjon adopsjon2 = new Adopsjon(OMSORGSOVERTAKELSE);

        assertEquals(OMSORGSOVERTAKELSE, adopsjon2.getOmsorgsovertakelse());
    }
}
