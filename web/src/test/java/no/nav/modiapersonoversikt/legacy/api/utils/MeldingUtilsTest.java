package no.nav.modiapersonoversikt.legacy.api.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.DOKUMENT_VARSEL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.OPPGAVE_VARSEL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe.ARBD;
import static no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype.*;
import static no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Status.*;
import static no.nav.modiapersonoversikt.legacy.api.utils.MeldingUtils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    private final String mockVerdiFraPropertyResolver = "value";

    private ContentRetriever propertyResolver = mock(ContentRetriever.class);
    private LDAPService ldapService = mock(LDAPService.class);

    @BeforeEach
    public void init() {
        when(propertyResolver.hentTekst(anyString(), anyString())).thenReturn(mockVerdiFraPropertyResolver);
        when(propertyResolver.hentTekst(anyString())).thenReturn(mockVerdiFraPropertyResolver);
        when(ldapService.hentSaksbehandler(NAVIDENT)).thenReturn(new Saksbehandler("Jan", "Saksbehandler", "ident"));
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
    public void testHenvendelseStatus() {
        XMLHenvendelse xmlHenvendelse = new XMLHenvendelse();
        xmlHenvendelse.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker()));

        xmlHenvendelse.withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name());
        xmlHenvendelse.withOpprettetDato(now());
        assertThat(henvendelseStatus(xmlHenvendelse), is(equalTo(IKKE_BESVART)));

        xmlHenvendelse.withHenvendelseType(XMLHenvendelseType.SVAR_SKRIFTLIG.name());
        xmlHenvendelse.withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingTilBruker()));
        assertThat(henvendelseStatus(xmlHenvendelse), is(equalTo(IKKE_LEST_AV_BRUKER)));

        xmlHenvendelse.withLestDato(now()).withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingTilBruker()));
        assertThat(henvendelseStatus(xmlHenvendelse), is(equalTo(LEST_AV_BRUKER)));
    }

    @Test
    public void testHenvendelseStatusUkjentType() {
        assertThrows(IllegalArgumentException.class, () -> henvendelseStatus(new XMLHenvendelse().withHenvendelseType("")));
    }

    @Test
    public void testTilMeldingTransformer_medSporsmal() {
        XMLMeldingFraBruker xmlMeldingFraBruker = new XMLMeldingFraBruker()
                .withFritekst(FRITEKST)
                .withTemagruppe(TEMAGRUPPE);

        Melding melding = tilMelding(propertyResolver, ldapService).apply(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(xmlMeldingFraBruker)));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SPORSMAL_SKRIFTLIG)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.getFritekst(), is(equalTo(FRITEKST)));
        assertThat(melding.temagruppe, is(equalTo(TEMAGRUPPE)));
        assertThat(melding.erDokumentMelding, is(false));
    }

    @Test
    public void testTilMeldingTransformer_medSporsmalMedKassertInnhold() {
        Melding melding = tilMelding(propertyResolver, ldapService).apply(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, null));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SPORSMAL_SKRIFTLIG)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.getFritekst(), is(mockVerdiFraPropertyResolver));
        assertThat(melding.temagruppe, is(nullValue()));
        assertThat(melding.erDokumentMelding, is(false));
    }

    @Test
    public void testTilMeldingTransformer_medSvar() {
        XMLMeldingTilBruker meldingTilBruker = createMeldingTilBruker();

        Melding melding = tilMelding(propertyResolver, ldapService).apply(lagXMLHenvendelse(ID_1, ID_2, OPPRETTET_DATO, LEST_DATO, XMLHenvendelseType.SVAR_SKRIFTLIG.name(), NAVIDENT, new XMLMetadataListe().withMetadata(meldingTilBruker)));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_2)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SVAR_SKRIFTLIG)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.getFritekst(), is(equalTo(FRITEKST)));
        assertThat(melding.temagruppe, is(equalTo(TEMAGRUPPE)));
        assertThat(melding.kanal, is(equalTo(KANAL)));
        assertThat(melding.navIdent, is(NAVIDENT));
        assertThat(melding.erDokumentMelding, is(false));
    }

    @Test
    public void testTilMeldingTransformer_medSvarMedKassertInnhold() {

        Melding melding = tilMelding(propertyResolver, ldapService).apply(lagXMLHenvendelse(ID_1, ID_2, OPPRETTET_DATO, LEST_DATO, XMLHenvendelseType.SVAR_SKRIFTLIG.name(), NAVIDENT, null));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_2)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SVAR_SKRIFTLIG)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.getFritekst(), is(mockVerdiFraPropertyResolver));
        assertThat(melding.temagruppe, is(nullValue()));
        assertThat(melding.kanal, is(nullValue()));
        assertThat(melding.navIdent, is(nullValue()));
        assertThat(melding.erDokumentMelding, is(false));
    }

    @Test
    public void testTilMeldingTransformer_medReferat() {
        XMLMeldingTilBruker xmlMeldingTilBruker = createMeldingTilBruker();

        Melding melding = tilMelding(propertyResolver, ldapService).apply(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, LEST_DATO, REFERAT_OPPMOTE.name(), NAVIDENT, new XMLMetadataListe().withMetadata(xmlMeldingTilBruker)));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SAMTALEREFERAT_OPPMOTE)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.getFritekst(), is(equalTo(FRITEKST)));
        assertThat(melding.temagruppe, is(equalTo(TEMAGRUPPE)));
        assertThat(melding.kanal, is(equalTo(KANAL)));
        assertThat(melding.navIdent, is(NAVIDENT));
        assertThat(melding.erDokumentMelding, is(false));
    }

    @Test
    public void testTilMeldingTransformer_medReferatMedKassertInnhold() {
        Melding melding = tilMelding(propertyResolver, ldapService).apply(lagXMLHenvendelse(ID_1, ID_1, OPPRETTET_DATO, LEST_DATO, REFERAT_OPPMOTE.name(), NAVIDENT, null));

        assertThat(melding.id, is(equalTo(ID_1)));
        assertThat(melding.traadId, is(equalTo(ID_1)));
        assertThat(melding.opprettetDato, is(equalTo(OPPRETTET_DATO)));
        assertThat(melding.meldingstype, is(equalTo(SAMTALEREFERAT_OPPMOTE)));
        assertThat(melding.lestDato, is(equalTo(LEST_DATO)));
        assertThat(melding.journalfortDato, is(JOURNALFORT_DATO));
        assertThat(melding.journalfortSaksId, is(JOURNALFORT_SAKSID));
        assertThat(melding.journalfortTema, is(JOURNALFORT_TEMA));
        assertThat(melding.eksternAktor, is(NAVIDENT));
        assertThat(melding.getFritekst(), is(mockVerdiFraPropertyResolver));
        assertThat(melding.temagruppe, is(nullValue()));
        assertThat(melding.kanal, is(nullValue()));
        assertThat(melding.navIdent, is(nullValue()));
        assertThat(melding.erDokumentMelding, is(false));
    }

    @Test
    public void skalLageHenvendelseBasertPaaXMLHenvendelseMedXMLMeldingFraBruker() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingFraBruker();
        XMLMeldingFraBruker xmlMeldingFraBruker = (XMLMeldingFraBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        Melding sporsmal = tilMelding(propertyResolver, ldapService).apply(xmlHenvendelse);

        assertThat(sporsmal.id, is(xmlHenvendelse.getBehandlingsId()));
        assertThat(sporsmal.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(sporsmal.oppgaveId, is(xmlHenvendelse.getOppgaveIdGsak()));
        assertThat(sporsmal.temagruppe, is(xmlMeldingFraBruker.getTemagruppe()));
        assertThat(sporsmal.getFritekst(), is(xmlMeldingFraBruker.getFritekst()));
        assertThat(sporsmal.erDokumentMelding, is(false));
    }

    @Test
    public void lagerHenvendelseFraXMLHenvendelseMedXMLMeldingTilBruker() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE);
        XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        Melding referat = MeldingUtils.tilMelding(propertyResolver, ldapService).apply(xmlHenvendelse);

        verify(ldapService, atLeastOnce()).hentSaksbehandler(xmlMeldingTilBruker.getNavident());

        assertThat(referat.fnrBruker, is(xmlHenvendelse.getFnr()));
        assertThat(referat.meldingstype, is(SAMTALEREFERAT_OPPMOTE));
        assertThat(referat.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(referat.traadId, is(xmlHenvendelse.getBehandlingskjedeId()));
        assertThat(referat.temagruppe, is(xmlMeldingTilBruker.getTemagruppe()));
        assertThat(referat.kanal, is(xmlMeldingTilBruker.getKanal()));
        assertThat(referat.getFritekst(), is(xmlMeldingTilBruker.getFritekst()));
        assertThat(referat.navIdent, is(xmlMeldingTilBruker.getNavident()));
        assertThat(referat.erDokumentMelding, is(false));
    }

    @Test
    public void taklerAtInnholdetErKassert() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedXmlMeldingFraBruker();
        xmlHenvendelse.setMetadataListe(null);

        Melding sporsmal = tilMelding(propertyResolver, ldapService).apply(xmlHenvendelse);

        assertThat(sporsmal.id, is(xmlHenvendelse.getBehandlingsId()));
        assertThat(sporsmal.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(sporsmal.oppgaveId, is(xmlHenvendelse.getOppgaveIdGsak()));
        assertThat(sporsmal.temagruppe, is(nullValue()));
        assertThat(sporsmal.getFritekst(), is(mockVerdiFraPropertyResolver));
        assertThat(sporsmal.erDokumentMelding, is(false));
    }

    @Test
    public void kasterExceptionVedUkjentType() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedUkjentType();
        assertThrows(RuntimeException.class, () -> tilMelding(propertyResolver, ldapService).apply(xmlHenvendelse));
    }

    @Test
    public void returnererMeldingSomErDokumentMeldingOmDokumentVarsel() {
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedDokumentVarsel();
        XMLDokumentVarsel innsendtVarsel = (XMLDokumentVarsel) xmlHenvendelse.getMetadataListe().getMetadata().get(0);

        Melding dokumentVarsel = tilMelding(propertyResolver, ldapService).apply(xmlHenvendelse);

        assertThat(dokumentVarsel.id, is(xmlHenvendelse.getBehandlingsId()));
        assertThat(dokumentVarsel.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(dokumentVarsel.statusTekst, is(innsendtVarsel.getDokumenttittel()));
        assertThat(dokumentVarsel.erDokumentMelding, is(true));
        assertThat(dokumentVarsel.temagruppe, is(nullValue()));

    }

    @Test
    public void getTekstForMeldingStatusForDynamiskNokkelSkalReturnereKeyBruktSomParameterDersomKeyIkkeEksiserer() {
        final String key = "melding.status.SVAR_SKRIFTLIG";
        final ContentRetriever propertyResolver = mock(ContentRetriever.class);
        when(propertyResolver.hentTekst(anyString())).thenThrow(new NoSuchElementException());

        final String returVerdi = getTekstForMeldingStatus(propertyResolver, Meldingstype.SVAR_SKRIFTLIG);

        assertThat(returVerdi, is(key));
    }

    @Test
    public void hentEnonicTekstDynamicSkalBrukeDefaultKeyDersomUthentingMedParameterKeyFeiler() {
        final String keySomFeiler = "key-som-feiler";
        final String defaultKey = "default-key";
        final String valueForDefaultKey = "value-for-default-key";
        final ContentRetriever propertyResolver = mock(ContentRetriever.class);
        when(propertyResolver.hentTekst(keySomFeiler)).thenThrow(new NoSuchElementException());
        when(propertyResolver.hentTekst(defaultKey)).thenReturn(valueForDefaultKey);

        final String returVerdi = hentEnonicTekstDynamic(propertyResolver, keySomFeiler, defaultKey);

        assertThat(returVerdi, is(valueForDefaultKey));
    }

    @Test
    public void lagerMeldingSomErOppgaveMeldingOmOppgaveVarsel() {
        XMLHenvendelse xmlHenvendelse = lagXMLHenvendelseMedOppgaveVarsel();

        Melding oppgaveVarsel = tilMelding(propertyResolver, ldapService).apply(xmlHenvendelse);

        assertThat(oppgaveVarsel.id, is(xmlHenvendelse.getBehandlingsId()));
        assertThat(oppgaveVarsel.opprettetDato, is(xmlHenvendelse.getOpprettetDato()));
        assertThat(oppgaveVarsel.statusTekst, is(mockVerdiFraPropertyResolver));
        assertThat(oppgaveVarsel.getFritekst(), is(mockVerdiFraPropertyResolver));
        assertThat(oppgaveVarsel.traadId, is(xmlHenvendelse.getBehandlingsId()));
        assertThat(oppgaveVarsel.erOppgaveMelding, is(true));
        assertThat(oppgaveVarsel.erDokumentMelding, is(false));
        assertThat(oppgaveVarsel.temagruppe, is(nullValue()));
        assertThat(oppgaveVarsel.statusKlasse, is("oppgave"));
    }

    private XMLHenvendelse lagXMLHenvendelseMedOppgaveVarsel() {
        return new XMLHenvendelse()
                .withBehandlingsId("123999123")
                .withBehandlingskjedeId("999222333")
                .withOpprettetDato(DateTime.now())
                .withTema("dagpenger")
                .withLestDato(null)
                .withKorrelasjonsId("a1-b2")
                .withHenvendelseType(OPPGAVE_VARSEL.value())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLOppgaveVarsel()
                                .withFritekst("fritekst")
                                .withOppgaveType("min_oppgavetype")
                                .withOppgaveURL("")
                                .withStoppRepeterendeVarsel(false)
                                .withTemagruppe("DAG")))
                .withFerdigstiltUtenSvar(false);
    }

    private XMLHenvendelse createXMLHenvendelseMedXmlMeldingTilBruker(XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withFnr("fnr")
                .withHenvendelseType(type.name())
                .withOpprettetDato(DateTime.now())
                .withBehandlingskjedeId("behandlingskjedeId")
                .withGjeldendeTemagruppe(ARBD.toString())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withTemagruppe("temagruppe")
                                .withKanal("kanal")
                                .withFritekst("fritekst")
                                .withNavident("navident")
                ))
                .withFerdigstiltUtenSvar(false);
    }

    private XMLHenvendelse createXMLHenvendelseMedXmlMeldingFraBruker() {
        return new XMLHenvendelse()
                .withBehandlingsId("behandlingsid")
                .withHenvendelseType(SPORSMAL_SKRIFTLIG.name())
                .withOpprettetDato(DateTime.now())
                .withOppgaveIdGsak("oppgaveidgsak")
                .withGjeldendeTemagruppe(ARBD.toString())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingFraBruker()
                                .withTemagruppe("temagruppe")
                                .withFritekst("fritekst")
                ))
                .withFerdigstiltUtenSvar(false);
    }

    private XMLHenvendelse createXMLHenvendelseMedDokumentVarsel() {
        return new XMLHenvendelse()
                .withBehandlingsId("123")
                .withBehandlingskjedeId("123")
                .withOpprettetDato(DateTime.now())
                .withTema("dagpenger")
                .withLestDato(null)
                .withKorrelasjonsId("a1-b2")
                .withHenvendelseType(DOKUMENT_VARSEL.value())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLDokumentVarsel()
                                .withDokumenttittel("tittel")
                                .withJournalpostId("1")
                                .withDokumentIdListe("2")
                                .withTemanavn("Dagpenger")))
                .withFerdigstiltUtenSvar(false);
    }

    private XMLHenvendelse createXMLHenvendelseMedUkjentType() {
        return new XMLHenvendelse()
                .withBehandlingsId("behandlingsid")
                .withHenvendelseType(SPORSMAL_SKRIFTLIG.name())
                .withOpprettetDato(DateTime.now())
                .withGjeldendeTemagruppe(ARBD.toString())
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

    private static XMLHenvendelse lagXMLHenvendelse(String behandlingsId, String behandlingskjedeId, DateTime opprettetDato, DateTime lestDato, String henvendelseType, String eksternAktor, XMLMetadataListe XMLMetadataListe) {
        return new XMLHenvendelse()
                .withBehandlingsId(behandlingsId)
                .withBehandlingskjedeId(behandlingskjedeId)
                .withOpprettetDato(opprettetDato)
                .withLestDato(lestDato)
                .withHenvendelseType(henvendelseType)
                .withGjeldendeTemagruppe(ARBD.toString())
                .withEksternAktor(eksternAktor)
                .withFerdigstiltUtenSvar(false)
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
