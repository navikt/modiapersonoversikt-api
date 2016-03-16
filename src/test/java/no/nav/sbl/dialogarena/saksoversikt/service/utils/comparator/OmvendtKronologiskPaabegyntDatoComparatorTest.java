package no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.joda.time.DateTime.parse;
import static org.junit.Assert.assertThat;

public class OmvendtKronologiskPaabegyntDatoComparatorTest {

    @Test
    public void shouldSortSistePaabegynteHenvendelseFirst() {
        Soknad henvendelsePaabegynt3Feb = new Soknad().withSistEndretDato(parse("2013-02-03"));
        Soknad henvendelsePaabegynt5Jan = new Soknad().withSistEndretDato(parse("2013-01-05"));

        List<Soknad> henvendelseList = asList(henvendelsePaabegynt5Jan, henvendelsePaabegynt3Feb);
        sort(henvendelseList, new OmvendtKronologiskSistEndretDatoComparator());
        assertThat(henvendelseList, contains(
                is(henvendelsePaabegynt3Feb),
                is(henvendelsePaabegynt5Jan)));
    }

}
