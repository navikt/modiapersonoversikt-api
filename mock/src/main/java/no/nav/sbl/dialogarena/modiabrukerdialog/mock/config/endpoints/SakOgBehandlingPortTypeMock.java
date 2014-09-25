package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSSakstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.lang.Math.min;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock.KVITTERING1;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock.KVITTERING2;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock.KVITTERINGETTERSENDELSE1;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock.KVITTERINGETTERSENDELSE2;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class SakOgBehandlingPortTypeMock {

    public static final String GENERISK_BEHANDLINGSID = "behandlingsid123";
    public static final String DAGPENGEARKIVTEMA = "DAG";
    public static final String AAPARKIVTEMA = "AAP";
    public static final String DAGPENGER_BEHANDLINGSTEMA = "ab0001";
    public static final String AAP_BEHANDLINGSTEMA = "ab0100";
    public static final String OMS_BEHANDLINGSTEMA = "ab0149";
    public static final String GRU_BEHANDLINGSTEMA = "ab0132";
    public static final String KON_BEHANDLINGSTEMA = "ab0084";
    public static final String SYK_BEHANDLINGSTEMA = "ab0061";
    public static final String HJE_BEHANDLINGSTEMA = "ab0116";

    public static final String ANTALLSAKER_PROPERTY = "sakogbehandling.antallmocksaker";


    @Bean
    public SakOgBehandling_v1PortType getSakOgBehandlingPortTypeMock() {
        SakOgBehandling_v1PortType mock = mock(SakOgBehandling_v1PortType.class);

        // Bruker thenAnswer slik at antall saker (ANTALLSAKER_PROPERTY) kan justeres under kjøring fra mocksetup
        when(mock.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return finnSakOgBehandlingskjedeListe();
            }
        });

        try {
            RuntimeException notImplemented = new RuntimeException("Denne tjenesten er ikke implementert i mock (eller i S&B prod per dags dato)");
            when(mock.hentBehandlingskjedensBehandlinger(any(HentBehandlingskjedensBehandlingerRequest.class))).thenThrow(notImplemented);
            when(mock.hentBehandling(any(HentBehandlingRequest.class))).thenThrow(notImplemented);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return mock;
    }

    public static FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe() {
        Integer antallSaker = Integer.valueOf(getProperty(ANTALLSAKER_PROPERTY, "100000"));

        List<WSSak> liste = asList(
                dagpengerSak(),
                aapSak(),
                feilutbetalingSak(),
                omsSak(),
                hjeSak(),
                gruSak(),
                konSak(),
                sykSak(),
                klaSak()
        );
        
        return new FinnSakOgBehandlingskjedeListeResponse().withSak(liste.subList(0, min(liste.size(), antallSaker)));
    }

    public static WSSak dagpengerSak() {
        return new WSSak()
                .withSaksId("1")
                .withSakstema(new WSSakstemaer().withValue(DAGPENGEARKIVTEMA))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, DAGPENGER_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(2)),
                        createBehandlingKobletTilKvittering(KVITTERING2, DAGPENGER_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(6)),
                        createBehandlingKobletTilKvittering(KVITTERINGETTERSENDELSE2, DAGPENGER_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(22))
                );
    }

    public static WSSak aapSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue(AAPARKIVTEMA))
                .withBehandlingskjede(
                        createBehandlingKobletTilKvittering(KVITTERING1, AAP_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(10)),
                        createBehandlingKobletTilKvittering(KVITTERINGETTERSENDELSE1, AAP_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(15)),
                        createAvsluttetSoknadKjede(GENERISK_BEHANDLINGSID, AAP_BEHANDLINGSTEMA).withSlutt(now().minusYears(1)),
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, AAP_BEHANDLINGSTEMA).withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae00XX")) // Skal filtreres bort
                );
    }

    public static WSSak feilutbetalingSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("FEI"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, AAP_BEHANDLINGSTEMA)
                );
    }

    public static WSSak omsSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("OMS"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, OMS_BEHANDLINGSTEMA)
                );
    }

    public static WSSak klaSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("KLA"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, AAP_BEHANDLINGSTEMA).withStart(now().minusYears(1))
                );
    }

    public static WSSak hjeSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("HJE"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, HJE_BEHANDLINGSTEMA).withStart(now().minusYears(1))
                );
    }

    public static WSSak gruSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("GRU"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, GRU_BEHANDLINGSTEMA)
                );
    }

    public static WSSak konSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("KON"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, KON_BEHANDLINGSTEMA)
                );
    }

    public static WSSak sykSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("SYK"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, SYK_BEHANDLINGSTEMA)
                );
    }

    private static WSBehandlingskjede createOpprettetSoknadKjede(String sisteBehandlingsREF, String behandlingstema) {
        return new WSBehandlingskjede()
                .withBehandlingskjedeId("motta" + now())
                .withSisteBehandlingREF(sisteBehandlingsREF)
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue(behandlingstema))
                .withBehandlingstema(new WSBehandlingstemaer().withValue(behandlingstema))
                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                .withStart(now().minusDays(5));

    }

    private static WSBehandlingskjede createAvsluttetSoknadKjede(String sisteBehandlingREF, String behandlingstema) {
        return new WSBehandlingskjede()
                .withBehandlingskjedeId("behandlingskjedeid" + now())
                .withSisteBehandlingREF(sisteBehandlingREF)
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue(behandlingstema))
                .withBehandlingstema(new WSBehandlingstemaer().withValue(behandlingstema))
                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                .withStart(now().minusDays(3).minusHours(5))
                .withSlutt(now());
    }

    private static WSBehandlingskjede createBehandlingKobletTilKvittering(String behandlingsListeRef, String behandlingstema) {
        return new WSBehandlingskjede()
                .withBehandlingsListeRef(behandlingsListeRef) // Kobler behandling i henvendelse til behandlingskjeden
                .withSisteBehandlingREF(behandlingsListeRef)
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue(behandlingstema))
                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                .withBehandlingstema(new WSBehandlingstemaer().withValue(behandlingstema))
                .withBehandlingskjedeId("behandle" + now())
                .withStart(now().minusDays(3).minusHours(2))
                .withSlutt(now().minusDays(3).minusHours(1));
    }

}
