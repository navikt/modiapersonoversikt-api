package no.nav.sbl.dialogarena.soknader.domain;

import no.nav.sbl.dialogarena.soknader.util.SoknadBuilder;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SoknadComparatorTest {

    @Test
    public void shouldSortWithNewestFirst() {
        Soknad newSoknad = new SoknadBuilder().withInnsendtDato(new DateTime(2013, 1, 1, 1, 1)).build();
        Soknad oldSoknad = new SoknadBuilder().withInnsendtDato(new DateTime(2012, 1, 1, 1, 1)).build();
        SoknadComparator soknadComparator = new SoknadComparator();
        assertThat(soknadComparator.compare(newSoknad, oldSoknad), is(-1));
    }

}
