package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSSakstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class SakOgBehandlingPortTypeMock {

    public static final String DAGPENGER_SKJEMAKODE_NAV_04_01_03 = "NAV 04-01.03";
    public static final String AAP_SKJEMAKODE_NAV_11_13_05 = "NAV 11-13.05";
    public static final String GENERISK_BEHANDLINGSID = "behandlingsid123";
    public static final String DAGPENGER_BEHANDLINGSID = "behandlingsidX";
    public static final String AAP_BEHANDLINGSID = "xxxx-mockbehandlingid2";
    public static final String DAGPENGER_ETTERSENDELSE_BEHANDLINGSID = "ettersendingbehandlingsid2";
    public static final String AAP_ETTERSENDELSE_BEHANDLINGSID = "ettersendingbehandlingsid1";
    public static final String DAGPENGEARKIVTEMA = "DAG";
    public static final String AAPARKIVTEMA = "AAP";
    public static final String DAGPENGER_BEHANDLINGSTEMA = "ab0001";
    public static final String AAP_BEHANDLINGSTEMA = "aX000X";

    @Bean
    public SakOgBehandlingPortType getSakOgBehandlingPortTypeMock() {
        SakOgBehandlingPortType mock = mock(SakOgBehandlingPortType.class);
        when(mock.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenReturn(finnSakOgBehandlingskjedeListe());
        return mock;
    }

    public static FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe() {
        return new FinnSakOgBehandlingskjedeListeResponse()
                .withSak(
                        dagpengerSak(),
                        aapSak(),
                        omsSak(),
                        hjeSak(),
                        gruSak(),
                        konSak(),
                        sykSak()
                );
    }

    public static WSSak dagpengerSak() {
        return new WSSak()
                .withSaksId("1")
                .withSakstema(new WSSakstemaer().withValue(DAGPENGEARKIVTEMA))
                .withBehandlingskjede(
                        createOpprettetSoknadKjede(GENERISK_BEHANDLINGSID, DAGPENGER_BEHANDLINGSTEMA),
                        createBehandlingKobletTilKvittering(DAGPENGER_BEHANDLINGSID, DAGPENGER_BEHANDLINGSTEMA),
                        createBehandlingKobletTilKvittering(DAGPENGER_ETTERSENDELSE_BEHANDLINGSID, DAGPENGER_BEHANDLINGSTEMA)
                );
    }

    public static WSSak aapSak() {
        return new WSSak()
                .withSaksId("2")
                .withSakstema(new WSSakstemaer().withValue(AAPARKIVTEMA))
                .withBehandlingskjede(
                        createBehandlingKobletTilKvittering(AAP_BEHANDLINGSID, AAP_BEHANDLINGSTEMA),
                        createBehandlingKobletTilKvittering(AAP_ETTERSENDELSE_BEHANDLINGSID, AAP_BEHANDLINGSTEMA),
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
