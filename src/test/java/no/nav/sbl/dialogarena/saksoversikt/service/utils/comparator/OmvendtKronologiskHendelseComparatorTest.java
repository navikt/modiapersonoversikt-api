package no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import org.junit.Test;
import java.util.List;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.*;
import static org.junit.Assert.assertThat;

public class OmvendtKronologiskHendelseComparatorTest {

    @Test
    public void sortOrderTest() {
        Behandling hendelse1 = new Behandling()
                .withSkjemanummerRef("Hendelse1")
                .withBehandlingsDato(now().minusDays(1));
        Behandling hendelse2 = new Behandling()
                .withSkjemanummerRef("Hendelse2")
                .withBehandlingsDato(now());
        Behandling hendelse3 = new Behandling()
                .withSkjemanummerRef("Hendelse3")
                .withBehandlingsDato(now().minusDays(2));
        Behandling hendelse4 = new Behandling()
                .withSkjemanummerRef("Hendelse4")
                .withBehandlingsDato(now().plusHours(23));
        Behandling hendelse5 = new Behandling()
                .withSkjemanummerRef("Hendelse5")
                .withBehandlingsDato(now().plusHours(10));

        List<Behandling> sortedList = asList(hendelse1, hendelse2, hendelse3, hendelse4, hendelse5).stream()
                .sorted(new OmvendtKronologiskHendelseComparator())
                .collect(toList());

        assertThat(sortedList.get(0).getSkjemanummerRef(), is("Hendelse4"));
        assertThat(sortedList.get(1).getSkjemanummerRef(), is("Hendelse5"));
        assertThat(sortedList.get(2).getSkjemanummerRef(), is("Hendelse2"));
        assertThat(sortedList.get(3).getSkjemanummerRef(), is("Hendelse1"));
        assertThat(sortedList.get(4).getSkjemanummerRef(), is("Hendelse3"));
    }
}
