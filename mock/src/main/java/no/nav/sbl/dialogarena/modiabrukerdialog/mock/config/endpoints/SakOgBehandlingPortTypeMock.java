package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingHentBehandlingBehandlingIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigInteger.TEN;
import static java.math.BigInteger.valueOf;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED;
import static org.joda.time.DateTime.now;

@Configuration
public class SakOgBehandlingPortTypeMock {

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return new SakOgBehandlingPortType() {

            @Override
            public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest finnSakOgBehandlingskjedeListeRequest) {
                List<WSBehandlingskjede> behandlingskjeder = new ArrayList<>();
                behandlingskjeder.addAll(asList(
                        createMottattSoknad(),
                        createMottattSoknadUtenBehandlingsTid(),
                        createUnderBehandlingSoknad(),
                        createNyligFerdigSoknad(),
                        createGammelFerdigSoknad()
                ));
                //                throw new ApplicationException("Feil i sakogBehandling");
                return new FinnSakOgBehandlingskjedeListeResponse().withSak(asList(new WSSak().withBehandlingskjede(behandlingskjeder)));
            }

            private WSBehandlingskjede createMottattSoknad() {
                return new WSBehandlingskjede()
                        .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("Dagpenger"))
                        .withStart(new DateTime(2013, 1, 1, 11, 11))
                        .withNormertBehandlingstid(new WSBehandlingstid().withTid(TEN).withType(new WSBehandlingstidtyper().withValue("dager")));
            }

            private WSBehandlingskjede createMottattSoknadUtenBehandlingsTid() {
                return new WSBehandlingskjede()
                        .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("Dagpenger uten behandlingstid"))
                        .withStart(now());
            }

            private WSBehandlingskjede createUnderBehandlingSoknad() {
                return new WSBehandlingskjede()
                        .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("Uføre"))
                        .withStart(now().minusDays(5))
                        .withStartNAVtid(now().minusDays(2))
                        .withNormertBehandlingstid(new WSBehandlingstid().withTid(valueOf(200)).withType(new WSBehandlingstidtyper().withValue("dager")));
            }

            private WSBehandlingskjede createNyligFerdigSoknad() {
                return new WSBehandlingskjede()
                        .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("Sykepenger"))
                        .withStart(new DateTime(2013, 8, 19, 11, 11))
                        .withStartNAVtid(new DateTime(2013, 9, 10, 11, 12))
                        .withSluttNAVtid(now().minusDays(AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED - 1))
                        .withNormertBehandlingstid(new WSBehandlingstid().withTid(TEN).withType(new WSBehandlingstidtyper().withValue("dager")));
            }

            private WSBehandlingskjede createGammelFerdigSoknad() {
                return new WSBehandlingskjede()
                        .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("Dagpenger"))
                        .withStart(new DateTime(2013, 1, 1, 11, 11))
                        .withStartNAVtid(new DateTime(2013, 2, 2, 11, 12))
                        .withSluttNAVtid(now().minusDays(AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED + 1))
                        .withNormertBehandlingstid(new WSBehandlingstid().withTid(valueOf(14)).withType(new WSBehandlingstidtyper().withValue("dager")));
            }

            @Override
            public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest) throws HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) throws HentBehandlingHentBehandlingBehandlingIkkeFunnet {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void ping() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

}
