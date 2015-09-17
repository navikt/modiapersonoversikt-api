package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.journal.v1.*;
import no.nav.tjeneste.virksomhet.journal.v1.informasjon.WSArkivtemaer;
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
import java.util.Map;

import static no.nav.sbl.dialogarena.common.collections.Collections.asMap;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakSakV1PortTypeMock.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class JoarkPortTypeMock {

    private static WSJournalpost defaultJournalpost = createJournalpost(JOURNALPOSTID_DEFAULT, "J", SAK_MED_INNSENDER, false, "DAG");

    private static Map<String, WSJournalpost> journalpostMap =
            asMap(
                    JOURNALPOSTID_DEFAULT, defaultJournalpost,
                    JOURNALPOSTID_FEILREGISTRERT_SAK, createJournalpost(JOURNALPOSTID_FEILREGISTRERT_SAK, "J", SAK_MED_INNSENDER, true, "DAG"),
                    JOURNALPOSTID_IKKE_JOURNALFORT, createJournalpost(JOURNALPOSTID_IKKE_JOURNALFORT, "N", SAK_MED_INNSENDER, false, "DAG"),
                    JOURNALPOSTID_IKKE_SAKSPART, createJournalpost(JOURNALPOSTID_IKKE_SAKSPART, "J", SAK_UTEN_INNSENDER, false, "DAG"),
                    JOURNALPOSTID_DOKUMENT_SLETTET, createJournalpost(JOURNALPOSTID_DOKUMENT_SLETTET, "J", SAK_MED_INNSENDER, false, "DAG")
            );

    @Bean(name = "joarkPortType")
    public static Journal_v1PortType getJournalPortTypeMock() throws HentDokumentSikkerhetsbegrensning, HentDokumentDokumentIkkeFunnet, HentDokumentDokumentErSlettet, HentDokumentURLDokumentIkkeFunnet, HentJournalpostJournalpostIkkeFunnet, HentJournalpostSikkerhetsbegrensning {
        Journal_v1PortType mock = mock(Journal_v1PortType.class);
        when(mock.hentDokument(any(WSHentDokumentRequest.class))).thenAnswer(new Answer<WSHentDokumentResponse>() {
            @Override
            public WSHentDokumentResponse answer(InvocationOnMock invocation) throws HentDokumentDokumentErSlettet {
                Object requestArgument = invocation.getArguments()[0];
                if (requestArgument == null) {
                    return hentDokumentResponse(JOURNALPOSTID_DEFAULT);
                }

                String journalpostId = ((WSHentDokumentRequest) requestArgument).getJournalpostId();
                return hentDokumentResponse(journalpostId);
            }
        });
        when(mock.hentJournalpost(any(WSHentJournalpostRequest.class))).thenAnswer(new Answer<WSHentJournalpostResponse>() {
            @Override
            public WSHentJournalpostResponse answer(InvocationOnMock invocation) {
                String journalpostId = ((WSHentJournalpostRequest) invocation.getArguments()[0]).getJournalpostId();
                return new WSHentJournalpostResponse()
                        .withJournalpost(hentJournalpostResponse(journalpostId));
            }
        });
        return mock;
    }

    private static WSHentDokumentResponse hentDokumentResponse(String journalpostId) throws HentDokumentDokumentErSlettet {
        if (journalpostId.equals(JOURNALPOSTID_DOKUMENT_SLETTET)) {
            throw new HentDokumentDokumentErSlettet();
        } else {
            return new WSHentDokumentResponse()
                    .withDokument(createDokument());
        }
    }

    private static WSJournalpost hentJournalpostResponse(String journalpostId) {
        if (journalpostMap.containsKey(journalpostId)) {
            return journalpostMap.get(journalpostId);
        } else {
            return defaultJournalpost;
        }
    }

    private static WSJournalpost createJournalpost(String journalpostId, String status, String sakId, boolean feilregistrert, String tema) {
        return new WSJournalpost()
                .withJournalpostId(journalpostId)
                .withJournalstatus(createJournalStatus(status))
                .withGjelderSak(createSak(sakId, feilregistrert))
                .withArkivtema(new WSArkivtemaer().withValue(tema));
    }

    private static WSSak createSak(String sakId, boolean feilregistrert) {
        return new WSSak()
                .withSakId(sakId)
                .withErFeilregistrert(feilregistrert);
    }

    private static WSJournalstatuser createJournalStatus(String verdi) {
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
