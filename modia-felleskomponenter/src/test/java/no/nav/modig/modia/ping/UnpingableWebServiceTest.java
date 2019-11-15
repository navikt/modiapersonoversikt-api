package no.nav.modig.modia.ping;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnpingableWebServiceTest {

    @Test
    public void skalReturnereUnpingable() {
        String navn = "gammeltRakkel";
        String adresse = "http://gammeltRakkel.com";
        UnpingableWebService ubrukeligTjenesteUtenPing = new UnpingableWebService(navn, adresse);
        PingResult ping = ubrukeligTjenesteUtenPing.ping();
        assertThat(ping.getServiceStatus(), is(PingResult.ServiceResult.UNPINGABLE));

        assertThat(ubrukeligTjenesteUtenPing.name(), is(navn));
        assertThat(ubrukeligTjenesteUtenPing.endpoint(), is(adresse));
        assertThat(ubrukeligTjenesteUtenPing.method(), is("-"));
    }
}