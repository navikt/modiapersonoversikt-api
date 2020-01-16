package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.BehandlingskjedeBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.SakBuilder;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
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
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock.*;
import static org.joda.time.DateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class SakOgBehandlingPortTypeMock {

    public static final String GENERISK_BEHANDLINGSID = "behandlingsid123";
    public static final String DAGPENGEARKIVTEMA = "DAG";
    public static final String AAPARKIVTEMA = "AAP";
    public static final String FORELDREPENGER_ARKIV_TEMA = "FOR";
    public static final String DAGPENGER_BEHANDLINGSTEMA = "ab0001";
    public static final String AAP_BEHANDLINGSTEMA = "ab0100";
    public static final String FOR_BEHANDLINGSTEMA = "ab0026";
    public static final String OMS_BEHANDLINGSTEMA = "ab0149";
    public static final String GRU_BEHANDLINGSTEMA = "ab0132";
    public static final String KON_BEHANDLINGSTEMA = "ab0084";
    public static final String SYK_BEHANDLINGSTEMA = "ab0061";
    public static final String HJE_BEHANDLINGSTEMA = "ab0116";

    public static final String ANTALLSAKER_PROPERTY = "sakogbehandling.antallmocksaker";


    @Bean
    public SakOgBehandlingV1 getSakOgBehandlingPortTypeMock() {
        SakOgBehandlingV1 mock = mock(SakOgBehandlingV1.class);

        // Bruker thenAnswer slik at antall saker (ANTALLSAKER_PROPERTY) kan justeres under kjøring fra mocksetup
        when(mock.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
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

        List<Sak> liste = asList(
                dagpengerSak(),
                aapSak(),
                foreldrepengerSak(),
                feilutbetalingSak(),
                omsSak(),
                hjeSak(),
                gruSak(),
                konSak(),
                sykSak(),
                klaSak()
        );

        FinnSakOgBehandlingskjedeListeResponse response = new FinnSakOgBehandlingskjedeListeResponse();
        response.getSak().addAll(liste.subList(0, min(liste.size(), antallSaker)));
        return response;
    }

    public static Sak dagpengerSak() {
        return SakBuilder.create()
                .withSaksId("1")
                .withSakstema(DAGPENGEARKIVTEMA)
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, DAGPENGER_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(2)).build(),
                        createBehandlingKobletTilKvittering(KVITTERING2, DAGPENGER_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(6)).build(),
                        createBehandlingKobletTilKvittering(KVITTERINGETTERSENDELSE2, DAGPENGER_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(22)).build()
                )
                .build();
    }

    public static Sak aapSak() {
        return SakBuilder.create()
                .withSaksId("2")
                .withSakstema(AAPARKIVTEMA)
                .withBehandlingskjede(
                        createBehandlingKobletTilKvittering(KVITTERING1, AAP_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(10)).build(),
                        createBehandlingKobletTilKvittering(KVITTERINGETTERSENDELSE1, AAP_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(15)).build(),
                        createAvsluttetSoknadKjede(GENERISK_BEHANDLINGSID, AAP_BEHANDLINGSTEMA).withSlutt(now().minusYears(1)).build(),
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, AAP_BEHANDLINGSTEMA).withSisteBehandlingstype("ae00XX").build() // Skal filtreres bort
                ).build();
    }

    public static Sak foreldrepengerSak() {
        return SakBuilder.create()
                .withSaksId("3")
                .withSakstema(FORELDREPENGER_ARKIV_TEMA)
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, FOR_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(2)).build(),
                        createBehandlingKobletTilKvittering(KVITTERING3, FOR_BEHANDLINGSTEMA).withSlutt(now().minusMinutes(6)).build()
                ).build();
    }

    public static Sak feilutbetalingSak() {
        return SakBuilder.create()
                .withSaksId("2")
                .withSakstema("FEI")
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, AAP_BEHANDLINGSTEMA).build()
                ).build();
    }

    public static Sak omsSak() {
        return SakBuilder.create()
                .withSaksId("2")
                .withSakstema("OMS")
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, OMS_BEHANDLINGSTEMA).build()
                ).build();
    }

    public static Sak klaSak() {
        return SakBuilder.create()
                .withSaksId("2")
                .withSakstema("KLA")
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, AAP_BEHANDLINGSTEMA).withStart(now().minusYears(1)).build()
                ).build();
    }

    public static Sak hjeSak() {
        return SakBuilder.create()
                .withSaksId("2")
                .withSakstema("HJE")
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, HJE_BEHANDLINGSTEMA).withStart(now().minusYears(1)).build()
                ).build();
    }

    public static Sak gruSak() {
        return SakBuilder.create()
                .withSaksId("2")
                .withSakstema("GRU")
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, GRU_BEHANDLINGSTEMA).build()
                ).build();
    }

    public static Sak konSak() {
        return SakBuilder.create()
                .withSaksId("2")
                .withSakstema("KON")
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, KON_BEHANDLINGSTEMA).build()
                ).build();
    }

    public static Sak sykSak() {
        return SakBuilder.create()
                .withSaksId("2")
                .withSakstema("SYK")
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, SYK_BEHANDLINGSTEMA).build()
                ).build();
    }

    private static BehandlingskjedeBuilder createOpprettetSoknadKjede(String sisteBehandlingsREF, String behandlingstema) {
        return BehandlingskjedeBuilder.create()
                .withBehandlingskjedeId("motta" + now())
                .withSisteBehandlingREF(sisteBehandlingsREF)
                .withBehandlingskjedetype(behandlingstema)
                .withBehandlingstema(behandlingstema)
                .withSisteBehandlingstype("ae0014")
                .withSisteBehandlingsstatus("opprettet")
                .withStart(now().minusDays(5));

    }

    private static BehandlingskjedeBuilder createAvsluttetSoknadKjede(String sisteBehandlingREF, String behandlingstema) {
        return BehandlingskjedeBuilder.create()
                .withBehandlingskjedeId("behandlingskjedeid" + now())
                .withSisteBehandlingREF(sisteBehandlingREF)
                .withBehandlingskjedetype(behandlingstema)
                .withBehandlingstema(behandlingstema)
                .withSisteBehandlingstype("ae0014")
                .withSisteBehandlingsstatus("avsluttet")
                .withStart(now().minusDays(3).minusHours(5))
                .withSlutt(now());
    }

    private static BehandlingskjedeBuilder createBehandlingKobletTilKvittering(String behandlingsListeRef, String behandlingstema) {
        return BehandlingskjedeBuilder.create()
                .withBehandlingsListeRef(behandlingsListeRef) // Kobler behandling i henvendelse til behandlingskjeden
                .withSisteBehandlingREF(behandlingsListeRef)
                .withBehandlingskjedetype(behandlingstema)
                .withSisteBehandlingstype("ae0014")
                .withSisteBehandlingsstatus("avsluttet")
                .withBehandlingstema(behandlingstema)
                .withBehandlingskjedeId("behandle" + now())
                .withSisteBehandlingsstatus("avsluttet")
                .withStart(now().minusDays(3).minusHours(2))
                .withSlutt(now().minusDays(3).minusHours(1));
    }

}
