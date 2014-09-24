package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakRequest;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakResponse;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class GsakRutingPortTypeMock {
    @Bean
    public static Ruting createRutingPortTypeMock() {
        Ruting ruting = mock(Ruting.class);
        final List<WSFinnAnsvarligEnhetForSakResponse> responser = Arrays.asList(
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("1111").withEnhetNavn("Enhet 1"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("2222").withEnhetNavn("Enhet 2"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("3333").withEnhetNavn("Enhet 3"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4444").withEnhetNavn("Enhet 4"));

        when(ruting.finnAnsvarligEnhetForSak(any(WSFinnAnsvarligEnhetForSakRequest.class))).thenAnswer(new Answer<WSFinnAnsvarligEnhetForSakResponse>() {
            @Override
            public WSFinnAnsvarligEnhetForSakResponse answer(InvocationOnMock invocation) throws Throwable {
                return responser.get(new Random().nextInt(responser.size()));
            }
        });
        return ruting;
//        return new Ruting() {
//            @Override
//            public WSFinnAnsvarligEnhetForSakResponse finnAnsvarligEnhetForSak(WSFinnAnsvarligEnhetForSakRequest request) {
//                List<WSFinnAnsvarligEnhetForSakResponse> responser = Arrays.asList(
//                        new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("1111").withEnhetNavn("Enhet 1"),
//                        new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("2222").withEnhetNavn("Enhet 2"),
//                        new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("3333").withEnhetNavn("Enhet 3"),
//                        new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4444").withEnhetNavn("Enhet 4"));
//
//                return responser.get(new Random().nextInt(responser.size()));
//            }
//        };
    }

}
