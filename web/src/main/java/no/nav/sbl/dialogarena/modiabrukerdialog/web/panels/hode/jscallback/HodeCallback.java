package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.Hode;
import org.apache.wicket.ajax.AjaxRequestTarget;

public interface HodeCallback<T> {
    public void onCallback(AjaxRequestTarget target, Hode component, T data);
}
