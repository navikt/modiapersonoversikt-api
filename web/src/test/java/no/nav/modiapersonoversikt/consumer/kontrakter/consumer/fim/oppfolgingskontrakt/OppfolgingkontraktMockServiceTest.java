package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt;

import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.mock.OppfolgingkontraktMockService;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktRequest;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktResponse;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class OppfolgingkontraktMockServiceTest {
    OppfolgingkontraktMockService mockService;

    @Before
    public void setUp() {
        mockService = new OppfolgingkontraktMockService();
        mockService.setMapper(OppfolgingskontraktMapper.getInstance());

    }

    @Test
    public void testHentOppfolgingskontrakter() {
        OppfolgingskontraktRequest request = new OppfolgingskontraktRequest();

        OppfolgingskontraktResponse response = mockService.hentOppfolgingskontrakter(request);

        assertNotNull(response);
    }
}
