package no.nav.modig.modia.ping;

import no.nav.sbl.dialogarena.types.Pingable;
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
        Pingable.Ping pingResult = pingable.ping();
        assertThat(pingResult.erVellykket(), is(true));
        assertThat(pingResult.getMetadata().getBeskrivelse(), is("WS"));
    }

    @Test
    public void skalReturnereFeilNarTjenestenFeiler() throws Exception {
        when(ws.ping()).thenThrow(new RuntimeException());
        Pingable.Ping pingResult = pingable.ping();
        assertThat(pingResult.erVellykket(), is(false));
    }

    private class UnpingableWS {

    }


}