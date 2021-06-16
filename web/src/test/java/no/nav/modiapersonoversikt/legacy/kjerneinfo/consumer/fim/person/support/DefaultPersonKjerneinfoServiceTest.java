package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.support;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.mock.PersonKjerneinfoMockFactory;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Diskresjonskoder;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kommune;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Sikkerhetstiltak;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentSikkerhetstiltakRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentSikkerhetstiltakResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultPersonKjerneinfoServiceTest {

    private static final String FODSELSNUMMER = "10108000398";
    private static final String STRENGT_FORTROLIG_ADRESSE = "SPSF";
    private static final String GEOGRAFISK_TILKNYTNING = "0219";

    private static PersonKjerneinfoMockFactory mockFactory;
    private static KjerneinfoMapper mapper;

    private PersonKjerneinfoServiceBi service;

    @Mock
    private PersonV3 portType;
    private Tilgangskontroll tilgangskontroll = TilgangskontrollMock.get();

    @Mock
    private OrganisasjonEnhetV2Service organisasjonEnhetV2Service;

    @BeforeClass
    public static void setUpOnce() {
        DefaultKodeverkmanager kodeverk = new DefaultKodeverkmanager(mock(KodeverkPortType.class));
        mapper = new KjerneinfoMapper(kodeverk);
        mockFactory = new PersonKjerneinfoMockFactory();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockFactory = new PersonKjerneinfoMockFactory();
        service = new DefaultPersonKjerneinfoService(portType, mapper, tilgangskontroll, organisasjonEnhetV2Service);
        when(organisasjonEnhetV2Service.finnNAVKontor(anyString(), anyString())).thenReturn(of(new AnsattEnhet("1234", "NAV Mockenhet")));
    }

    @Test
    public void hentKjerneinformasjon() throws HentPersonPersonIkkeFunnet, HentPersonSikkerhetsbegrensning {
        when(portType.hentPerson(any(HentPersonRequest.class))).thenReturn(new HentPersonResponse()
                .withPerson(mockFactory.getBruker(FODSELSNUMMER, true)));

        service.hentKjerneinformasjon(new HentKjerneinformasjonRequest(FODSELSNUMMER));

        verify(portType, times(1)).hentPerson(any(HentPersonRequest.class));
    }

    @Test
    public void hentGeografiskTilknytning() throws HentGeografiskTilknytningSikkerhetsbegrensing, HentGeografiskTilknytningPersonIkkeFunnet {
        when(portType.hentGeografiskTilknytning(any(HentGeografiskTilknytningRequest.class))).thenReturn(lagResponse());

        GeografiskTilknytning response = service.hentGeografiskTilknytning(FODSELSNUMMER);

        verify(portType, times(1)).hentGeografiskTilknytning(any(HentGeografiskTilknytningRequest.class));
        assertThat(response.getValue(), is(GEOGRAFISK_TILKNYTNING));
        assertThat(response.getDiskresjonskode(), is(STRENGT_FORTROLIG_ADRESSE));
    }

    @Test
    public void hentSikkerhetstiltak() throws Exception {
        HentSikkerhetstiltakResponse response = new HentSikkerhetstiltakResponse()
                .withSikkerhetstiltak(new Sikkerhetstiltak()
                        .withSikkerhetstiltaksbeskrivelse("Farlig person."));
        when(portType.hentSikkerhetstiltak(any(no.nav.tjeneste.virksomhet.person.v3.meldinger.HentSikkerhetstiltakRequest.class))).thenReturn(response);

        service.hentSikkerhetstiltak(new no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest(FODSELSNUMMER));

        verify(portType, times(1)).hentSikkerhetstiltak(any(HentSikkerhetstiltakRequest.class));
    }

    private HentGeografiskTilknytningResponse lagResponse() {
        return new HentGeografiskTilknytningResponse()
                .withDiskresjonskode(new Diskresjonskoder().withValue(STRENGT_FORTROLIG_ADRESSE))
                .withGeografiskTilknytning(new Kommune().withGeografiskTilknytning(GEOGRAFISK_TILKNYTNING));
    }
}
