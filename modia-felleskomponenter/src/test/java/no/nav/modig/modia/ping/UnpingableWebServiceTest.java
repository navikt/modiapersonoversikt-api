package no.nav.modig.modia.ping;

import no.nav.sbl.dialogarena.types.Pingable;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnpingableWebServiceTest {

    @Test
    public void skalReturnereUnpingable() {
        String navn = "gammeltRakkel";
        String adresse = "http://gammeltRakkel.com";
        UnpingableWebService ubrukeligTjenesteUtenPing = new UnpingableWebService(navn, adresse);
        Pingable.Ping ping = ubrukeligTjenesteUtenPing.ping();

        assertThat(ping.erAvskrudd(), is(true));

        assertThat(ping.getMetadata().getId(), is(navn));
        assertThat(ping.getMetadata().getEndepunkt(), is(adresse));
    }
}