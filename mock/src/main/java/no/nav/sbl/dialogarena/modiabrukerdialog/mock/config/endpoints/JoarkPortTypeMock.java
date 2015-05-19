package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.journal.v1.binding.*;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Journalpost;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentDokumentResponse;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentJournalpostRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.HentJournalpostResponse;
import org.apache.commons.io.IOUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock.JOURNALPOSTID;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class JoarkPortTypeMock {

    @Bean(name = "joarkPortType")
    public static JournalV1 getJournalPortTypeMock() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet, HentDokumentURLDokumentIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        JournalV1 mock = mock(JournalV1.class);
        when(mock.hentDokument(any(HentDokumentRequest.class))).thenAnswer(new Answer<HentDokumentResponse>() {
            @Override
            public HentDokumentResponse answer(InvocationOnMock invocation) {
                HentDokumentResponse response = new HentDokumentResponse();
                response.setDokument(new Byte("1"));
                return response;
            }
        });

        when(mock.hentJournalpost(any(HentJournalpostRequest.class))).thenAnswer(new Answer<HentJournalpostResponse>() {
            @Override
            public HentJournalpostResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                HentJournalpostResponse journalpostResponse = new HentJournalpostResponse();
                journalpostResponse.setJournalpost(createJournalpost());
                return journalpostResponse;
            }
        });

        return mock;
    }

    private static Journalpost createJournalpost() {
        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId(JOURNALPOSTID);
        journalpost.setGjelderSak(createSak());
        return journalpost;
    }

    private static Sak createSak() {
        Sak sak = new Sak();
        sak.setSakId("sakId");
        sak.setErFeilregistrert(false);
        return sak;
    }

    private static byte[] createDokument() {
        try {
            return IOUtils.toByteArray(JoarkPortTypeMock.class.getResourceAsStream("/mock/mock.pdf"));
        } catch (IOException e) {
            throw new RuntimeException("IOException ved henting av Mock PDFen", e);
        }
    }
}
