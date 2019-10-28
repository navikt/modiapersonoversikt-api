package no.nav.kontrakter.consumer.fim.oppfolgingskontrakt;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.mock.OppfolgingkontraktMockService;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class OppfolgingkontraktMockServiceTest {
    OppfolgingkontraktMockService mockService;

    @Before
    public void setUp() throws Exception {
        mockService = new OppfolgingkontraktMockService();
        mockService.setMapper(OppfolgingskontraktMapper.getInstance());

    }

    @Test
    public void testHentOppfolgingskontrakter() throws Exception {
        OppfolgingskontraktRequest request = new OppfolgingskontraktRequest();

        OppfolgingskontraktResponse response = mockService.hentOppfolgingskontrakter(request);

        assertNotNull(response);
    }
}
