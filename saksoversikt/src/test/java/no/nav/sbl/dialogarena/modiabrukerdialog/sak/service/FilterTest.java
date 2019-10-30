package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock.MockCreationUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.BehandlingsStatus;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.BehandlingsType;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter.Filter;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter.FilterUtils;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilterTest {

    @Mock
    private ContentRetriever cms;

    @InjectMocks
    private Filter filter = new Filter();

    @Before
    public void setup() {
        when(cms.hentTekst("filter.lovligebehandlingstyper")).thenReturn("ae0047,ae0034,ae0014,ae0020,ae0019,ae0011,ae0045");
        when(cms.hentTekst("filter.ulovligesakstema")).thenReturn("FEI,SAK,SAP,OPP,YRA,GEN,AAR,KLA,HEL");
    }

    @Test
    public void sjekkerLovligeTema_vedFiltrering_avSaker() throws Exception {
        List<WSSak> saker = asList(
                MockCreationUtil.createWSSak().withSakstema(new WSSakstemaer().withValue("FEI")).withBehandlingskjede( // Ulovlig
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                MockCreationUtil.createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede(
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                MockCreationUtil.createWSSak().withSakstema(new WSSakstemaer().withValue("SAK")).withBehandlingskjede( // Ulovlig
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                ),
                MockCreationUtil.createWSSak().withSakstema(new WSSakstemaer().withValue("SAP")).withBehandlingskjede( // Ulovlig
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
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
                MockCreationUtil.createWSSak().withSakstema(new WSSakstemaer().withValue("DAG")).withBehandlingskjede(
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0001")), // Ulovlig
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                                .withSisteBehandlingREF("lovlig")
                ),
                MockCreationUtil.createWSSak().withSakstema(new WSSakstemaer().withValue("HJL")).withBehandlingskjede(
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0034"))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet")),
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0047"))
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))
                ),
                MockCreationUtil.createWSSak().withSakstema(new WSSakstemaer().withValue("FEI")).withBehandlingskjede(                      // Ulovlig
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0034")).withSisteBehandlingREF("lovlig"),
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0047")).withSisteBehandlingREF("lovlig")
                ),
                MockCreationUtil.createWSSak().withSakstema(new WSSakstemaer().withValue("AAP")).withBehandlingskjede(
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET)).withSlutt(new DateTime()).withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0001"))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet")), // Ulovlig, men avsluttet kvittering
                        MockCreationUtil.createWSBehandlingskjede().withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.AVSLUTTET)).withSlutt(new DateTime()).withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0002"))
                                .withSisteBehandlingREF("lovlig")
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))// Ulovlig, men avsluttet kvittering
                )
        );

        List<WSSak> filtrerteSaker = filter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(3));
    }

    @Test
    public void filtrerBehandlingerUlovligPrefix() {
        List<Behandling> behandling = filter.filtrerBehandlinger(ulovligPrefix());
        assertThat(behandling.size(), is(0));
    }

    @Test
    public void filtrerBehandlingerUlovligBehandlingsstatus() {
        List<Behandling> behandling = filter.filtrerBehandlinger(ulovligBehandlingsstatus());
        assertThat(behandling.size(), is(0));
    }

    private List<Behandling> lovligBehandling() {
        return asList(
                new Behandling()
                        .withBehandlingKvittering(BehandlingsType.KVITTERING)
                        .withBehandlingsType(FilterUtils.SEND_SOKNAD_KVITTERINGSTYPE)
                        .withBehandlingStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                        .withPrefix("11")

        );
    }

    private List<Behandling> ulovligPrefix() {
        return asList(
                new Behandling()
                        .withBehandlingsType(FilterUtils.SEND_SOKNAD_KVITTERINGSTYPE)
                        .withBehandlingStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                        .withPrefix("17")

        );
    }

    private List<Behandling> ulovligBehandlingsstatus() {
        return asList(
                new Behandling()
                        .withBehandlingsType(FilterUtils.SEND_SOKNAD_KVITTERINGSTYPE)
                        .withBehandlingStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                        .withPrefix("11")

        );
    }

}
