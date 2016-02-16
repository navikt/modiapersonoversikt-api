package no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator.OmvendtKronologiskHendelseComparator;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BEHANDLING_DATO;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering.SKJEMANUMMER_REF;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OmvendtKronologiskHendelseComparatorTest {

    @Test
    public void sortOrderTest() {
        DateTime now = DateTime.now();
        Record<GenerellBehandling> hendelse1 = new Record<GenerellBehandling>()
                .with(SKJEMANUMMER_REF, "Hendelse1")
                .with(BEHANDLING_DATO, now.minusDays(1));
        Record<GenerellBehandling> hendelse2 = new Record<GenerellBehandling>()
                .with(SKJEMANUMMER_REF, "Hendelse2")
                .with(BEHANDLING_DATO, now);
        Record<GenerellBehandling> hendelse3 = new Record<GenerellBehandling>()
                .with(SKJEMANUMMER_REF, "Hendelse3")
                .with(BEHANDLING_DATO, now.minusDays(2));
        Record<GenerellBehandling> hendelse4 = new Record<GenerellBehandling>()
                .with(SKJEMANUMMER_REF, "Hendelse4")
                .with(BEHANDLING_DATO, now.plusHours(23));
        Record<GenerellBehandling> hendelse5 = new Record<GenerellBehandling>()
                .with(SKJEMANUMMER_REF, "Hendelse5")
                .with(BEHANDLING_DATO, now.plusHours(10));

        List<Record<GenerellBehandling>> sortedList = on(asList(hendelse1, hendelse2, hendelse3, hendelse4, hendelse5)).collect(new OmvendtKronologiskHendelseComparator());

        assertThat(sortedList.get(0).get(SKJEMANUMMER_REF), is("Hendelse4"));
        assertThat(sortedList.get(1).get(SKJEMANUMMER_REF), is("Hendelse5"));
        assertThat(sortedList.get(2).get(SKJEMANUMMER_REF), is("Hendelse2"));
        assertThat(sortedList.get(3).get(SKJEMANUMMER_REF), is("Hendelse1"));
        assertThat(sortedList.get(4).get(SKJEMANUMMER_REF), is("Hendelse3"));
    }
}
