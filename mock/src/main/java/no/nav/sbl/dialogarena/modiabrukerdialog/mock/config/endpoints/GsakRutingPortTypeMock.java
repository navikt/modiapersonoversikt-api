package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakRequest;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakResponse;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
public class GsakRutingPortTypeMock {
    @Bean
    public static Ruting createRutingPortTypeMock() {
        return new Ruting() {
            @Override
            public WSFinnAnsvarligEnhetForSakResponse finnAnsvarligEnhetForSak(WSFinnAnsvarligEnhetForSakRequest request) {
                List<WSFinnAnsvarligEnhetForSakResponse> responser = Arrays.asList(
                        new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("1111").withEnhetNavn("Enhet 1"),
                        new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("2222").withEnhetNavn("Enhet 2"),
                        new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("3333").withEnhetNavn("Enhet 3"),
                        new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4444").withEnhetNavn("Enhet 4"));

                return responser.get(new Random().nextInt(responser.size()));
            }
        };
    }

}
