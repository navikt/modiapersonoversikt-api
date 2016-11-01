package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TraadTest {

    @Test
    public void traaderSkalSorteresMedNyesteMeldingForst() throws Exception {
        final Traad traad = new Traad("traadId", 2,
                Arrays.asList(
                        new Melding().withId("melding_1").withOpprettetDato(new DateTime(1)),
                        new Melding().withId("melding_2").withOpprettetDato(new DateTime(2)),
                        new Melding().withId("melding_3").withOpprettetDato(new DateTime(3))
                ));

        assertThat(traad.meldinger.get(0).id, is("melding_3"));
    }

}