package no.nav.modig.modia.ping;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

public class PingableWebServiceTest {

    private Pingable ws;
    private PingableWebService pingable;

    @Before
    public void setUp() throws Exception {
        ws = mock(Pingable.class);
        pingable = new PingableWebService("WS", ws);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void skalFeileNarKlassenIkkeHarPing() throws Exception {
        PingableWebService pingable = new PingableWebService("WS", new UnpingableWS());
        pingable.ping();
    }

    @Test(expected = AssertionError.class)
    public void skalFeilePingableIkkeNavngis() throws Exception {
        PingableWebService pingable = new PingableWebService("", ws);
    }

    @Test
    public void skalKallePingNårKlassenErPingable() throws Exception {
        pingable.ping();
        verify(ws, times(1)).ping();
    }

    @Test
    public void skalReturnereOkResultatNårTjenestenErOk() throws Exception {
        PingResult pingResult = pingable.ping();
        assertThat(pingResult.getServiceStatus(), is(PingResult.ServiceResult.SERVICE_OK));
        assertThat(pingable.name(), is("WS"));
        assertThat(pingable.method(), is("ping"));
    }

    @Test
    public void skalReturnereFeilNarTjenestenFeiler() throws Exception {
        when(ws.ping()).thenThrow(new RuntimeException());
        PingResult pingResult = pingable.ping();
        assertThat(pingResult.getServiceStatus(), is(PingResult.ServiceResult.SERVICE_FAIL));
    }

    private class UnpingableWS {

    }


}