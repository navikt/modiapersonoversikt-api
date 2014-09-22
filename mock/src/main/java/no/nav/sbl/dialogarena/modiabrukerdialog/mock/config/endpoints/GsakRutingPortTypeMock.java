package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakRequest;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakResponse;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsakRutingPortTypeMock {
    @Bean
    public static Ruting createRutingPortTypeMock() {
        return new Ruting() {
            @Override
            public WSFinnAnsvarligEnhetForSakResponse finnAnsvarligEnhetForSak(WSFinnAnsvarligEnhetForSakRequest request) {
                return new WSFinnAnsvarligEnhetForSakResponse();
            }
        };
    }

}
