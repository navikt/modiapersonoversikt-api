package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.tjenester.ruting.meldinger.v1.*;
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
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4412").withEnhetNavn("Nav Drift og Utvikling - IKT"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4414").withEnhetNavn("Nav Drift og Utvikling - DevOps"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4415").withEnhetNavn("Nav Drift og Utvikling - Aura"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("0313").withEnhetNavn("Nav Drift og Utvikling - Team Ulv"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4416").withEnhetNavn("Nav Drift og Utvikling - Team Ørn"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("0314").withEnhetNavn("Nav Drift og Utvikling - Team Bjørn"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4417").withEnhetNavn("Nav Drift og Utvikling - Team Gaupe"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4219").withEnhetNavn("Nav Drift og Utvikling - Team Rev"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("0315").withEnhetNavn("Nav Drift og Utvikling - Team Kanary"),
                new WSFinnAnsvarligEnhetForSakResponse().withEnhetId("4418").withEnhetNavn("Nav Drift og Utvikling - Team Hakkespett"),
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
                WSFinnAnsvarligEnhetForOppgavetypeRequest request = (WSFinnAnsvarligEnhetForOppgavetypeRequest) invocation.getArguments()[0];
                WSFinnAnsvarligEnhetForOppgavetypeResponse response = new WSFinnAnsvarligEnhetForOppgavetypeResponse();
                leggTilEnhet(response, responser.get(new Random().nextInt(responser.size())));

                if ("DAG".equals(request.getFagomradeKode()) && "KONT_BRUK_DAG".equals(request.getOppgaveKode())) {
                    leggTilEnhet(response, responser.get(new Random().nextInt(responser.size())));
                }
                return response;
            }
        });
        return ruting;
    }

    private static void leggTilEnhet(WSFinnAnsvarligEnhetForOppgavetypeResponse response, WSFinnAnsvarligEnhetForSakResponse enhet) {
        response.getEnhetListe().add(new WSEnhet().withEnhetId(enhet.getEnhetId()).withEnhetNavn(enhet.getEnhetNavn()));
    }

}
