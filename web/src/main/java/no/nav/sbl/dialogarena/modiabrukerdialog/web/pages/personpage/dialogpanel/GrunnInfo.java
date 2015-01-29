package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import java.io.Serializable;

public class GrunnInfo implements Serializable {

    public static final String FALLBACK_FORNAVN = "bruker";

    public String fnr;
    private String fornavn;

    public GrunnInfo(String fnr, String fornavn) {
        this.fnr = fnr;
        this.fornavn = fornavn;
    }

    public String getFornavn() {
        if (fornavn == null) {
            return FALLBACK_FORNAVN;
        } else {
            return fornavn;
        }
    }
}
