package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.begrunnelse;

import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentCallback;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import java.util.HashMap;
import java.util.Map;

public class ReactBegrunnelseModal extends Panel {

    public static final String COMPONENT = "FeilmeldingsModaler.OppgiBegrunnelse";
    public static final String DISCARD = "discard";
    public static final String CONFIRM = "confirm";
    private final ReactComponentPanel modal;

    public ReactBegrunnelseModal(String id) {
        super(id);
        modal = new ReactComponentPanel("modal", COMPONENT, modalProps());
        add(modal);
    }

    public <T> void addCallback(String action, Class<T> expectedType, ReactComponentCallback<T> callback) {
        this.modal.addCallback(action, expectedType, callback);
    }

    private Map<String, Object> modalProps() {
        return new HashMap<String, Object>() {{
            put("title", getCmsString("bekreft.begrunnelse.tittel"));
            put("avbryttekst", getCmsString("bekreft.begrunnelse.avbryttekst"));
            put("lagretekst", getCmsString("bekreft.begrunnelse.lagretekst"));
            put("discardCallback", DISCARD);
            put("confirmCallback", CONFIRM);
        }};
    }

    private String getCmsString(String key) {
        return new StringResourceModel(key, this, null).getString();
    }

    public void show(AjaxRequestTarget target, String fnr) {
        modal.call("vis", fnr);
    }

    public void hide(AjaxRequestTarget target) {
        modal.call("skjul");
    }
}
