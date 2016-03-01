package no.nav.sbl.dialogarena.sak.viewdomain.dokumentvisning;

import java.util.Map;

import static java.lang.Boolean.*;

public class DokumentFeilmelding {

    private String feilmeldingEnonicKey;
    private String bildeUrl;
    private Boolean kanVises = FALSE;
    private Map<String, String> ekstrafeilinfo;

    public DokumentFeilmelding(String feilmeldingEnonicKey, String bildeUrl, Map ekstrafeilinfo) {
        this.feilmeldingEnonicKey = feilmeldingEnonicKey;
        this.bildeUrl = bildeUrl;
        this.ekstrafeilinfo = ekstrafeilinfo;
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


    public Map<String, String> getEkstrafeilinfo() {
        return ekstrafeilinfo;
    }
}
