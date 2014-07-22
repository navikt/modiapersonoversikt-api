package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import java.io.Serializable;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.CookieHandler.getCookieUtils;

public class SaksbehandlerInstillinger implements Serializable {

    public String valgtEnhet;

    public SaksbehandlerInstillinger() {
        this.valgtEnhet = getCookieUtils().load(brukerSpesifikCookieId());
    }

    public void lagreInstillingerCookie() {
        getCookieUtils().save(brukerSpesifikCookieId(), valgtEnhet);
    }

    private String brukerSpesifikCookieId() {
        return "saksbehandlerinstillinger-" + getSubjectHandler().getUid();
    }
}
