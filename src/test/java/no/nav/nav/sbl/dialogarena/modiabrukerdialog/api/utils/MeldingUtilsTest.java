package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.content.PropertyResolver;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.hamcrest.MatcherAssert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Status.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public static final String JOURNALFORT_ID = "journalfortId";
    public static final DateTime JOURNALFORT_DATO = DateTime.now().minusDays(1);
    public static final String JOURNALFORT_TEMA = "journalfortTema";
    public static final String JOURNALFORT_SAKSID = "journalfortSaksId1";

    private PropertyResolver propertyResolver = mock(PropertyResolver.class);

    @Before
    public void init() {
        when(propertyResolver.getProperty(anyString(), anyString())).thenReturn("value");
    }

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

        Melding melding = tilMelding(propertyResolver).transform(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(xmlMeldingFraBruker)));

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
        Melding melding = tilMelding(propertyResolver).transform(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, null));

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

        Melding melding = tilMelding(propertyResolver).transform(lagXMLHenvendelse(ID_1, ID_2, OPPRETTET_DATO, LEST_DATO, XMLHenvendelseType.SVAR_SKRIFTLIG.name(), NAVIDENT, new XMLMetadataListe().withMetadata(meldingTilBruker)));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_2)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SVAR_SKRIFTLIG)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.fritekst, is(equalTo(FRITEKST)));
        assertThat(melding.temagruppe, is(equalTo(TEMAGRUPPE)));
        assertThat(melding.kanal, is(equalTo(KANAL)));
        assertThat(melding.navIdent, is(NAVIDENT));
    }

    @Test
    public void testTilMeldingTransformer_medSvarMedKassertInnhold() {

        Melding melding = tilMelding(propertyResolver).transform(lagXMLHenvendelse(ID_1, ID_2, OPPRETTET_DATO, LEST_DATO, XMLHenvendelseType.SVAR_SKRIFTLIG.name(), NAVIDENT, null));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_2)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SVAR_SKRIFTLIG)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.fritekst, is(nullValue()));
        assertThat(melding.temagruppe, is(nullValue()));
        assertThat(melding.kanal, is(nullValue()));
        assertThat(melding.navIdent, is(nullValue()));
    }

    @Test
    public void testTilMeldingTransformer_medReferat() {
        XMLMeldingTilBruker xmlMeldingTilBruker = createMeldingTilBruker();

        Melding melding = tilMelding(propertyResolver).transform(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, LEST_DATO, REFERAT_OPPMOTE.name(), NAVIDENT, new XMLMetadataListe().withMetadata(xmlMeldingTilBruker)));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SAMTALEREFERAT_OPPMOTE)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.fritekst, is(equalTo(FRITEKST)));
        assertThat(melding.temagruppe, is(equalTo(TEMAGRUPPE)));
        assertThat(melding.kanal, is(equalTo(KANAL)));
        assertThat(melding.navIdent, is(NAVIDENT));
    }

    @Test
    public void testTilMeldingTransformer_medReferatMedKassertInnhold() {
        Melding melding = tilMelding(propertyResolver).transform(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, LEST_DATO, REFERAT_OPPMOTE.name(), NAVIDENT, null));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SAMTALEREFERAT_OPPMOTE)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.fritekst, is(nullValue()));
        assertThat(melding.temagruppe, is(nullValue()));
        assertThat(melding.kanal, is(nullValue()));
        assertThat(melding.navIdent, is(nullValue()));
    }

    @Test
    public void skalLageHenvendelseBasertPaaXMLHenvendelseMedXMLMeldingFraBruker() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingFraBruker();
        XMLMeldingFraBruker xmlMeldingFraBruker = (XMLMeldingFraBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        Melding sporsmal = tilMelding(propertyResolver).transform(xmlHenvendelse);

        MatcherAssert.assertThat(sporsmal.id, is(xmlHenvendelse.getBehandlingsId()));
        MatcherAssert.assertThat(sporsmal.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        MatcherAssert.assertThat(sporsmal.oppgaveId, is(xmlHenvendelse.getOppgaveIdGsak()));
        MatcherAssert.assertThat(sporsmal.temagruppe, is(xmlMeldingFraBruker.getTemagruppe()));
        MatcherAssert.assertThat(sporsmal.fritekst, is(xmlMeldingFraBruker.getFritekst()));
    }

    @Test
    public void lagerHenvendelseFraXMLHenvendelseMedXMLMeldingTilBruker() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE);
        XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        Melding referat = MeldingUtils.tilMelding(propertyResolver).transform(xmlHenvendelse);

        MatcherAssert.assertThat(referat.fnrBruker, is(xmlHenvendelse.getFnr()));
        MatcherAssert.assertThat(referat.meldingstype, is(SAMTALEREFERAT_OPPMOTE));
        MatcherAssert.assertThat(referat.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        MatcherAssert.assertThat(referat.traadId, is(xmlHenvendelse.getBehandlingskjedeId()));
        MatcherAssert.assertThat(referat.temagruppe, is(xmlMeldingTilBruker.getTemagruppe()));
        MatcherAssert.assertThat(referat.kanal, is(xmlMeldingTilBruker.getKanal()));
        MatcherAssert.assertThat(referat.fritekst, is(xmlMeldingTilBruker.getFritekst()));
        MatcherAssert.assertThat(referat.navIdent, is(xmlMeldingTilBruker.getNavident()));
    }

    @Test
    public void taklerAtInnholdetErKassert() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingFraBruker();
        xmlHenvendelse.setMetadataListe(null);

        Melding sporsmal = tilMelding(propertyResolver).transform(xmlHenvendelse);

        MatcherAssert.assertThat(sporsmal.id, is(xmlHenvendelse.getBehandlingsId()));
        MatcherAssert.assertThat(sporsmal.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        MatcherAssert.assertThat(sporsmal.oppgaveId, is(xmlHenvendelse.getOppgaveIdGsak()));
        MatcherAssert.assertThat(sporsmal.temagruppe, is(nullValue()));
        MatcherAssert.assertThat(sporsmal.fritekst, is(nullValue()));
    }

    @Test(expected = RuntimeException.class)
    public void kasterExceptionVedUkjentType() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedUkjentType();
        tilMelding(propertyResolver).transform(xmlHenvendelse);
    }

    private XMLHenvendelse createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withFnr("fnr")
                .withHenvendelseType(type.name())
                .withOpprettetDato(DateTime.now())
                .withBehandlingskjedeId("behandlingskjedeId")
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withTemagruppe("temagruppe")
                                .withKanal("kanal")
                                .withFritekst("fritekst")
                                .withNavident("navident")
                ));
    }

    private XMLHenvendelse createXMLHenvendelseMedXmlMeldingFraBruker() {
        return new XMLHenvendelse()
                .withBehandlingsId("behandlingsid")
                .withHenvendelseType(SPORSMAL_SKRIFTLIG.name())
                .withOpprettetDato(DateTime.now())
                .withOppgaveIdGsak("oppgaveidgsak")
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingFraBruker()
                                .withTemagruppe("temagruppe")
                                .withFritekst("fritekst")
                ));
    }

    private XMLHenvendelse createXMLHenvendelseMedUkjentType() {
        return new XMLHenvendelse()
                .withBehandlingsId("behandlingsid")
                .withHenvendelseType(SPORSMAL_SKRIFTLIG.name())
                .withOpprettetDato(DateTime.now())
                .withOppgaveIdGsak("oppgaveidgsak")
                .withMetadataListe(new XMLMetadataListe().withMetadata(new XMLUkjentType()));
    }

    private static class XMLUkjentType extends XMLMetadata {
    }

    private XMLMeldingTilBruker createMeldingTilBruker() {
        return new XMLMeldingTilBruker()
                .withFritekst(FRITEKST)
                .withTemagruppe(TEMAGRUPPE)
                .withKanal(KANAL)
                .withNavident(NAVIDENT);
    }

    public static XMLHenvendelse lagXMLHenvendelse(String behandlingsId, String behandlingskjedeId, DateTime opprettetDato, DateTime lestDato, String henvendelseType, String eksternAktor, XMLMetadataListe XMLMetadataListe) {
        return new XMLHenvendelse()
                .withBehandlingsId(behandlingsId)
                .withBehandlingskjedeId(behandlingskjedeId)
                .withOpprettetDato(opprettetDato)
                .withLestDato(lestDato)
                .withHenvendelseType(henvendelseType)
                .withEksternAktor(eksternAktor)
                .withJournalfortInformasjon(
                        new XMLJournalfortInformasjon()
                                .withJournalfortDato(JOURNALFORT_DATO)
                                .withJournalfortTema(JOURNALFORT_TEMA)
                                .withJournalpostId(JOURNALFORT_ID)
                                .withJournalfortSaksId(JOURNALFORT_SAKSID)
                )
                .withMetadataListe(XMLMetadataListe);
    }
}
