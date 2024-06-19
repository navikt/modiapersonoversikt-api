package no.nav.modiapersonoversiktproxy.consumer.infortrygd.foreldrepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ForeldrepengerMockServiceTest {
    ForeldrepengerMockService mockService;

    @Before
    public void setUp() {
        mockService = new ForeldrepengerMockService(ForeldrepengerMapper.getInstance());
    }

    @Test
    public void testHentOppfolgingskontrakter() {
        ForeldrepengerListeRequest request = new ForeldrepengerListeRequest();

        ForeldrepengerListeResponse response = mockService.hentForeldrepengerListe(request);

        assertNotNull(response);
    }
}
