package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.journal.v1.*;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSJournalpost;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSJournalstatuser;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSSak;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentDokumentRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentDokumentResponse;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentJournalpostRequest;
import no.nav.tjeneste.virksomhet.journal.v1.meldinger.WSHentJournalpostResponse;
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
    public static Journal_v1PortType getJournalPortTypeMock() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet, HentDokumentURLDokumentIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        Journal_v1PortType mock = mock(Journal_v1PortType.class);
        when(mock.hentDokument(any(WSHentDokumentRequest.class))).thenAnswer(new Answer<WSHentDokumentResponse>() {
            @Override
            public WSHentDokumentResponse answer(InvocationOnMock invocation) {
                return new WSHentDokumentResponse()
                        .withDokument(createDokument());
            }
        });

        when(mock.hentJournalpost(any(WSHentJournalpostRequest.class))).thenAnswer(new Answer<WSHentJournalpostResponse>() {
            @Override
            public WSHentJournalpostResponse answer(InvocationOnMock invocationOnMock) {
                return new WSHentJournalpostResponse()
                        .withJournalpost(createJournalpost());
            }
        });

        return mock;
    }

    private static WSJournalpost createJournalpost() {
        return new WSJournalpost()
                .withJournalpostId(JOURNALPOSTID)
                .withJournalstatus(createJournalstatus("J"))
                .withGjelderSak(createSak());
    }

    private static WSSak createSak() {
        return new WSSak()
                .withSakId("sakId")
                .withErFeilregistrert(false);
    }

    private static WSJournalstatuser createJournalstatus(String verdi) {
        return new WSJournalstatuser()
                .withValue(verdi);
    }

    private static byte[] createDokument() {
        try {
            return IOUtils.toByteArray(JoarkPortTypeMock.class.getResourceAsStream("/mock/pdf-vedlegg-mock.pdf"));
        } catch (IOException e) {
            throw new RuntimeException("IOException ved henting av Mock PDFen", e);
        }
    }
}
