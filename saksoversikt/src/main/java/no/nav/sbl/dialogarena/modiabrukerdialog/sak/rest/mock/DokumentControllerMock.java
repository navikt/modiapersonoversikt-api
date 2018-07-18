package no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest.mock;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.dokumentvisning.DokumentResultat;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.domain.dokumentvisning.JournalpostResultat;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static javax.ws.rs.core.Response.ok;
import static org.apache.commons.io.IOUtils.toByteArray;

public class DokumentControllerMock {

    public static Response mockDokumentResponse() throws IOException {
        byte[] dokument = toByteArray(DokumentControllerMock.class.getResourceAsStream("/mock/mock.pdf"));
        return ok(dokument).type("application/pdf").build();
    }

    public static JournalpostResultat mockJournalpost() {
        return new JournalpostResultat()
                .withTittel("Hoveddokumenttittel")
                .withDokument(new DokumentResultat("Fancy dokument", 7, "101080003987", "123456", "123456", false));
    }
}
