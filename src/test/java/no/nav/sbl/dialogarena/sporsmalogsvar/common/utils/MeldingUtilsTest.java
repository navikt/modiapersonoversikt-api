package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.BESVARINGSFRIST_TIMER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.STATUS;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.UTGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.IKKE_BESVART_INNEN_FRIST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.LEST_AV_BRUKER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class MeldingUtilsTest {
    @Test
    public void testSkillUtTraader() {
        Map<String, List<Melding>> traader = skillUtTraader(asList(
                new Melding("1", "1", INNGAENDE, now(), ""),
                new Melding("2", "1", UTGAENDE, now(), ""),
                new Melding("3", "2", INNGAENDE, now(), "")));

        assertThat(traader.size(), is(equalTo(2)));
        assertThat(traader.get("1").size(), is(equalTo(2)));
        assertThat(traader.get("2").size(), is(equalTo(1)));

    }

    @Test
    public void testStatusTransformer() {
        WSMelding wsMelding = new WSMelding();

        wsMelding.withMeldingsType(WSMeldingstype.INNGAENDE);
        wsMelding.withOpprettetDato(now());
        assertThat(STATUS.transform(wsMelding), is(equalTo(IKKE_BESVART)));

        wsMelding.withOpprettetDato(now().minusHours(BESVARINGSFRIST_TIMER + 1));
        assertThat(STATUS.transform(wsMelding), is(equalTo(IKKE_BESVART_INNEN_FRIST)));

        wsMelding.withMeldingsType(WSMeldingstype.UTGAENDE);
        assertThat(STATUS.transform(wsMelding), is(equalTo(IKKE_LEST_AV_BRUKER)));

        wsMelding.withLestDato(now());
        assertThat(STATUS.transform(wsMelding), is(equalTo(LEST_AV_BRUKER)));
    }
}
