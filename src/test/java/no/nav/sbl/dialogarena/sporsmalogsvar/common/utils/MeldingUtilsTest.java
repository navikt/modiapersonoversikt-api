package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSvar;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.SVAR;
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
                new Melding("1", "1", INNGAENDE, now()),
                new Melding("2", "1", UTGAENDE, now()),
                new Melding("3", "2", INNGAENDE, now())));

        assertThat(traader.size(), is(equalTo(2)));
        assertThat(traader.get("1").size(), is(equalTo(2)));
        assertThat(traader.get("2").size(), is(equalTo(1)));

    }

    @Test
    public void testStatusTransformer() {
        XMLBehandlingsinformasjonV2 behandlingsinformasjonV2 = new XMLBehandlingsinformasjonV2();
        behandlingsinformasjonV2.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLSporsmal()));

        behandlingsinformasjonV2.withHenvendelseType(SPORSMAL.name());
        behandlingsinformasjonV2.withOpprettetDato(now());
        assertThat(STATUS.transform(behandlingsinformasjonV2), is(equalTo(IKKE_BESVART)));

        behandlingsinformasjonV2.withOpprettetDato(now().minusHours(BESVARINGSFRIST_TIMER + 1));
        assertThat(STATUS.transform(behandlingsinformasjonV2), is(equalTo(IKKE_BESVART_INNEN_FRIST)));

        behandlingsinformasjonV2.withHenvendelseType(SVAR.name());
        behandlingsinformasjonV2.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLSvar()));
        assertThat(STATUS.transform(behandlingsinformasjonV2), is(equalTo(IKKE_LEST_AV_BRUKER)));

        behandlingsinformasjonV2.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLSvar().withLestDato(now())));
        assertThat(STATUS.transform(behandlingsinformasjonV2), is(equalTo(LEST_AV_BRUKER)));
    }
}
