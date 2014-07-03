package no.nav.sbl.dialogarena.sak.comparators;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OmvendtKronologiskBehandlingComparatorTest {

    @Test
    public void skalSortereBehandlingerOmvendtKronologisk() {
        GenerellBehandling behandling2013 = new GenerellBehandling().withBehandlingsDato(new DateTime().withYear(2013));
        GenerellBehandling behandling2014 = new GenerellBehandling().withBehandlingsDato(new DateTime().withYear(2014));
        OmvendtKronologiskBehandlingComparator comparator = new OmvendtKronologiskBehandlingComparator();

        assertThat(comparator.compare(behandling2013, behandling2014), is(1));
    }

}
