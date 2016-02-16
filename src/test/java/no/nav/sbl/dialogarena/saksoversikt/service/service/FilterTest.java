package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.saksoversikt.service.mock.MockCreationUtil.createWSBehandlingskjede;
import static no.nav.sbl.dialogarena.saksoversikt.service.mock.MockCreationUtil.createWSSak;
import static org.hamcrest.Matchers.is;
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
        when(cmsContentRetriever.hentTekst("filter.ulovligesakstema")).thenReturn("FEI, SAK,SAP  ");
        when(cmsContentRetriever.hentTekst("filter.lovligebehandlingstyper")).thenReturn("ae0047,ae0034,ae0014");
    }

    @Test
    public void sjekkerLovligeTema_vedFiltrering_avSaker() throws Exception {
        List<WSSak> saker = asList(
                createWSSak().withSakstema(new WSSakstemaer().withValue("FEI")).withBehandlingskjede( // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede(
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("SAK")).withBehandlingskjede( // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("SAP")).withBehandlingskjede( // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                )
        );

        List<WSSak> filtrerteSaker = filter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(1));
        assertThat(filtrerteSaker.get(0).getSakstema().getValue(), is("AAP"));
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

    @Test
    public void sjekkerBehandlingstyper_vedFiltrering_avSaker() throws Exception {
        List<WSSak> saker = asList(
                createWSSak().withSakstema(new WSSakstemaer().withValue("DAG")).withBehandlingskjede(
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0001")), // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                                .withSisteBehandlingREF("lovlig")
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("HJL")).withBehandlingskjede(
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0034"))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet")),
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0047"))
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("FEI")).withBehandlingskjede(                      // Ulovlig
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0034")).withSisteBehandlingREF("lovlig"),
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0047")).withSisteBehandlingREF("lovlig")
                ),
                createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede(
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET)).withSlutt(new DateTime()).withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0001"))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet")), // Ulovlig, men avsluttet kvittering
                        createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET)).withSlutt(new DateTime()).withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0002"))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))// Ulovlig, men avsluttet kvittering
                )
        );

        List<WSSak> filtrerteSaker = filter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(3));
    }

}
