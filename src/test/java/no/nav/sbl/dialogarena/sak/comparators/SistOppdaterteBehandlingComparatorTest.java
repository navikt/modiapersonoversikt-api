package no.nav.sbl.dialogarena.sak.comparators;

import no.nav.sbl.dialogarena.sak.domain.widget.Tema;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SistOppdaterteBehandlingComparatorTest {

    @Test
    public void skalSortereBehandlingerOmvendtKronologisk() {
        SistOppdaterteBehandlingComparator comparator = new SistOppdaterteBehandlingComparator();
        assertThat(comparator.compare(new Tema("DAG").withSistOppdaterteBehandling(DateTime.now()), new Tema("DAG").withSistOppdaterteBehandling(DateTime.now().plusDays(1))), is(1));
    }

}
