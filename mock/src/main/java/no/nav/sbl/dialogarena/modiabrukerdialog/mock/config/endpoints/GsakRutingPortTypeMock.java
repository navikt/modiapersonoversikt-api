package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSEnhet;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeRequest;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeResponse;
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
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("0122").withEnhetNavn("NAV Trøgstad"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("0100").withEnhetNavn("NAV Østfold"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("2960").withEnhetNavn("Nav Drift og Utvikling - Anskaffelse og økonomi"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4303").withEnhetNavn("NAV Id og fordeling"));

        when(ruting.finnAnsvarligEnhetForSak(any(WSFinnAnsvarligEnhetForSakRequest.class))).thenAnswer(new Answer<WSFinnAnsvarligEnhetForSakResponse>() {
            @Override
            public WSFinnAnsvarligEnhetForSakResponse answer(InvocationOnMock invocation) {
                return responser.get(new Random().nextInt(responser.size()));
            }
        });
        when(ruting.finnAnsvarligEnhetForOppgavetype(any(WSFinnAnsvarligEnhetForOppgavetypeRequest.class))).thenAnswer(new Answer<WSFinnAnsvarligEnhetForOppgavetypeResponse>() {
            @Override
            public WSFinnAnsvarligEnhetForOppgavetypeResponse answer(InvocationOnMock invocation) {
                WSFinnAnsvarligEnhetForSakResponse enhet = responser.get(new Random().nextInt(responser.size()));
                WSFinnAnsvarligEnhetForOppgavetypeResponse response = new WSFinnAnsvarligEnhetForOppgavetypeResponse();
                response.getEnhetListe().add(new WSEnhet().withEnhetId(enhet.getEnhetId()).withEnhetNavn(enhet.getEnhetNavn()));
                return response;
            }
        });
        return ruting;
    }

}
