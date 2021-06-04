package no.nav.modiapersonoversikt.integration.kodeverk2;

import org.junit.Test;

public class DownloadedFileTest {

    @Test
    @SuppressWarnings("PMD.SystemPrintln")
    public void shouldParseDownloadedFile() {
        System.out.println("Trying to parse new kodeverk");
        new JsonKodeverk(DownloadedFileTest.class.getResourceAsStream("/kodeverk.json"));
        System.out.println("Kodeverk OK!");
    }
}
