package no.nav.modig.modia.ping;

import no.nav.common.health.selftest.SelfTestCheck;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UnpingableWebServiceTest {

    @Test
    public void skalReturnereUnpingable() {
        String navn = "gammeltRakkel";
        String adresse = "http://gammeltRakkel.com";
        UnpingableWebService ubrukeligTjenesteUtenPing = new UnpingableWebService(navn, adresse);
        SelfTestCheck ping = ubrukeligTjenesteUtenPing.ping();

        assertThat(ping.isCritical(), is(false));
        assertThat(ping.getDescription(), is("gammeltRakkel via http://gammeltRakkel.com"));
        assertThat(ping.getCheck().checkHealth().isHealthy(), is(true));
    }
}
