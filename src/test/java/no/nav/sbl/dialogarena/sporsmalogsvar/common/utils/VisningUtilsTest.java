package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import org.junit.Test;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.getStatusKlasse;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.lagMeldingOverskriftKey;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.LEST_AV_BRUKER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.joda.time.DateTime.now;

public class VisningUtilsTest {
    @Test
    public void testGetStatusKlasse() throws Exception {
        assertThat(getStatusKlasse(IKKE_BESVART),               is(equalTo("status ikke-besvart")));
        assertThat(getStatusKlasse(LEST_AV_BRUKER),             is(equalTo("status lest-av-bruker")));
        assertThat(getStatusKlasse(IKKE_LEST_AV_BRUKER),        is(equalTo("status ikke-lest-av-bruker")));
    }

    @Test
    public void testLagMeldingOverskriftKey() throws Exception {
        assertThat(lagMeldingOverskriftKey(new Melding("", SPORSMAL, now())),       is(equalTo("melding.overskrift.sporsmal")));
        assertThat(lagMeldingOverskriftKey(new Melding("", SVAR, now())),           is(equalTo("melding.overskrift.svar")));
        assertThat(lagMeldingOverskriftKey(new Melding("", SAMTALEREFERAT, now())), is(equalTo("melding.overskrift.samtalereferat")));
    }
}
