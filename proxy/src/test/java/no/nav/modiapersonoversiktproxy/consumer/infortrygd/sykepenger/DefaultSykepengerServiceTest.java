package no.nav.modiapersonoversiktproxy.consumer.infortrygd.sykepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.DefaultSykepengerService;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.SykepengerServiceBi;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.SykepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to.SykepengerRequest;
import no.nav.tjeneste.virksomhet.sykepenger.v2.HentSykepengerListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeRequest;
import no.nav.tjeneste.virksomhet.sykepenger.v2.meldinger.FimHentSykepengerListeResponse;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DefaultSykepengerServiceTest {

    private static final String IDENT = "11223344556";
    private static SykepengerRequest request;
    private static FimHentSykepengerListeResponse rawResponse;
    private static SykepengerMapper mapper;
    private static final LocalDate from = LocalDate.now().minusMonths(2);
    private static final LocalDate to = LocalDate.now();

    private SykepengerServiceBi service;
    @Mock
    private SykepengerV2 portType;

    @BeforeClass
    public static void setUpOnce() {
        mapper = SykepengerMapper.getInstance();
        request = new SykepengerRequest(from, IDENT, to);
        rawResponse = SykepengerMockFactory.createFimHentSykepengerResponse();
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        service = new DefaultSykepengerService();
        ((DefaultSykepengerService)service).setMapper(mapper);
        ((DefaultSykepengerService)service).setSykepengerService(portType);

    }

    @Test(expected = RuntimeException.class)
    public void hentSykepengerFinnesIkke() throws Exception {
        when(portType.hentSykepengerListe(any(FimHentSykepengerListeRequest.class))).thenThrow(new HentSykepengerListeSikkerhetsbegrensning());
        service.hentSykmeldingsperioder(request);
    }

}