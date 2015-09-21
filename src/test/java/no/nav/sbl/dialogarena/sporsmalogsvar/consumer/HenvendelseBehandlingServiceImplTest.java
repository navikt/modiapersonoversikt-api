package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.content.PropertyResolver;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.subjectAttribute;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.OKSOS;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.*;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HenvendelseBehandlingServiceImplTest {

    private static final String FNR = "11111111";
    private static final String TEMAGRUPPE = "temagruppe";
    private static final String BEHANDLINGS_ID = "id1";
    private static final TraadVM VALGT_TRAAD = new TraadVM(createMeldingVMer());
    private static final List<String> IDER_I_VALGT_TRAAD = asList(ID_1, ID_2, ID_3);
    private static final String NAVBRUKERS_ENHET = "Navbrukers enhet";
    public static final String ARKIVTEMANAVN = "arkivtemanavn";
    public static final String NAV_SELBU = "1664";
    public static final String NAV_LEKSVIK = "1718";
    public static final String NAV_VAERNES = "1783";

    @Captor
    private ArgumentCaptor<WSHentHenvendelseListeRequest> wsHentHenvendelseListeRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<PolicyRequest> pepArgument;

    @Mock
    private HenvendelsePortType henvendelsePortType;
    @Mock
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Mock
    private EnforcementPoint pep;
    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private PersonKjerneinfoServiceBi kjerneinfo;
    @Mock
    private StandardKodeverk standardKodeverk;
    @Mock
    private PropertyResolver propertyResolver;
    @Mock
    private SporingsLogger sporingsLogger;
    @Mock
    private LDAPService ldapService;

    @InjectMocks
    private HenvendelseBehandlingServiceImpl henvendelseBehandlingService;

    @Before
    public void setUp() {
        when(propertyResolver.getProperty(anyString())).thenAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return ((String) invocation.getArguments()[0]);
            }
        });
        Whitebox.setInternalState(henvendelseBehandlingService, "propertyResolver", propertyResolver);
        XMLMeldingFraBruker xmlMeldingFraBruker = new XMLMeldingFraBruker()
                .withFritekst("fritekst")
                .withTemagruppe(TEMAGRUPPE);

        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true);

        List<Object> xmlHenvendelseListe = new ArrayList<>();
        xmlHenvendelseListe.add(lagXMLHenvendelse(BEHANDLINGS_ID, BEHANDLINGS_ID, DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(xmlMeldingFraBruker)));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(
                new WSHentHenvendelseListeResponse().withAny(xmlHenvendelseListe));

        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("1231");
        when(standardKodeverk.getArkivtemaNavn(anyString())).thenReturn(ARKIVTEMANAVN);
    }

    @Test
    public void skalHenteMeldingerMedRiktigType() {
        henvendelseBehandlingService.hentMeldinger(FNR);

        verify(henvendelsePortType).hentHenvendelseListe(wsHentHenvendelseListeRequestArgumentCaptor.capture());
        WSHentHenvendelseListeRequest request = wsHentHenvendelseListeRequestArgumentCaptor.getValue();

        assertThat(request.getFodselsnummer()).isEqualTo(FNR);
        assertThat(request.getTyper()).contains(SPORSMAL_SKRIFTLIG.name());
        assertThat(request.getTyper()).contains(SVAR_OPPMOTE.name());
        assertThat(request.getTyper()).contains(SVAR_SKRIFTLIG.name());
        assertThat(request.getTyper()).contains(SVAR_TELEFON.name());
        assertThat(request.getTyper()).contains(REFERAT_OPPMOTE.name());
        assertThat(request.getTyper()).contains(REFERAT_TELEFON.name());
    }

    @Test
    public void skalTransformereResponsenTilMeldingsliste() {
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger).hasSize(1);
        assertThat(meldinger.get(0).id).isEqualTo(BEHANDLINGS_ID);
    }

    @Test
    public void skalMerkeSomKontorsperret() {
        HentKjerneinformasjonResponse hentKjerneinformasjonResponse = new HentKjerneinformasjonResponse();
        hentKjerneinformasjonResponse.setPerson(
                new Person.With().personfakta(
                        new Personfakta.With().harAnsvarligEnhet(
                                new AnsvarligEnhet.With().organisasjonsenhet(
                                        new Organisasjonsenhet.With().organisasjonselementId(NAVBRUKERS_ENHET)
                                                .done()
                                ).done()
                        ).done()
                ).done()
        );

        when(kjerneinfo.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(hentKjerneinformasjonResponse);
        henvendelseBehandlingService.merkSomKontorsperret("navbrukers fnr", VALGT_TRAAD);

        verify(behandleHenvendelsePortType).oppdaterKontorsperre(NAVBRUKERS_ENHET, IDER_I_VALGT_TRAAD);
    }

    @Test
    public void skalMerkeSomFeilsendt() {
        henvendelseBehandlingService.merkSomFeilsendt(VALGT_TRAAD);

        verify(behandleHenvendelsePortType).oppdaterTilKassering(IDER_I_VALGT_TRAAD);
    }

    @Test
    public void skalAlltidHenteMeldingerSomIkkeErKontorsperret() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", null, DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(null)
                .withKontorsperreEnhet(null));
        final XMLMeldingFraBruker fritekst1 = new XMLMeldingFraBruker("fritekst", TEMAGRUPPE);
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id2", "id2", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(fritekst1))
                .withJournalfortInformasjon(null)
                .withKontorsperreEnhet(null));
        final XMLMeldingFraBruker fritekst3 = new XMLMeldingFraBruker("fritekst", TEMAGRUPPE);
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id4", "id4", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(fritekst3))
                .withKontorsperreEnhet("1111"));

        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false);
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger).hasSize(2);
        assertThat(meldinger.get(0).id).isEqualTo("id1");
        assertThat(meldinger.get(1).id).isEqualTo("id2");
    }

    @Test
    public void skalFjerneFritekstFraJournalfortMeldingManIkkeHarTilgangTil() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", null, DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(null));
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id2", "id2", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(new XMLJournalfortInformasjon().withJournalfortTema("tema")));

        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false);
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger).hasSize(2);
        assertThat(meldinger.get(0).fritekst).isEqualTo("fritekst");
        assertThat(meldinger.get(1).fritekst).isEqualTo("tilgang.journalfort");
    }

    @Test
    public void oversetterFraJournalfortTemaTilTemanavn() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", null, DateTime.now().minusDays(1), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(new XMLJournalfortInformasjon()
                        .withJournalfortSaksId("saksid")
                        .withJournalforerNavIdent("journalfort av navident")
                        .withJournalfortDato(DateTime.now())
                        .withJournalfortTema("journalfort tema")));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));

        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger.get(0).journalfortTemanavn).isEqualTo(ARKIVTEMANAVN);
    }

    @Test
    public void oversetterIkkeFraJournalfortTemaTilTemanavnDersomJournalfortTemaErNull() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", null, DateTime.now().minusDays(1), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(null));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));

        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger.get(0).journalfortTemanavn).isNull();
    }

    @Test
    public void skalFjerneFritekstForOkonomiskSosialhjelpDersomManIkkeHarTilgang() {
        String fritekst = "fritekst";

        XMLHenvendelse okonomiskSosialhjelp = lagXMLHenvendelse("1234", "1234", DateTime.now(), DateTime.now(), SPORSMAL_SKRIFTLIG.toString(), null,
                new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker().withFritekst(fritekst).withTemagruppe(OKSOS.toString())))
                .withTilknyttetEnhet("9999")
                .withGjeldendeTemagruppe(OKSOS.toString());

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(okonomiskSosialhjelp));
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false);

        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger.get(0).fritekst).isNotEqualTo(fritekst);

    }

    @Test
    public void skalSendeFlereEnheterTilPepForKontorsperre() throws Exception {
        List<Melding> meldinger = Arrays.asList(new Melding().withKontorsperretEnhet(NAV_SELBU));

        on(meldinger).filter(henvendelseBehandlingService.kontorsperreTilgang(NAV_VAERNES)).collect();

        verify(pep).hasAccess(pepArgument.capture());
        assertThat(pepArgument.getValue().getAttributes()).contains(
                subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(NAV_VAERNES)),
                subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(NAV_SELBU))
        );
    }

    @Test
    public void skalSendeEnEnheterTilPepForKontorsperre() throws Exception {
        List<Melding> meldinger = Arrays.asList(new Melding().withKontorsperretEnhet(NAV_SELBU));

        on(meldinger).filter(henvendelseBehandlingService.kontorsperreTilgang(NAV_LEKSVIK)).collect();

        verify(pep).hasAccess(pepArgument.capture());
        assertThat(pepArgument.getValue().getAttributes()).contains(
                subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(NAV_LEKSVIK))
        );
    }

    @Test
    public void skalSendeFlereEnheterTilPepForOkonomiskSosialeMeldinger() throws Exception {
        List<Melding> meldinger = Arrays.asList(new Melding().withGjeldendeTemagruppe(OKSOS));

        on(meldinger).map(henvendelseBehandlingService.okonomiskSosialhjelpTilgang(NAV_VAERNES)).collect();

        verify(pep).hasAccess(pepArgument.capture());
        assertThat(pepArgument.getValue().getAttributes()).contains(
                subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(NAV_VAERNES)),
                subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(NAV_SELBU))
        );
    }

    @Test
    public void skalSendeEnEnheterTilPepForOkonomiskSosialeMeldinger() throws Exception {
        List<Melding> meldinger = Arrays.asList(new Melding().withGjeldendeTemagruppe(OKSOS));

        on(meldinger).map(henvendelseBehandlingService.okonomiskSosialhjelpTilgang(NAV_LEKSVIK)).collect();

        verify(pep).hasAccess(pepArgument.capture());
        assertThat(pepArgument.getValue().getAttributes()).contains(
                subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(NAV_LEKSVIK))
        );
    }






}