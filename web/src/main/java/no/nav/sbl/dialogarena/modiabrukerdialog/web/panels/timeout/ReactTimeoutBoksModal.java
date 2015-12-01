package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.timeout;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import java.util.HashMap;
import java.util.Map;

import static org.apache.wicket.markup.head.OnLoadHeaderItem.forScript;

public class ReactTimeoutBoksModal extends Panel {

    public static final String COMPONENT = "FeilmeldingsModaler.SesjonenHarLoptUt";

    private final String jsRef;
    private final String fnr;

    public ReactTimeoutBoksModal(String id, String fnr) {
        super(id);
        this.fnr = fnr;
        final ReactComponentPanel modal = new ReactComponentPanel("modal", COMPONENT, modalProps());
        jsRef = String.format("%s.%s", ReactComponentPanel.JS_REF_INITIALIZED_COMPONENTS, modal.getMarkupId());

        add(modal);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(forScript("setSessionTimeoutBox(" + jsRef + ");"));
        super.renderHead(response);
    }

    private Map<String, Object> modalProps() {
        return new HashMap<String, Object>() {{
            put("hovedtekst", getCmsString("feilmelding.utloptsesjon.tittel"));
            put("beskrivendeTekst", getCmsString("feilmelding.utloptsesjon.hovedtekst"));
            put("avbryttekst", getCmsString("feilmelding.utloptsesjon.lenke.sistebruker"));
            put("fortsetttekst", getCmsString("feilmelding.utloptsesjon.lenke.startsiden"));
            put("fnr", fnr);
        }};
    }

    private String getCmsString(String key) {
        return new StringResourceModel(key, this, null).getString();
    }
}
