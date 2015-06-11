package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1;
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakSakEksistererAllerede;
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakUgyldigInput;
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest;
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsakOpprettSakEndpointMock {
    @Bean
    public static BehandleSakV1 createGsakOpprettSakPortTypeMock() {
        return new BehandleSakV1() {
            @Override
            public void ping() {

            }

            @Override
            public WSOpprettSakResponse opprettSak(WSOpprettSakRequest wsOpprettSakRequest) throws OpprettSakUgyldigInput, OpprettSakSakEksistererAllerede {
                return new WSOpprettSakResponse().withSakId("-1");
            }
        };
    }
}
