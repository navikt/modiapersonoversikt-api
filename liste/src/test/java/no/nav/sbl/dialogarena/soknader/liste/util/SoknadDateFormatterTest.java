package no.nav.sbl.dialogarena.soknader.liste.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SoknadDateFormatterTest {

    @Test
    public void testPrintShortDate() throws Exception {
        DateTime dt = new DateTime(1980,1,2,12,0);
        assertThat(SoknadDateFormatter.printShortDate(dt), is(equalTo("02.01.1980" )));
    }

    @Test
    public void testPrintShortDateWithNull() throws Exception {
        assertThat(SoknadDateFormatter.printShortDate(null), is(equalTo("" )));
    }
}
