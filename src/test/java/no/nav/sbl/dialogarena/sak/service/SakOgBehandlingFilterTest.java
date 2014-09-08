package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingsstegtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSSakstemaer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SakOgBehandlingFilterTest {

    @Mock
    private CmsContentRetriever cmsContentRetriever;

    @InjectMocks
    private SakOgBehandlingFilter sakOgBehandlingFilter = new SakOgBehandlingFilter();

    @Before
    public void setup() {
        when(cmsContentRetriever.hentTekst("filter.ulovligesakstema")).thenReturn("FEI, SAK,SAP  ");
        when(cmsContentRetriever.hentTekst("filter.lovligebehandlingstyper")).thenReturn("ae0047,ae0034,ae0014");
    }

    @Test
    public void filtrererSakstema() throws Exception {
        List<WSSak> saker = Arrays.asList(
                createWSSak().withSakstema(new WSSakstemaer().withValue("FEI")).withBehandlingskjede( // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede(
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("SAK")).withBehandlingskjede( // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("SAP")).withBehandlingskjede( // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                )
        );

        List<WSSak> filtrerteSaker = sakOgBehandlingFilter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(1));
        assertThat(filtrerteSaker.get(0).getSakstema().getValue(), is("AAP"));
    }

    @Test
    @Ignore
    public void filtrererBehandlingstyper() throws Exception {
        List<WSSak> saker = Arrays.asList(
                createWSSak().withSakstema(new WSSakstemaer().withValue("DAG")).withBehandlingskjede(
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0001")), // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("DAG")).withBehandlingskjede(
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0034")),
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0047"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("FEI")).withBehandlingskjede(                      // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0034")),
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0047"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede(
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0001")), // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0002")) // Ulovlig
                )
        );

        List<WSSak> filtrerteSaker = sakOgBehandlingFilter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(2));
        assertThat(filtrerteSaker.get(0).getBehandlingskjede().size(), is(1));
        assertThat(filtrerteSaker.get(1).getBehandlingskjede().size(), is(2));
    }

    public static WSSak createWSSak() {
        return new WSSak()
                .withSaksId("saksId-mock")
                .withSakstema(new WSSakstemaer().withValue("DAG").withKodeverksRef("kodeverk-ref-mock"))
                .withOpprettet(now());
    }

    public static WSBehandlingskjede createWSBehandlingskjede() {
        return new WSBehandlingskjede()
                .withBehandlingskjedeId("behandlingskjedeid-mock")
                .withBehandlingstema(new WSBehandlingstemaer().withKodeverksRef("kodeverk-ref-mock"))
                .withBehandlingstema(new WSBehandlingstemaer().withKodeverksRef("kodeverk-tema-mock"))
                .withStart(now())
                .withSisteBehandlingREF("siste-behandling-ref-mock")
                .withSisteBehandlingstype(new WSBehandlingstyper().withKodeverksRef("behandlingstype-ref-mock"))
                .withSisteBehandlingsstegREF("siste-behandling-steg-ref-mock")
                .withSisteBehandlingsstegtype(new WSBehandlingsstegtyper().withKodeverksRef("behandlingssteg-ref-mock"));
    }
}
