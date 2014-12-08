package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_OPPMOTE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.STATUS;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SAMTALEREFERAT_OPPMOTE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.JOURNALFORT_DATO;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.JOURNALFORT_SAKSID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.JOURNALFORT_TEMA;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.lagXMLHenvendelse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class MeldingUtilsTest {

    public static final String ID_1 = "1";
    public static final String ID_2 = "2";
    public static final String ID_3 = "3";
    public static final String NAVIDENT = "navident";
    public static final String FRITEKST = "fritekst";
    public static final String TEMAGRUPPE = "temagruppe";
    public static final String KANAL = "kanal";
    public static final DateTime OPPRETTET_DATO = DateTime.now().minusDays(2);
    public static final DateTime LEST_DATO = DateTime.now();

    @Test
    public void testSkillUtTraader() {
        Melding melding1 = new Melding(ID_1, SPORSMAL_SKRIFTLIG, now());
        melding1.traadId = ID_1;
        Melding melding2 = new Melding(ID_2, SVAR_SKRIFTLIG, now());
        melding2.traadId = ID_1;
        Melding melding3 = new Melding(ID_3, SPORSMAL_SKRIFTLIG, now());
        melding3.traadId = ID_3;
        Map<String, List<Melding>> traader = skillUtTraader(asList(melding1, melding2, melding3));

        assertThat(traader.size(), is(equalTo(2)));
        assertThat(traader.get(ID_1).size(), is(equalTo(2)));
        assertThat(traader.get(ID_3).size(), is(equalTo(1)));
    }

    @Test
    public void filtrererUtTraaderSomIkkeHarEnRothenvendelse() {
        //Etter en viss periode (5 år i skrivende stund) skal henvendelser skjules helt. Derfor kan det hende at spørsmålet ikke kommer med når man spør Henvendelse.
        //Frittstående referater og spørsmål skal alltid ha behandlingskjedeId lik sin egen behandlingsId, så de skal ikke filtreres bort.

        Melding melding2 = new Melding(ID_2, SVAR_SKRIFTLIG, now());
        melding2.traadId = ID_1;
        Melding melding3 = new Melding(ID_3, SPORSMAL_SKRIFTLIG, now());
        melding3.traadId = ID_3;
        Map<String, List<Melding>> traader = skillUtTraader(asList(melding2, melding3));

        assertThat(traader.size(), is(equalTo(1)));
        assertThat(traader.get(ID_3).size(), is(equalTo(1)));
    }

    @Test
    public void testStatusTransformer() {
        XMLHenvendelse xmlHenvendelse = new XMLHenvendelse();
        xmlHenvendelse.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker()));

        xmlHenvendelse.withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name());
        xmlHenvendelse.withOpprettetDato(now());
        assertThat(STATUS.transform(xmlHenvendelse), is(equalTo(IKKE_BESVART)));

        xmlHenvendelse.withHenvendelseType(XMLHenvendelseType.SVAR_SKRIFTLIG.name());
        xmlHenvendelse.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingTilBruker()));
        assertThat(STATUS.transform(xmlHenvendelse), is(equalTo(IKKE_LEST_AV_BRUKER)));

        xmlHenvendelse.withLestDato(now()).withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingTilBruker()));
        assertThat(STATUS.transform(xmlHenvendelse), is(equalTo(LEST_AV_BRUKER)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStatusTransformerUkjentType() {
        STATUS.transform(new XMLHenvendelse().withHenvendelseType(""));
    }

    @Test
    public void testTilMeldingTransformer_medSporsmal() {
        XMLMeldingFraBruker xmlMeldingFraBruker = new XMLMeldingFraBruker()
                .withFritekst(FRITEKST)
                .withTemagruppe(TEMAGRUPPE);

        Melding melding = TIL_MELDING.transform(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), new XMLMetadataListe().withMetadata(xmlMeldingFraBruker)));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SPORSMAL_SKRIFTLIG)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.fritekst, is(equalTo(FRITEKST)));
        assertThat(melding.temagruppe, is(equalTo(TEMAGRUPPE)));
    }

    @Test
    public void testTilMeldingTransformer_medSporsmalMedKassertInnhold() {
        Melding melding = TIL_MELDING.transform(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SPORSMAL_SKRIFTLIG)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.fritekst, is(nullValue()));
        assertThat(melding.temagruppe, is(nullValue()));
    }

    @Test
    public void testTilMeldingTransformer_medSvar() {
        XMLMeldingTilBruker meldingTilBruker = createMeldingTilBruker();

        Melding melding = TIL_MELDING.transform(lagXMLHenvendelse(ID_1, ID_2, OPPRETTET_DATO, LEST_DATO, XMLHenvendelseType.SVAR_SKRIFTLIG.name(), new XMLMetadataListe().withMetadata(meldingTilBruker)));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_2)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SVAR_SKRIFTLIG)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.fritekst, is(equalTo(FRITEKST)));
        assertThat(melding.temagruppe, is(equalTo(TEMAGRUPPE)));
        assertThat(melding.kanal, is(equalTo(KANAL)));
        assertThat(melding.navIdent, is(NAVIDENT));
    }

    @Test
    public void testTilMeldingTransformer_medSvarMedKassertInnhold() {

        Melding melding = TIL_MELDING.transform(lagXMLHenvendelse(ID_1, ID_2, OPPRETTET_DATO, LEST_DATO, XMLHenvendelseType.SVAR_SKRIFTLIG.name(), null));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_2)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SVAR_SKRIFTLIG)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.fritekst, is(nullValue()));
        assertThat(melding.temagruppe, is(nullValue()));
        assertThat(melding.kanal, is(nullValue()));
        assertThat(melding.navIdent, is(nullValue()));
    }

    @Test
    public void testTilMeldingTransformer_medReferat() {
        XMLMeldingTilBruker xmlMeldingTilBruker = createMeldingTilBruker();

        Melding melding = TIL_MELDING.transform(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, LEST_DATO, REFERAT_OPPMOTE.name(), new XMLMetadataListe().withMetadata(xmlMeldingTilBruker)));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SAMTALEREFERAT_OPPMOTE)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.fritekst, is(equalTo(FRITEKST)));
        assertThat(melding.temagruppe, is(equalTo(TEMAGRUPPE)));
        assertThat(melding.kanal, is(equalTo(KANAL)));
        assertThat(melding.navIdent, is(NAVIDENT));
    }

    @Test
    public void testTilMeldingTransformer_medReferatMedKassertInnhold() {
        Melding melding = TIL_MELDING.transform(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, LEST_DATO, REFERAT_OPPMOTE.name(), null));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SAMTALEREFERAT_OPPMOTE)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.fritekst, is(nullValue()));
        assertThat(melding.temagruppe, is(nullValue()));
        assertThat(melding.kanal, is(nullValue()));
        assertThat(melding.navIdent, is(nullValue()));
    }

    private XMLMeldingTilBruker createMeldingTilBruker() {
        return new XMLMeldingTilBruker()
                .withFritekst(FRITEKST)
                .withTemagruppe(TEMAGRUPPE)
                .withKanal(KANAL)
                .withNavident(NAVIDENT);
    }

    @Test(expected = ClassCastException.class)
    public void tilMeldingTransformer_girClassCastExceptionVedFeilType() {
        TIL_MELDING.transform(new XMLMeldingFraBruker());
    }

}
