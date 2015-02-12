package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSAvslutningsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSSakstemaer;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.createWSBehandlingskjede;
import static no.nav.sbl.dialogarena.sak.service.Filter.DOKUMENTINNSENDING_KVITTERINGSTYPE;
import static no.nav.sbl.dialogarena.sak.service.Filter.SEND_SOKNAD_KVITTERINGSTYPE;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilterTest {

    @Mock
    private CmsContentRetriever cmsContentRetriever;

    @InjectMocks
    private Filter filter = new Filter();

    @Before
    public void setup() {
        when(cmsContentRetriever.hentTekst("filter.ulovligesakstema")).thenReturn("FEI,SAK, SAP");
        when(cmsContentRetriever.hentTekst("filter.lovligebehandlingstyper")).thenReturn("ae0047, ae0034, ae0014");
    }

    @Test
    public void ulovligeTema_slipperIkkeGjennomFilter() throws Exception {
        List<WSSak> saker = asList(
                createWSSak().withSakstema(new WSSakstemaer().withValue("FEI")).withBehandlingskjede( // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede( // Lovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("SAK")).withBehandlingskjede( // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("SAP")).withBehandlingskjede( // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                )
        );

        List<WSSak> filtrerteSaker = filter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(1));
        assertThat(filtrerteSaker.get(0).getSakstema().getValue(), is("AAP"));
    }

    @Test
    public void temaUtenBehandlinger_slipperIkkeGjennomFilter() throws Exception {
        List<WSSak> saker = asList(
                createWSSak().withSakstema(new WSSakstemaer().withValue("PEN")), // Lovlig tema, men ingen behandlinger
                createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede( // Lovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                )
        );

        List<WSSak> filtrerteSaker = filter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(1));
    }

    @Test
    public void temaMedKunUlovligeBehandlingstyper_slipperIkkeGjennomFilter() throws Exception {
        String lovligType = "ae0014";
        List<WSSak> saker = asList(
                createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede( // Lovlig tema, men ulovlig behandlingstype
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("LOL1337"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("PEN")).withBehandlingskjede( // Lovlig
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue(lovligType))
                )
        );

        List<WSSak> filtrerteSaker = filter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(1));
        assertThat(filtrerteSaker.get(0).getBehandlingskjede().get(0).getSisteBehandlingstype().getValue(), equalTo(lovligType));
    }

    @Test
    public void temaMedKvittering_slipperGjennomFilter() throws Exception {
        List<WSSak> saker = asList(
                createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede( // Lovlig tema, men ulovlig behandlingstype
                        createWSBehandlingskjede().withSisteBehandlingstype(new WSBehandlingstyper().withValue("LOL1337"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("PEN")).withBehandlingskjede( // Lovlig, men ingen lovlige typer: Kun avsluttet kvittering
                        createWSBehandlingskjede()
                                .withSlutt(new DateTime())
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue(SEND_SOKNAD_KVITTERINGSTYPE))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))

                )
        );

        List<WSSak> filtrerteSaker = filter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(1));
        assertThat(filtrerteSaker.get(0).getBehandlingskjede().get(0).getSisteBehandlingstype().getValue(), equalTo(SEND_SOKNAD_KVITTERINGSTYPE));
    }

    @Test
    public void lovligeTyperOgKvitteringer_slipperGjennomFilter() throws Exception {
        List<GenerellBehandling> alleBehandlinger = new ArrayList<>();
        alleBehandlinger.addAll(asList(
                new GenerellBehandling().withBehandlingStatus(AVSLUTTET).withBehandlingsType("LOL1337").withBehandlingsDato(now().minusDays(4)), //ulovlig
                new GenerellBehandling().withBehandlingStatus(AVSLUTTET).withBehandlingsType("ae0047").withBehandlingsDato(now().minusDays(1)), //lovlig
                new GenerellBehandling().withBehandlingStatus(AVSLUTTET).withBehandlingsType("ae0034").withBehandlingsDato(now().minusDays(2)), //lovlig
                new GenerellBehandling().withBehandlingStatus(AVSLUTTET).withBehandlingsType("ae0014").withBehandlingsDato(now().minusDays(3)), //lovlig
                new Kvittering().withBehandlingStatus(AVSLUTTET).withBehandlingsDato(now().minusDays(5)).withBehandlingsType(DOKUMENTINNSENDING_KVITTERINGSTYPE) //kvittering, lovlig
        ));

        List<GenerellBehandling> filtrerteBehandlinger = filter.filtrerBehandlinger(alleBehandlinger);

        assertThat(filtrerteBehandlinger.size(), is(4));
    }

    @Test
    public void sakMedFlereUlovligeBehandlinger_slipperIkkeGjennomFilter() {
        DateTime tolvteDesember2014 = new DateTime().withYear(2014).withMonthOfYear(12).withDayOfMonth(9);
        List<WSSak> saker = asList(
                new WSSak()
                        .withSakstema(new WSSakstemaer().withValue("FOS"))
                        .withSaksId("000379625")
                        .withOpprettet(tolvteDesember2014)
                        .withBehandlingskjede(
                                new WSBehandlingskjede()
                                        .withBehandlingskjedeId("000WNMM")
                                        .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("ad0003"))
                                        .withBehandlingstema(new WSBehandlingstemaer().withValue("ab0142"))
                                        .withStart(tolvteDesember2014)
                                        .withSlutt(tolvteDesember2014.plus(1))
                                        .withSisteBehandlingREF("171000L9S")
                                        .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0034"))
                                        .withBehandlingsListeRef("171000L9S")
                                        .withSisteBehandlingsoppdatering(tolvteDesember2014)
                                        .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))
                                        .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("opphoert")),
                                new WSBehandlingskjede()
                                        .withBehandlingskjedeId("000WQ5U")
                                        .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("ad0003"))
                                        .withBehandlingstema(new WSBehandlingstemaer().withValue("ab0142"))
                                        .withStart(tolvteDesember2014.plus(2))
                                        .withSlutt(tolvteDesember2014.plus(3))
                                        .withSisteBehandlingREF("161000UVV")
                                        .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0028"))
                                        .withBehandlingsListeRef("WSBehandlingstyper")
                                        .withSisteBehandlingsoppdatering(tolvteDesember2014.plus(2))
                                        .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))
                                        .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("opphoert"))
                                )

        );

        List<WSSak> filtrerteSaker = filter.filtrerSaker(saker);

        assertThat(filtrerteSaker.size(), is(0));
    }

    public static WSSak createWSSak() {
        return new WSSak()
                .withSaksId("saksId-mock")
                .withSakstema(new WSSakstemaer().withValue("DAG").withKodeverksRef("kodeverk-ref-mock"))
                .withOpprettet(now());
    }
}
