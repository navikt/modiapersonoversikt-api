package no.nav.modiapersonoversikt.consumer.kodeverk2;

import no.nav.modiapersonoversikt.consumer.kodeverk2.exception.KodeverkTjenesteFeiletException;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKodeverk;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class CachingKodeverkClientTest {

    private CachingKodeverkClient cachingClient;
    private KodeverkClient kodeverkClient;

    private final File dumpDir = new File("target", "kodeverkdump/" + UUID.randomUUID().toString());

    @Before
    public void setUp() {
        kodeverkClient = mock(DefaultKodeverkClient.class);
        when(kodeverkClient.hentKodeverk(anyString())).thenReturn(new XMLEnkeltKodeverk().withVersjonsnummer("v1"));

        cachingClient = new CachingKodeverkClient(kodeverkClient, of(dumpDir));
    }

    @Test
    public void skalHenteKodeverk() {
        XMLKodeverk kodeverk = cachingClient.hentKodeverk("testkodeverk");

        assertThat(kodeverk, notNullValue());
        assertThat(kodeverk.getVersjonsnummer(), is("v1"));
    }

    @Test
    public void skalHenteKodeverkKunEnGang() {
        cachingClient.hentKodeverk("testkodeverk");
        cachingClient.hentKodeverk("testkodeverk");

        verify(kodeverkClient, times(1)).hentKodeverk("testkodeverk");
    }

    @Test
    public void skalHenteKodeverkFraDiskVedOppstart() {
        KodeverkClient kodeverkClientFeiler = mock(DefaultKodeverkClient.class);
        when(kodeverkClientFeiler.hentKodeverk(anyString())).thenThrow(new KodeverkTjenesteFeiletException(null));
        KodeverkClient cachingClientFeiler = new CachingKodeverkClient(kodeverkClientFeiler, of(dumpDir));

        // hent kodeverk fra fungerende klient slik at det blir skrevet til disk
        cachingClient.hentKodeverk("testkodeverk");

        // hent kodeverk fra feilende klient, fallbackmekanismen skal da lese fra disk
        XMLKodeverk kodeverk = cachingClientFeiler.hentKodeverk("testkodeverk");

        assertThat(kodeverk, notNullValue());
    }

    @Test(expected = KodeverkTjenesteFeiletException.class)
    public void skalKasteExceptionHvisTjenesteErNedeOgDiskFallbackIkkeFinnes() {
        KodeverkClient kodeverkClientFeiler = mock(DefaultKodeverkClient.class);
        when(kodeverkClientFeiler.hentKodeverk(anyString())).thenThrow(new KodeverkTjenesteFeiletException(null));
        KodeverkClient cachingClientFeiler = new CachingKodeverkClient(kodeverkClientFeiler, of(dumpDir));

        cachingClientFeiler.hentKodeverk("testkodeverk");
    }

    @Test
    public void skalOppdatereKodeverk() {
        XMLKodeverk kodeverk = cachingClient.hentKodeverk("testkodeverk");
        assertThat(kodeverk.getVersjonsnummer(), is("v1"));

        when(kodeverkClient.hentKodeverk("testkodeverk")).thenReturn(new XMLEnkeltKodeverk().withVersjonsnummer("v2"));
        cachingClient.oppdaterKodeverk();

        XMLKodeverk nyttKodeverk = cachingClient.hentKodeverk("testkodeverk");
        assertThat(nyttKodeverk.getVersjonsnummer(), is("v2"));
    }
}
