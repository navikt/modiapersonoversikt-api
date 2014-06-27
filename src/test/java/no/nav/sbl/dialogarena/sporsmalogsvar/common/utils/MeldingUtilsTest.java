package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.STATUS;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.LEST_AV_BRUKER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class MeldingUtilsTest {

    public static final String ID_1 = "1";
    public static final String ID_2 = "2";
    public static final String ID_3 = "3";
    public static final String NAVIDENT = "navident";
    public static final String FRITEKST = "fritekst";
    public static final String TEMA = "tema";
    public static final String KANAL = "kanal";
    public static final DateTime OPPRETTET_DATO = DateTime.now().minusDays(1);
    public static final DateTime LEST_DATO = DateTime.now();


    @Test
    public void testSkillUtTraader() {
        Melding melding1 = new Melding(ID_1, SPORSMAL, now());
        melding1.traadId = ID_1;
        Melding melding2 = new Melding(ID_2, SVAR, now());
        melding2.traadId = ID_1;
        Melding melding3 = new Melding(ID_3, SPORSMAL, now());
        melding3.traadId = ID_2;
        Map<String, List<Melding>> traader = skillUtTraader(asList(melding1, melding2, melding3));

        assertThat(traader.size(), is(equalTo(2)));
        assertThat(traader.get(ID_1).size(), is(equalTo(2)));
        assertThat(traader.get(ID_2).size(), is(equalTo(1)));
    }

    @Test
    public void testStatusTransformer() {
        XMLBehandlingsinformasjon behandlingsinformasjonV2 = new XMLBehandlingsinformasjon();
        behandlingsinformasjonV2.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLSporsmal()));

        behandlingsinformasjonV2.withHenvendelseType(XMLHenvendelseType.SPORSMAL.name());
        behandlingsinformasjonV2.withOpprettetDato(now());
        assertThat(STATUS.transform(behandlingsinformasjonV2), is(equalTo(IKKE_BESVART)));

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
        XMLSporsmal xmlSporsmal = new XMLSporsmal().withFritekst(FRITEKST).withTemagruppe(TEMA);

        Melding melding = TIL_MELDING.transform(lagXMLBehandlingsInformasjon(ID_1, OPPRETTET_DATO, XMLHenvendelseType.SPORSMAL.name(), xmlSporsmal));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SPORSMAL)));
        assertThat(melding.fritekst, is(equalTo(FRITEKST)));
        assertThat(melding.tema, is(equalTo(TEMA)));
    }

    @Test
    public void testTilMeldingTransformer_medSvar() {
        XMLSvar xmlSvar = new XMLSvar().withSporsmalsId(ID_2).withTemagruppe(TEMA).withLestDato(LEST_DATO).withFritekst(FRITEKST);

        Melding melding = TIL_MELDING.transform(lagXMLBehandlingsInformasjon(ID_1, OPPRETTET_DATO, XMLHenvendelseType.SVAR.name(), xmlSvar));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_2)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SVAR)));
        assertThat(melding.fritekst, is(equalTo(FRITEKST)));
        assertThat(melding.tema, is(equalTo(TEMA)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.navIdent, is(NAVIDENT));
    }

    @Test
    public void testTilMeldingTransformer_medReferat() {
        XMLReferat xmlReferat = new XMLReferat().withFritekst(FRITEKST).withTemagruppe(TEMA).withLestDato(LEST_DATO).withKanal(KANAL);

        Melding melding = TIL_MELDING.transform(lagXMLBehandlingsInformasjon(ID_1, OPPRETTET_DATO, REFERAT.name(), xmlReferat));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SAMTALEREFERAT)));
        assertThat(melding.fritekst, is(equalTo(FRITEKST)));
        assertThat(melding.tema, is(equalTo(TEMA)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.kanal, is(equalTo(KANAL)));
        assertThat(melding.navIdent, is(NAVIDENT));
    }

    @Test(expected = ClassCastException.class)
    public void tilMeldingTransformer_girClassCastExceptionVedFeilType() {
        TIL_MELDING.transform(new XMLSporsmal());
    }

    private static XMLBehandlingsinformasjon lagXMLBehandlingsInformasjon(String behandlingsId, DateTime opprettetDato, String henvendelseType, XMLMetadata xmlMetadata) {
        return new XMLBehandlingsinformasjon()
                .withAktor(new XMLAktor().withNavIdent(NAVIDENT))
                .withBehandlingsId(behandlingsId)
                .withOpprettetDato(opprettetDato)
                .withHenvendelseType(henvendelseType)
                .withMetadataListe(new XMLMetadataListe().withMetadata(xmlMetadata));
    }

}
