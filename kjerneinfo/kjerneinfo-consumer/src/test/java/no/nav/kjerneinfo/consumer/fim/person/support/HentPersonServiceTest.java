package no.nav.kjerneinfo.consumer.fim.person.support;

import no.nav.kjerneinfo.consumer.fim.person.exception.AuthorizationWithSikkerhetstiltakException;
import no.nav.kjerneinfo.consumer.fim.person.mock.PersonKjerneinfoMockFactory;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.consumer.fim.person.to.RecoverableAuthorizationException;
import no.nav.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollUtenTPS;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.person.v3.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.feil.WSSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentPersonResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;


public class HentPersonServiceTest {

    private static final String IDENT = "11223344556";
    private static final String FNR_BRUKER = "10108000398";
    private static final String FNR_SAMBOER = "12345678910";
    private static final String STRENGT_FORTROLIG_ADRESSE = "SPSF";

    private static PersonKjerneinfoMockFactory mockFactory;
    private static KjerneinfoMapper mapper;

    private final HentKjerneinformasjonRequest request = new HentKjerneinformasjonRequest(IDENT);
    private final WSHentPersonResponse response = new WSHentPersonResponse()
            .withPerson(mockFactory.getBruker(IDENT, true));

    private HentPersonService service;

    @Mock
    private PersonV3 portType;

    @Mock
    private OrganisasjonEnhetV2Service organisasjonEnhetV2Service;
    private TilgangskontrollUtenTPS tilgangskontroll = TilgangskontrollMock.getUtenTPS();

    @BeforeClass
    public static void setUpOnce() {
        DefaultKodeverkmanager kodeverk = new DefaultKodeverkmanager(mock(KodeverkPortType.class));
        mapper = new KjerneinfoMapper(kodeverk);
        mockFactory = new PersonKjerneinfoMockFactory();
    }

    @Before
    public void setUp() throws HentPersonPersonIkkeFunnet, HentPersonSikkerhetsbegrensning {
        MockitoAnnotations.initMocks(this);
        mockFactory = new PersonKjerneinfoMockFactory();
        service = new HentPersonService(portType, mapper, organisasjonEnhetV2Service, tilgangskontroll);
        when(organisasjonEnhetV2Service.finnNAVKontor(anyString(), anyString())).thenReturn(of(new AnsattEnhet("1234", "NAV Mockenhet")));
        when(portType.hentPerson(any(WSHentPersonRequest.class))).thenReturn(response);
    }


    @Test(expected = ApplicationException.class)
    public void personFinnesIkke() throws Exception {
        when(portType.hentPerson(any(WSHentPersonRequest.class))).thenThrow(new HentPersonPersonIkkeFunnet(IDENT));
        service.hentPerson(request);
    }

    @Test(expected = AuthorizationException.class)
    public void sikkerhetsbegrensning() throws Exception {
        when(portType.hentPerson(any(WSHentPersonRequest.class))).thenThrow(new HentPersonSikkerhetsbegrensning(IDENT));
        service.hentPerson(request);
    }

    @Test
    public void harTilgang() throws Exception {
        HentKjerneinformasjonResponse response = service.hentPerson(request);

        assertNotNull(response.getPerson());
    }

    @Test
    public void berOmAllInformasjon() throws Exception {
        service.hentPerson(request);

        ArgumentCaptor<WSHentPersonRequest> argumentCaptor = ArgumentCaptor.forClass(WSHentPersonRequest.class);
        verify(portType).hentPerson(argumentCaptor.capture());
        List<WSInformasjonsbehov> informasjonsBehov = argumentCaptor.getValue().getInformasjonsbehov();
        assertThat(informasjonsBehov.contains(WSInformasjonsbehov.ADRESSE), is(true));
        assertThat(informasjonsBehov.contains(WSInformasjonsbehov.BANKKONTO), is(true));
        assertThat(informasjonsBehov.contains(WSInformasjonsbehov.FAMILIERELASJONER), is(true));
        assertThat(informasjonsBehov.contains(WSInformasjonsbehov.KOMMUNIKASJON), is(true));
        assertThat(informasjonsBehov.contains(WSInformasjonsbehov.SPORINGSINFORMASJON), is(true));
    }

    @Test(expected = AuthorizationException.class)
    public void harIkkeTilgangKode6() throws Exception {
        WSSikkerhetsbegrensning begrensning = new WSSikkerhetsbegrensning().withFeilaarsak(FeilAarsaker.FP1_SFA.name());
        HentPersonSikkerhetsbegrensning exception = new HentPersonSikkerhetsbegrensning("Ingen tilgang", begrensning);
        when(portType.hentPerson(any(WSHentPersonRequest.class))).thenThrow(exception);

        try {
            service.hentPerson(request);
        } catch (AuthorizationException ae) {
            assertThat(ae.getMessage(), equalTo("sikkerhetsbegrensning.diskresjonskode6"));
            throw ae;
        }
    }

    @Test(expected = AuthorizationException.class)
    public void harIkkeTilgangKode7() throws Exception {
        WSSikkerhetsbegrensning begrensning = new WSSikkerhetsbegrensning().withFeilaarsak(FeilAarsaker.FP2_FA.name());
        HentPersonSikkerhetsbegrensning exception = new HentPersonSikkerhetsbegrensning("Ingen tilgang", begrensning);
        when(portType.hentPerson(any(WSHentPersonRequest.class))).thenThrow(exception);
        try {
            service.hentPerson(request);
        } catch (AuthorizationException ae) {
            assertThat(ae.getMessage(), equalTo("sikkerhetsbegrensning.diskresjonskode7"));
            throw ae;
        }
    }

    @Test(expected = AuthorizationException.class)
    public void harIkkeTilgangGeografisk() throws Exception {
        when(portType.hentPerson(any(WSHentPersonRequest.class))).thenThrow(new HentPersonSikkerhetsbegrensning());

        try {
            service.hentPerson(request);
        } catch (AuthorizationWithSikkerhetstiltakException ae) {
            assertThat(ae.getMessage(), equalTo("sikkerhetsbegrensning.geografisk"));
            throw  ae;
        }
    }

    @Test
    public void harTilgangMedBegrunnelse() throws Exception {
        HentKjerneinformasjonRequest requestWithBegrunnelse = new HentKjerneinformasjonRequest(IDENT);
        requestWithBegrunnelse.setBegrunnet(true);

        HentKjerneinformasjonResponse response = service.hentPerson(requestWithBegrunnelse);

        assertNotNull(response.getPerson());
    }

    @Ignore
    @Test(expected = RecoverableAuthorizationException.class)
    public void harTilgangVedUtviding() throws Exception {
        service.hentPerson(request);
    }

    @Test
    public void harTilgangFamilierelasjonFilteringFjernerIngen() throws Exception {
        response.setPerson(new WSBruker()
                .withHarFraRolleI(new WSFamilierelasjon(), new WSFamilierelasjon(), new WSFamilierelasjon()));

        HentKjerneinformasjonResponse response = service.hentPerson(request);

        assertNotNull(response.getPerson());
        assertEquals(3, response.getPerson().getPersonfakta().getHarFraRolleIList().size());
    }

    @Test
    public void harTilgangFamilierelasjonFilteringFjernerSiste() throws Exception {
        WSPerson person = new WSBruker()
                .withHarFraRolleI(new WSFamilierelasjon(), new WSFamilierelasjon(), new WSFamilierelasjon(), mockTomFamilieRelasjon(FNR_BRUKER));
        response.setPerson(person);

        HentKjerneinformasjonResponse response = service.hentPerson(request);

        assertNotNull(response.getPerson());
        assertEquals(3, response.getPerson().getPersonfakta().getHarFraRolleIList().size());
    }

    @Test
    public void harTilgangFamilierelasjonFilteringFjernerToSiste() throws Exception {
        WSPerson person = new WSBruker().withHarFraRolleI(new WSFamilierelasjon(), new WSFamilierelasjon(),
                new WSFamilierelasjon(), mockTomFamilieRelasjon(FNR_SAMBOER), mockTomFamilieRelasjon(FNR_SAMBOER));
        response.setPerson(person);

        HentKjerneinformasjonResponse response = service.hentPerson(request);

        assertNotNull(response.getPerson());
        assertEquals(3, response.getPerson().getPersonfakta().getHarFraRolleIList().size());
    }

    @Test
    public void harTilgangFamilierelasjonFilteringFjernerDenIMidten() throws Exception {
        WSPerson person = new WSBruker().withHarFraRolleI(new WSFamilierelasjon(), new WSFamilierelasjon(),
                new WSFamilierelasjon(), mockTomFamilieRelasjon(FNR_SAMBOER),
                mockFactory.getMockFamilieRelasjon("samboer", FNR_SAMBOER));
        response.withPerson(person);

        HentKjerneinformasjonResponse response = service.hentPerson(request);

        assertNotNull(response.getPerson());
        assertEquals(4, response.getPerson().getPersonfakta().getHarFraRolleIList().size());
    }

    @Test
    public void skalOppdatereAnsvarligEnhetMedInformasjonFraNORGDersomGeografiskTilknytningErSatt() throws Exception {
        final String ansvarligEnhetId = "9999";
        final String ansvarligEnhetNavn = "NAV Ansvarlig Enhet";

        when(organisasjonEnhetV2Service.finnNAVKontor(anyString(), anyString()))
                .thenReturn(of(new AnsattEnhet(ansvarligEnhetId, ansvarligEnhetNavn)));

        final HentKjerneinformasjonResponse hentKjerneinformasjonResponse = service.hentPerson(new HentKjerneinformasjonRequest(FNR_BRUKER));

        assertEquals(ansvarligEnhetId, hentKjerneinformasjonResponse.getPerson().getPersonfakta()
                .getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId());
        assertEquals(ansvarligEnhetNavn, hentKjerneinformasjonResponse.getPerson().getPersonfakta()
                .getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementNavn());
    }

    @Test
    public void skalIkkeHenteInformasjonFraNORGDersomGeografiskTilknytningErEtLand() throws Exception {
        WSBruker bruker = new WSBruker().withGeografiskTilknytning(new WSLand().withGeografiskTilknytning("FIN"));
        when(portType.hentPerson(any())).thenReturn(new WSHentPersonResponse().withPerson(bruker));

        service.hentPerson(new HentKjerneinformasjonRequest(FNR_BRUKER));

        verify(organisasjonEnhetV2Service, never()).finnNAVKontor(anyString(), anyString());
    }

    @Test
    public void skalHenteInformasjonFraNORGDersomKodeStrengtFortroligAdresse() throws HentPersonPersonIkkeFunnet, HentPersonSikkerhetsbegrensning {
        when(portType.hentPerson(any(WSHentPersonRequest.class)))
                .thenReturn(new WSHentPersonResponse()
                        .withPerson(new WSBruker()
                                .withDiskresjonskode(new WSDiskresjonskoder()
                                        .withValue(STRENGT_FORTROLIG_ADRESSE))
                                .withGeografiskTilknytning(null)));

        service.hentPerson(new HentKjerneinformasjonRequest(FNR_BRUKER));

        verify(organisasjonEnhetV2Service, atLeastOnce()).finnNAVKontor(null, STRENGT_FORTROLIG_ADRESSE);
    }

    @Test
    public void skalIkkeHentePersonVedUgyldigFnr() {
        HentPersonService hps = new HentPersonService(null, null, null, null);
        try {
            hps.hentPerson(new HentKjerneinformasjonRequest("falsk ident"));
        } catch(ApplicationException ae) {
            assertEquals(HentPersonPersonIkkeFunnet.class, ae.getCause().getClass());
            assertEquals("UgyldigFnr", ae.getMessage());
        }
    }

    private WSFamilierelasjon mockTomFamilieRelasjon(String fodselnummer) {
        WSFamilierelasjon familierelasjon = new WSFamilierelasjon();
        WSPerson person = new WSPerson();
        person.setAktoer(new WSPersonIdent().withIdent(new WSNorskIdent().withIdent(fodselnummer)));
        familierelasjon.setTilPerson(person);
        return familierelasjon;
    }
}