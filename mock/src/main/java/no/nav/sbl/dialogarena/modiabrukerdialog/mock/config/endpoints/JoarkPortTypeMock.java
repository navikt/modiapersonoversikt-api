package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.journal.v1.binding.*;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentDokumentURLRequest;
import org.apache.commons.io.IOUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class JoarkPortTypeMock {

    @Bean(name = "joarkPortType")
    public static JournalV1 getJournalPortTypeMock() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet, HentDokumentURLDokumentIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        JournalV1 mock = mock(JournalV1.class);
        when(mock.hentDokument(any(HentDokumentURLRequest.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                return createDokument();
            }
        });
        return mock;
    }

    private static byte[] createDokument() {
        try {
            return IOUtils.toByteArray(JoarkPortTypeMock.class.getResourceAsStream("/mock/mock.pdf"));
        } catch (IOException e) {
            throw new RuntimeException("IOException ved henting av Mock PDFen", e);
        }
    }
}
