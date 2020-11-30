package no.nav.modig.modia.ping;

import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.sbl.dialogarena.types.Pingable;
import org.hamcrest.CoreMatchers;
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
    public void skalKallePingNarKlassenErPingable() throws Exception {
        pingable.ping().getCheck().checkHealth();
        verify(ws, times(1)).ping();
    }

    @Test
    public void skalReturnereOkResultatNarTjenestenErOk() throws Exception {
        SelfTestCheck pingResult = pingable.ping();
        assertThat(pingResult.getCheck().checkHealth().isHealthy(), is(true));
        assertThat(pingResult.getDescription(), CoreMatchers.containsString("WS"));
    }

    @Test
    public void skalReturnereFeilNarTjenestenFeiler() throws Exception {
        when(ws.ping()).thenThrow(new RuntimeException());
        SelfTestCheck pingResult = pingable.ping();
        assertThat(pingResult.getCheck().checkHealth().isHealthy(), is(false));
    }

    private class UnpingableWS {

    }


}
