package no.nav.modiapersonoversiktproxy.consumer.infortrygd.domain.foreldrepenger;


import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.foreldrepenger.Foedsel;
import org.joda.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class FoedselTest {
    public static final LocalDate TERMINDATO = new LocalDate(2015, 1, 1);

    @Test
    public void testBean() {
        Foedsel foedsel = new Foedsel();
        foedsel.setTermin(TERMINDATO);
        assertEquals(TERMINDATO, foedsel.getTermin());

        Foedsel foedsel2 = new Foedsel(TERMINDATO);
        assertEquals(TERMINDATO, foedsel2.getTermin());
    }
}
