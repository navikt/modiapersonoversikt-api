package no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning;

import static java.lang.Boolean.*;

public class DokumentFeilmelding {

    private String feilmeldingEnonicKey;
    private String bildeUrl;
    private Boolean kanVises = FALSE;

    public DokumentFeilmelding(String feilmeldingEnonicKey, String bildeUrl) {
        this.feilmeldingEnonicKey = feilmeldingEnonicKey;
        this.bildeUrl = bildeUrl;
    }

    public String getFeilmeldingEnonicKey() {
        return feilmeldingEnonicKey;
    }

    public String getBildeUrl() {
        return bildeUrl;
    }

    public Boolean getKanVises() {
        return kanVises;
    }
}
