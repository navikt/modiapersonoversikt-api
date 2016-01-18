package no.nav.sbl.dialogarena.modiabrukerdialog.web.tekniskfeil;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ReactTekniskFeilModal extends Panel {

    public static final String COMPONENT = "FeilmeldingsModaler.TekniskFeil";

    public ReactTekniskFeilModal(String id, PageParameters pageParameters) {
        super(id);
        ReactComponentPanel modal = new ReactComponentPanel("modal", COMPONENT, modalProps(pageParameters));
        add(modal);
    }

    private Map<String, Object> modalProps(final PageParameters pageParameters) {
        return new HashMap<String, Object>() {{
            put("tekst", getCmsString("feilmelding.tekniskfeil.tekst"));
            put("isOpen", !isBlank(pageParameters.get("tekniskfeil").toString()));
        }};
    }

    private String getCmsString(String key) {
        return new StringResourceModel(key, this, null).getString();
    }
}
