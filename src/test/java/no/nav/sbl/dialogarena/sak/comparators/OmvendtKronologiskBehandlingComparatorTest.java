package no.nav.sbl.dialogarena.sak.comparators;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class OmvendtKronologiskBehandlingComparatorTest {

    @Test
    public void skalSortereBehandlingerOmvendtKronologisk() {
        Behandling behandling2013 = new Behandling().withBehandlingsDato(new DateTime().withYear(2013));
        Behandling behandling2014 = new Behandling().withBehandlingsDato(new DateTime().withYear(2014));
        OmvendtKronologiskBehandlingComparator comparator = new OmvendtKronologiskBehandlingComparator();

        assertThat(comparator.compare(behandling2013, behandling2014), is(1));
    }

}
