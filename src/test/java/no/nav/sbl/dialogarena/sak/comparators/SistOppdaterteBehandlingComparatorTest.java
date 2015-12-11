package no.nav.sbl.dialogarena.sak.comparators;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SistOppdaterteBehandlingComparatorTest {

    @Test
    public void skalSortereBehandlingerOmvendtKronologisk() {
        GenerellBehandling behandling2013 = new GenerellBehandling().withBehandlingsDato(new DateTime().withYear(2013));
        GenerellBehandling behandling2014 = new GenerellBehandling().withBehandlingsDato(new DateTime().withYear(2014));
        SistOppdaterteBehandlingComparator comparator = new SistOppdaterteBehandlingComparator();

        assertThat(comparator.compare(new TemaVM().withSistOppdaterteBehandling(behandling2013), new TemaVM().withSistOppdaterteBehandling(behandling2014)), is(1));
    }

}
