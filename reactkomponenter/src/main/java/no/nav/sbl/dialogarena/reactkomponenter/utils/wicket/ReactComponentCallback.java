package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface ReactComponentCallback<T> {
    public void onCallback(AjaxRequestTarget target, T data);
}
