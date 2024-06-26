package no.nav.modiapersonoversiktproxy.consumer.infortrygd.sykepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.SykepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to.SykepengerResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SykepengerMockServiceTest {
    SykepengerMockService mockService;

    @Before
    public void setUp() {
        mockService = new SykepengerMockService(SykepengerMapper.getInstance());
    }

    @Test
    public void testHentOppfolgingskontrakter() {
        SykepengerRequest request = new SykepengerRequest();

        SykepengerResponse response = mockService.hentSykmeldingsperioder(request);

        assertNotNull(response);
    }

}
