package no.nav.modiapersonoversikt.service.sakogbehandling;

import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SakOgBehandlingFilterTest {
    @Test
    public void sjekkerLovligeTema_vedFiltrering_avSaker() throws Exception {
        List<Sak> saker = List.of(
                lagSak("FEI"),
                lagSak("SAK"),
                lagSak("SAP")
        );

        List<Sak> filtrerteSaker = SakOgBehandlingFilter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(0));
    }

    @Test
    public void sakMedKunUlovligeBehandlinger_slipperIkkeGjennomFilter() {
        DateTime tolvteDesember2014 = new DateTime().withYear(2014).withMonthOfYear(12).withDayOfMonth(9);

        List<Sak> saker = List.of(
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

        List<Sak> filtrerteSaker = SakOgBehandlingFilter.filtrerSaker(saker);

        assertThat(filtrerteSaker.size(), is(0));
    }

    @Test
    public void sjekkerAtEldreEnn1MndgamleBehandlingstyperUtelukkes_vedFiltrering_avSaker() {
        DateTime dagensDato = DateTime.now();
        List<Sak> saker = List.of(
                SakBuilder.create()
                        .withSakstema("DAG")
                        .withBehandlingskjede(
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingstype("ae0001")
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .build(), // Ulovlig
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSisteBehandlingstype("ae0014")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .build()
                        ).build(),
                SakBuilder.create()
                        .withSakstema("HJL")
                        .withBehandlingskjede(
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSisteBehandlingstype("ae0034")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .withSlutt(dagensDato)
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingstype("ae0047")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .withSlutt(dagensDato)
                                        .build()
                        ).build(),
                SakBuilder.create()
                        .withSakstema("FEI")
                        .withBehandlingskjede(                      // Ulovlig
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSisteBehandlingstype("ae0039").withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSisteBehandlingstype("ae0047").withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .build()
                        ).build(),
                SakBuilder.create()
                        .withSakstema("AAP")
                        .withBehandlingskjede(
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSlutt(new DateTime())
                                        .withSisteBehandlingstype("ae0001")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .build(), // Ulovlig, men avsluttet kvittering
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSlutt(new DateTime())
                                        .withSisteBehandlingstype("ae0002")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsstatus("avsluttet")// Ulovlig, men avsluttet kvittering
                                        .withSisteBehandlingsoppdatering(dagensDato)
                                        .build()
                        )
                        .build()

        );

        List<Sak> filtrerteSaker = SakOgBehandlingFilter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(3));


    }

    @Test
    public void filtererGamleFerdistilteBehandlingsKjeder() {
        DateTime toMndSidan = DateTime.now().minusMonths(2);
        List<Sak> saker = List.of(
                SakBuilder.create()
                        .withSakstema("HJL")
                        .withBehandlingskjede(
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSisteBehandlingstype("ae0034")
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsoppdatering(toMndSidan)
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSlutt(toMndSidan)
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withSisteBehandlingsstatus(SakOgBehandlingFilter.AVSLUTTET)
                                        .withSisteBehandlingREF("lovlig")
                                        .withSisteBehandlingstype("ae0047")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingsoppdatering(toMndSidan)
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSlutt(toMndSidan)
                                        .build()
                        ).build()

        );
        List<Sak> filtrerteSaker = SakOgBehandlingFilter.filtrerSaker(saker);
        assertThat(filtrerteSaker.size(), is(0));
    }

    @Test
    public void filtereGamleForeldrePengerMedAnnaStatusEnnOK() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        List<Sak> saker = List.of(
                SakBuilder.create()
                        .withSakstema("FOR")
                        .withBehandlingskjede(

                                BehandlingskjedeBuilder.create()
                                        .withBehandlingskjedetype("ad0003")
                                        .withBehandlingstema("ab0260")
                                        .withStart(DateTime.parse("2015-04-17T12:00:00.000+02:00", formatter))
                                        .withSlutt(DateTime.parse("2015-04-18T00:12:58.526+02:00", formatter))
                                        .withSisteBehandlingstype("ae0028")
                                        .withSisteBehandlingsoppdatering(DateTime.parse("2015-04-17T12:00:00.000+02:00", formatter))
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingAvslutningsstatus("mangl-aktiv")
                                        .withSisteBehandlingREF("171017SU0") //forsvinner grunnet prefix-17
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withBehandlingskjedetype("ukjent")
                                        //.withBehandlingstema("ab0260")
                                        .withStart(DateTime.parse("2019-09-13T09:21:10.766+02:00", formatter))
                                        .withSlutt(DateTime.parse("2019-09-13T09:21:11.493+02:00", formatter))
                                        .withSisteBehandlingstype("ae0103")
                                        .withSisteBehandlingsoppdatering(DateTime.parse("2019-09-13T09:21:10.767+02:00", formatter)) //forsvinner grunnet alder
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingREF("1000PKZR2")
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withBehandlingskjedetype("ad0003")
                                        .withBehandlingstema("ab0047")
                                        .withStart(DateTime.parse("2020-06-12T10:40:29.000+02:00", formatter))
                                        .withSlutt(DateTime.parse("2020-06-17T08:31:40.143+02:00", formatter))
                                        .withSisteBehandlingstype("ae0028")
                                        .withSisteBehandlingsoppdatering(DateTime.parse("2019-04-24T12:00:00.000+02:00", formatter)) //forsviner grunnet alder
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingREF("FS36_1495156")
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withBehandlingskjedetype("ad0003")
                                        .withBehandlingstema("ab0126")
                                        .withStart(DateTime.parse("2019-04-24T12:00:00.000+02:00", formatter))
                                        .withSisteBehandlingstype("ae0034")
                                        .withSisteBehandlingsoppdatering(DateTime.parse("2020-06-17T08:31:38.000+02:00", formatter))
                                        .withSisteBehandlingsstatus("opprettet") // blir med i resultat grunnet ulik status enn avsluttet
                                        .withSisteBehandlingREF("16520E10o")
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withBehandlingskjedetype("ad0003")
                                        .withBehandlingstema("ab0326")
                                        .withStart(DateTime.parse("2019-09-11T00:00:00.000+02:00", formatter))
                                        .withSlutt(DateTime.parse("2019-09-13T09:21:17.194+02:00", formatter))
                                        .withSisteBehandlingstype("ae0034")
                                        .withSisteBehandlingsoppdatering(DateTime.parse("2019-09-13T00:00:00.000+02:00", formatter))
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingREF("FS36_1273752")
                                        .build(),
                                BehandlingskjedeBuilder.create()
                                        .withBehandlingskjedetype("ad0003")
                                        .withBehandlingstema("ab0326")
                                        .withStart(DateTime.now())
                                        .withSlutt(DateTime.now())
                                        .withSisteBehandlingstype("ae0034")
                                        .withSisteBehandlingsoppdatering(DateTime.now())
                                        .withSisteBehandlingsstatus("avsluttet")
                                        .withSisteBehandlingAvslutningsstatus("ok")
                                        .withSisteBehandlingREF("FS36_1273753")
                                        .build()

                        ).build()


        );

        List<Behandlingskjede> filtrerteBehandlinger = SakOgBehandlingFilter.filtrerBehandlinger(saker.get(0).getBehandlingskjede());
        assertThat(filtrerteBehandlinger.size(), is(2));

    }

    @Test
    public void filtrerBehandlingerUlovligPrefix() {
        List<Behandlingskjede> behandling = SakOgBehandlingFilter.filtrerBehandlinger(ulovligPrefix());
        assertThat(behandling.size(), is(0));
    }

    @Test
    public void filtrerBehandlingerUlovligBehandlingsstatus() {
        List<Behandlingskjede> behandling = SakOgBehandlingFilter.filtrerBehandlinger(ulovligBehandlingsstatus());
        assertThat(behandling.size(), is(0));
    }

    @Test
    public void mapperBehandlingskjedestatusRiktig() {
        var opprettet = SakOgBehandlingFilter.behandlingsstatus(lagBehandlingskjede(SakOgBehandlingFilter.OPPRETTET));
        var avsluttet = SakOgBehandlingFilter.behandlingsstatus(lagBehandlingskjede(SakOgBehandlingFilter.AVSLUTTET));
        var avbrutt = SakOgBehandlingFilter.behandlingsstatus(lagBehandlingskjede(SakOgBehandlingFilter.AVBRUTT));

        assertThat(opprettet, is(BehandlingsStatus.UNDER_BEHANDLING));
        assertThat(avsluttet, is(BehandlingsStatus.FERDIG_BEHANDLET));
        assertThat(avbrutt, is(BehandlingsStatus.AVBRUTT));
    }

    @Test(expected = IllegalStateException.class)
    public void skalKasteFeilVedUkjentBehandlingsstatus() {
        SakOgBehandlingFilter.behandlingsstatus(lagBehandlingskjede("ukjent"));
    }

    private Sak lagSak(String sakstema) throws Exception {
        Sak sak = MockCreationUtil.createWSSak();
        Sakstemaer sakstemaer = new Sakstemaer();
        sakstemaer.setValue(sakstema);

        Behandlingskjede behandlingskjede = lagBehandlingskjede(
                SakOgBehandlingFilter.AVSLUTTET,
                "ae0014",
                "lovlig"
        );

        sak.getBehandlingskjede().add(behandlingskjede);
        sak.setSakstema(sakstemaer);

        return sak;
    }

    private List<Behandlingskjede> ulovligPrefix() {
        return List.of(
                lagBehandlingskjede(
                        SakOgBehandlingFilter.AVSLUTTET,
                        SakOgBehandlingFilter.SEND_SOKNAD_KVITTERINGSTYPE,
                        "17"
                )
        );
    }

    private List<Behandlingskjede> ulovligBehandlingsstatus() {
        return List.of(
                lagBehandlingskjede(
                        SakOgBehandlingFilter.AVSLUTTET,
                        SakOgBehandlingFilter.SEND_SOKNAD_KVITTERINGSTYPE,
                        "11"
                )
        );
    }

    private Behandlingskjede lagBehandlingskjede(String statusValue) {
        return lagBehandlingskjede(statusValue, "", "");
    }
    private Behandlingskjede lagBehandlingskjede(
            String statusValue,
            String typeValue,
            String behandlingRef
    ) {
        var kjede = new Behandlingskjede();
        var type = new Behandlingskjedetyper();
        type.setValue(typeValue);
        kjede.setBehandlingskjedetype(type);

        var status = new Behandlingsstatuser();
        status.setValue(statusValue);
        kjede.setSisteBehandlingsstatus(status);

        kjede.setSisteBehandlingREF(behandlingRef);

        return kjede;
    }
}
