package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.ytelseskontrakt;

import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.ytelseskontrakt.mock.YtelseskontraktMockService;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class YtelseskontraktMockServiceTest {
    YtelseskontraktMockService mockService;

    @Before
    public void setUp() {
        mockService = new YtelseskontraktMockService();
        mockService.setMapper(YtelseskontraktMapper.getInstance());
    }

    @Test
    public void testHentYtelseskontrakter() {
        YtelseskontraktRequest request = new YtelseskontraktRequest();

        YtelseskontraktResponse response = mockService.hentYtelseskontrakter(request);

        assertEquals(3, response.getYtelser().size());
    }
}
