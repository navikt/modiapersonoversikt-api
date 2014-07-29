package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSSakstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock.KVITTERING1;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock.KVITTERING2;
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
    public static final String AAP_BEHANDLINGSTEMA = "aX000X";

    public static final String ANTALLSAKER_PROPERTY = "sakogbehandling.antallmocksaker";

    @Bean
    public SakOgBehandlingPortType getSakOgBehandlingPortTypeMock() {
        SakOgBehandlingPortType mock = mock(SakOgBehandlingPortType.class);
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
                omsSak(),
                hjeSak(),
                gruSak(),
                konSak(),
                sykSak()
        ).subList(0, antallSaker);

        return new FinnSakOgBehandlingskjedeListeResponse().withSak(liste);
    }

    public static WSSak dagpengerSak() {
        return new WSSak()
                .withSaksId("1")
                .withSakstema(new WSSakstemaer().withValue(DAGPENGEARKIVTEMA))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, DAGPENGER_BEHANDLINGSTEMA),
                        createBehandlingKobletTilKvittering(KVITTERING1, DAGPENGER_BEHANDLINGSTEMA)
                );
    }

    public static WSSak aapSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue(AAPARKIVTEMA))
                .withBehandlingskjede(
                        createBehandlingKobletTilKvittering(KVITTERING2, AAP_BEHANDLINGSTEMA),
                        createAvsluttetSoknadKjede(GENERISK_BEHANDLINGSID, AAP_BEHANDLINGSTEMA)
                );
    }

    public static WSSak omsSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("OMS"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, "OMS")
                );
    }

    public static WSSak hjeSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("HJE"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, "HJE")
                );
    }

    public static WSSak gruSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("GRU"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, "GRU")
                );
    }

    public static WSSak konSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("KON"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, "KON")
                );
    }

    public static WSSak sykSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue("SYK"))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, "SYK")
                );
    }

    private static WSBehandlingskjede createOpprettetSoknadKjede(String sisteBehandlingsREF, String behandlingstema) {
        return new WSBehandlingskjede()
                .withBehandlingskjedeId("motta" + now())
                .withSisteBehandlingREF(sisteBehandlingsREF)
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue(behandlingstema))
                .withStart(now().minusDays(3));

    }

    private static WSBehandlingskjede createAvsluttetSoknadKjede(String sisteBehandlingREF, String behandlingstema) {
        return new WSBehandlingskjede()
                .withBehandlingskjedeId("behandlingskjedeid" + now())
                .withSisteBehandlingREF(sisteBehandlingREF)
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue(behandlingstema))
                .withStart(now().minusDays(3).minusHours(5))
                .withSlutt(now());
    }


    private static WSBehandlingskjede createBehandlingKobletTilKvittering(String behandlingsListeRef, String behandlingstema) {
        return new WSBehandlingskjede()
                .withBehandlingsListeRef(behandlingsListeRef) // Kobler behandling i henvendelse til behandlingskjeden
                .withSisteBehandlingREF(behandlingsListeRef)
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue(behandlingstema))
                .withBehandlingskjedeId("behandle" + now())
                .withStart(now().minusDays(3).minusHours(2));
    }

}
