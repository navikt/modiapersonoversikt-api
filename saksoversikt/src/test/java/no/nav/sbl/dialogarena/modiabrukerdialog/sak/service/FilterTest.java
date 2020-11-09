package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.BehandlingskjedeBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.SakBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock.MockCreationUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.BehandlingsStatus;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.BehandlingsType;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter.Filter;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter.FilterUtils;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
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
        List<Sak> saker = asList(
                lagSak("FEI"),
                lagSak("AAP"),
                lagSak("SAK"),
                lagSak("SAP")
        );

        List<Sak> filtrerteSaker = filter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(1));
        assertThat(filtrerteSaker.get(0).getSakstema().getValue(), is("AAP"));
    }

    private Sak lagSak(String sakstema) throws Exception {
        Sak sak = MockCreationUtil.createWSSak();
        Sakstemaer sakstemaer = new Sakstemaer();
        sakstemaer.setValue(sakstema);

        Behandlingskjede behandlingskjede = MockCreationUtil.createWSBehandlingskjede();
        Behandlingsstatuser behandlingstatus = new Behandlingsstatuser();
        behandlingstatus.setValue(FilterUtils.AVSLUTTET);
        behandlingskjede.setSisteBehandlingsstatus(behandlingstatus);
        behandlingskjede.setSisteBehandlingREF("lovlig");
        Behandlingstyper behandlingstype = new Behandlingstyper();
        behandlingstype.setValue("ae0014");
        behandlingskjede.setSisteBehandlingstype(behandlingstype);

        sak.getBehandlingskjede().add(behandlingskjede);
        sak.setSakstema(sakstemaer);

        return sak;
    }

    @Test
    public void sakMedFlereUlovligeBehandlinger_slipperIkkeGjennomFilter() {
        DateTime tolvteDesember2014 = new DateTime().withYear(2014).withMonthOfYear(12).withDayOfMonth(9);

        List<Sak> saker = asList(
                SakBuilder.create()
                        .withSakstema("FOS")
                        .withSaksId("000379625")
                        .withOpprettet(tolvteDesember2014)
                        .withBehandlingskjede(
                                BehandlingskjedeBuilder.create()
                                        .withBehandlingskjedeId("000WNMM")
                                        .withBehandlingskjedetype("ad0003")
                                        .withBehandlingstema("ab0142")
                                        .withStart(tolvteDesember2014)
                                        .withSlutt(tolvteDesember2014.plus(1))
                                        .withSisteBehandlingREF("171000L9S")
                                        .withSisteBehandlingstype("ae0034")
                                        .withBehandlingsListeRef("171000L9S")
                                        .withSisteBehandlingsoppdatering(tolvteDesember2014)
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingAvslutningsstatus("opphoert")
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withBehandlingskjedeId("000WQ5U")
                                        .withBehandlingskjedetype("ad0003")
                                        .withBehandlingstema("ab0142")
                                        .withStart(tolvteDesember2014.plus(2))
                                        .withSlutt(tolvteDesember2014.plus(3))
                                        .withSisteBehandlingREF("161000UVV")
                                        .withSisteBehandlingstype("ae0028")
                                        .withBehandlingsListeRef("WSBehandlingstyper")
                                        .withSisteBehandlingsoppdatering(tolvteDesember2014.plus(2))
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingAvslutningsstatus("opphoert")
                                        .build()
                        )
                        .build()

        );

        List<Sak> filtrerteSaker = filter.filtrerSaker(saker);

        assertThat(filtrerteSaker.size(), is(0));
    }

    @Test
    public void sjekkerAtEldreEnn1MndgamleBehandlingstyperUtelukkes_vedFiltrering_avSaker() throws Exception {
        DateTime dagensDato = new DateTime().now();
        DateTime toMndSidan = new DateTime().now().minusMonths(2);
        List<Sak> saker = asList(
                SakBuilder.create()
                        .withSakstema("DAG")
                        .withBehandlingskjede(
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingstype("ae0001")
                                        .build(), // Ulovlig
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSisteBehandlingstype("ae0014")
                                        .withSisteBehandlingREF("lovlig")
                                        .build()
                        ).build(),
                SakBuilder.create()
                        .withSakstema("HJL")
                        .withBehandlingskjede(
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSisteBehandlingstype("ae0034")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingstype("ae0047")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .build()
                        ).build(),
                SakBuilder.create()
                        .withSakstema("FEI")
                        .withBehandlingskjede(                      // Ulovlig
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSisteBehandlingstype("ae0034").withSisteBehandlingREF("lovlig")
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSisteBehandlingstype("ae0047").withSisteBehandlingREF("lovlig")
                                        .build()
                        ).build(),
                SakBuilder.create()
                        .withSakstema("AAP")
                        .withBehandlingskjede(
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSlutt(new DateTime())
                                        .withSisteBehandlingstype("ae0001")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .build(), // Ulovlig, men avsluttet kvittering
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSlutt(new DateTime())
                                        .withSisteBehandlingstype("ae0002")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")// Ulovlig, men avsluttet kvittering
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .build()
                        )
                        .build(),
                SakBuilder.create()
                        .withSakstema("SYM")
                        .withBehandlingskjede(
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSlutt(new DateTime())
                                        .withSisteBehandlingstype("ae0001")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingsoppdatering(toMndSidan)
                                        .build(), // Ulovlig, men avsluttet kvittering
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                                        .withSlutt(new DateTime())
                                        .withSisteBehandlingstype("ae0002")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")// Ulovlig, men avsluttet kvittering
                                        .withSisteBehandlingsoppdatering(toMndSidan)
                                        .build()
                        )
                        .build()
        );

        List<Sak> filtrerteSaker = filter.filtrerSaker(saker);
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