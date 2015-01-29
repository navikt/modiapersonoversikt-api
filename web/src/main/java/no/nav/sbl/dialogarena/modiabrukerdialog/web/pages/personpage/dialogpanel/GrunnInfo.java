package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import java.io.Serializable;

public class GrunnInfo implements Serializable {

    public static final String FALLBACK_FORNAVN = "bruker";

    public String fnr;
    public String fornavn;

    public GrunnInfo(String fnr, String fornavn) {
        this.fnr = fnr;
        this.fornavn = fornavn;
    }
}
