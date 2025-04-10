package no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt;

import no.nav.modiapersonoversikt.arena.ytelseskontrakt.YtelseskontraktResponse;
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.mock.YtelseskontraktMockService;
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
