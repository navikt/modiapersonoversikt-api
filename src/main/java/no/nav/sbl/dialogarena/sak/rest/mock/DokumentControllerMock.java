package no.nav.sbl.dialogarena.sak.rest.mock;

import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.DokumentResultat;
import no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning.JournalpostResultat;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDateTime;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.ok;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Kommunikasjonsretning.INN;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Entitet.EKSTERN_PART;
import static org.apache.commons.io.IOUtils.toByteArray;

public class DokumentControllerMock {

    public static Response mockDokumentResponse() throws IOException {
        byte[] dokument = toByteArray(DokumentControllerMock.class.getResourceAsStream("/mock/mock.pdf"));
        return ok(dokument).type("application/pdf").build();
    }

    public static DokumentResultat mockDokumentReferanserResponse(String journalpostid, String dokumentreferanse, String fnr) {
        String pdfUrl = "http://localhost:8083/modiabrukerdialog/rest/saksoversikt/" + fnr + "/dokument/" + journalpostid + "/" + dokumentreferanse;
        return new DokumentResultat("Oppdragsbeskrivelse", 1, fnr, journalpostid, dokumentreferanse);
    }

    public static JournalpostResultat mockJournalpost() {
        return new JournalpostResultat()
                .withTittel("Hoveddokumenttittel")
                .withDokument(new DokumentResultat("Fancy dokument", 7, "101080003987", "123456", "123456"));
//                .withDokumentFeilmelding(new DokumentFeilmelding(DOKUMENT_IKKE_FUNNET.feilmeldingKey, BLURRED_DOKUMENT, new HashMap()));
    }

    public static DokumentMetadata mockDokumentMetaData(String journalpostId) {
        return new DokumentMetadata()
                .withNavn("Andreas")
                .withAvsender(EKSTERN_PART)
                .withRetning(INN)
                .withTemakode("DAG")
                .withDato(LocalDateTime.now())
                .withJournalpostId(journalpostId)
                .withMottaker(EKSTERN_PART)
                .withHoveddokument(new Dokument()
                        .withTittel("Søknad om dagpenger ved arbeidsledighet")
                        .withDokumentreferanse("12321"))
                .withVedlegg(asList(
                        new Dokument()
                                .withTittel("Dokument 1")
                                .withDokumentreferanse("1"),
                        new Dokument()
                                .withTittel("Dokument 2")
                                .withDokumentreferanse("2"),
                        new Dokument()
                                .withTittel("Dokument 3")
                                .withDokumentreferanse("3"),
                        new Dokument()
                                .withTittel("Dokument 4")
                                .withDokumentreferanse("4")
                ));
    }
}
