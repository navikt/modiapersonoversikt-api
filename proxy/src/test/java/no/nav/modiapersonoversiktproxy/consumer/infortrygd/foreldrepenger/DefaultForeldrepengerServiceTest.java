package no.nav.modiapersonoversiktproxy.consumer.infortrygd.foreldrepenger;

import no.nav.modiapersonoversiktproxy.commondomain.Periode;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.DefaultForeldrepengerService;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.HentForeldrepengerettighetSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetRequest;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DefaultForeldrepengerServiceTest {

    private static final String IDENT = "11223344556";
    private static ForeldrepengerListeRequest request;
    private static FimHentForeldrepengerettighetResponse rawResponse;
    private static ForeldrepengerMockFactory mockFactory;
    private static ForeldrepengerMapper mapper;
    private static Periode periode;

    private ForeldrepengerServiceBi service;
    @Mock
    private ForeldrepengerV2 portType;

    @BeforeClass
    public static void setUpOnce() {
        mapper = ForeldrepengerMapper.getInstance();
        mockFactory = new ForeldrepengerMockFactory();
        periode = new Periode();
        request = new ForeldrepengerListeRequest(IDENT, periode);
        rawResponse = ForeldrepengerMockFactory.createFimHentForeldrepengerListeResponse();
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        service = new DefaultForeldrepengerService();
        ((DefaultForeldrepengerService)service).setMapper(mapper);
        ((DefaultForeldrepengerService)service).setForeldrepengerService(portType);
    }

    @Test(expected = RuntimeException.class)
    public void hentForeldrepengerFinnesIkke() throws Exception {
        when(portType.hentForeldrepengerettighet(any(FimHentForeldrepengerettighetRequest.class))).thenThrow(new HentForeldrepengerettighetSikkerhetsbegrensning());
        service.hentForeldrepengerListe(request);
    }

}