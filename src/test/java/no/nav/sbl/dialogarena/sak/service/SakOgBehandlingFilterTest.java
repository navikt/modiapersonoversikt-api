package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingsstegtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSSakstemaer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
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
        when(cmsContentRetriever.hentTekst("filter.ulovligesakstema")).thenReturn("FEI,SAK, SAP");
        when(cmsContentRetriever.hentTekst("filter.lovligebehandlingstyper")).thenReturn("ae0047, ae0034, ae0014");
    }

    @Test
    public void filtrererSakstema() throws Exception {
        List<WSSak> saker = asList(
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
    public void filtrererBehandlingstyper() throws Exception {
        List<GenerellBehandling> alleBehandlinger = new ArrayList<>();
        alleBehandlinger.addAll(asList(
                new GenerellBehandling().withBehandlingsType("ae0047").withBehandlingsDato(now().minusDays(1)), //lovlig
                new GenerellBehandling().withBehandlingsType("ae0034").withBehandlingsDato(now().minusDays(2)), //lovlig
                new GenerellBehandling().withBehandlingsType("ae0014").withBehandlingsDato(now().minusDays(3)), //lovlig
                new GenerellBehandling().withBehandlingsType("LOL1337").withBehandlingsDato(now().minusDays(4)), //ulovlig
                new Kvittering().withAvsluttet(true).withBehandlingsDato(now().minusDays(5))
        ));

        List<GenerellBehandling> filtrerteBehandlinger = sakOgBehandlingFilter.filtrerBehandlinger(alleBehandlinger);

        assertThat(filtrerteBehandlinger.size(), is(4));
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
