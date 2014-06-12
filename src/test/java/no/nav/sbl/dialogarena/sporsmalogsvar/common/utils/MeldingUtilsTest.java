package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.BESVARINGSFRIST_TIMER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.STATUS;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.SVAR;
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
        Melding melding1 = new Melding("1", SPORSMAL, now());
        melding1.traadId = "1";
        Melding melding2 = new Melding("2", SVAR, now());
        melding2.traadId = "1";
        Melding melding3 = new Melding("3", SPORSMAL, now());
        melding3.traadId = "2";
        Map<String, List<Melding>> traader = skillUtTraader(asList(melding1, melding2, melding3));

        assertThat(traader.size(), is(equalTo(2)));
        assertThat(traader.get("1").size(), is(equalTo(2)));
        assertThat(traader.get("2").size(), is(equalTo(1)));

    }

    @Test
    public void testStatusTransformer() {
        XMLBehandlingsinformasjon behandlingsinformasjonV2 = new XMLBehandlingsinformasjon();
        behandlingsinformasjonV2.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLSporsmal()));

        behandlingsinformasjonV2.withHenvendelseType(XMLHenvendelseType.SPORSMAL.name());
        behandlingsinformasjonV2.withOpprettetDato(now());
        assertThat(STATUS.transform(behandlingsinformasjonV2), is(equalTo(IKKE_BESVART)));

        behandlingsinformasjonV2.withOpprettetDato(now().minusHours(BESVARINGSFRIST_TIMER + 1));
        assertThat(STATUS.transform(behandlingsinformasjonV2), is(equalTo(IKKE_BESVART_INNEN_FRIST)));

        behandlingsinformasjonV2.withHenvendelseType(XMLHenvendelseType.SVAR.name());
        behandlingsinformasjonV2.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLSvar()));
        assertThat(STATUS.transform(behandlingsinformasjonV2), is(equalTo(IKKE_LEST_AV_BRUKER)));

        behandlingsinformasjonV2.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLSvar().withLestDato(now())));
        assertThat(STATUS.transform(behandlingsinformasjonV2), is(equalTo(LEST_AV_BRUKER)));
    }

    @Test(expected = ApplicationException.class)
    public void testStatusTransformerUkjentType() {
        STATUS.transform(new XMLBehandlingsinformasjon().withHenvendelseType(""));
    }

    @Test
    public void testTilMeldingTransformer_medSporsmal() {
        DateTime opprettet = DateTime.now();
        String behandlingsId = "1";
        String fritekst = "fritekst";
        String tema = "tema";
        XMLSporsmal xmlSporsmal = new XMLSporsmal().withFritekst(fritekst).withTemagruppe(tema);

        Melding melding = TIL_MELDING.transform(lagXMLBehandlingsInformasjon(behandlingsId, opprettet, XMLHenvendelseType.SPORSMAL.name(), xmlSporsmal));

        assertThat(melding.id, is(equalTo(behandlingsId)));
        assertThat(melding.traadId, is(equalTo(behandlingsId)));
        assertThat(melding.opprettetDato, is(equalTo(opprettet)));
        assertThat(melding.meldingstype, is(equalTo(SPORSMAL)));
        assertThat(melding.fritekst, is(equalTo(fritekst)));
        assertThat(melding.tema, is(equalTo(tema)));
    }

    @Test
    public void testTilMeldingTransformer_medSvar() {
        DateTime opprettet = DateTime.now().minusDays(1);
        DateTime lest = DateTime.now();
        String behandlingsId = "1";
        String sporsmalsId = "2";
        String fritekst = "fritekst";
        String tema = "tema";
        XMLSvar xmlSvar = new XMLSvar().withSporsmalsId(sporsmalsId).withTemagruppe(tema).withLestDato(lest).withFritekst(fritekst);

        Melding melding = TIL_MELDING.transform(lagXMLBehandlingsInformasjon(behandlingsId, opprettet, XMLHenvendelseType.SVAR.name(), xmlSvar));

        assertThat(melding.id, is(equalTo(behandlingsId)));
        assertThat(melding.traadId, is(equalTo(sporsmalsId)));
        assertThat(melding.opprettetDato, is(equalTo(opprettet)));
        assertThat(melding.meldingstype, is(equalTo(SVAR)));
        assertThat(melding.fritekst, is(equalTo(fritekst)));
        assertThat(melding.tema, is(equalTo(tema)));
        assertThat(melding.lestDato, is(equalTo(lest)));
    }

    @Test
    public void testTilMeldingTransformer_medReferat() {
        DateTime opprettet = DateTime.now().minusDays(1);
        DateTime lest = DateTime.now();
        String behandlingsId = "1";
        String fritekst = "fritekst";
        String tema = "tema";
        String kanal = "TELEFON";
        XMLReferat xmlReferat = new XMLReferat().withFritekst(fritekst).withTemagruppe(tema).withLestDato(lest).withKanal(kanal);

        Melding melding = TIL_MELDING.transform(lagXMLBehandlingsInformasjon(behandlingsId, opprettet, REFERAT.name(), xmlReferat));

        assertThat(melding.id, is(equalTo(behandlingsId)));
        assertThat(melding.traadId, is(equalTo(behandlingsId)));
        assertThat(melding.opprettetDato, is(equalTo(opprettet)));
        assertThat(melding.meldingstype, is(equalTo(SAMTALEREFERAT)));
        assertThat(melding.fritekst, is(equalTo(fritekst)));
        assertThat(melding.tema, is(equalTo(tema)));
        assertThat(melding.lestDato, is(equalTo(lest)));
        assertThat(melding.kanal, is(equalTo(kanal)));
    }

    @Test(expected = ClassCastException.class)
    public void tilMeldingTransformer_girClassCastExceptionVedFeilType() {
        TIL_MELDING.transform(new XMLSporsmal());
    }

    private static XMLBehandlingsinformasjon lagXMLBehandlingsInformasjon(String behandlingsId, DateTime opprettetDato, String henvendelseType, XMLMetadata xmlMetadata) {
        return new XMLBehandlingsinformasjon()
                .withBehandlingsId(behandlingsId)
                .withOpprettetDato(opprettetDato)
                .withHenvendelseType(henvendelseType)
                .withMetadataListe(new XMLMetadataListe().withMetadata(xmlMetadata));
    }
}
