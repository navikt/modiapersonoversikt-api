package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.nyforeldrepenger;

import no.nav.modig.wicket.errorhandling.panels.ErrorHandlingPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import java.util.HashMap;

public class NyttForeldrepengerPanel extends ErrorHandlingPanel{

    public NyttForeldrepengerPanel(String id, IModel<String> model, IModel<String> payloadModel) {
        super(id, model);

        add(getNyttForeldrepengerPanel(model.getObject(), payloadModel.getObject()));
        setOutputMarkupId(true);
    }

    private Component getNyttForeldrepengerPanel(String fnr, String idDato) {
        HashMap<String, Object> props = new HashMap<String, Object>() {{
            put("fødselsnummer", fnr);
        }};
        return new ReactComponentPanel("ny-foreldrepenger", "NyForeldrepenger", props)
                .setOutputMarkupId(true)
                .setVisibilityAllowed(true);
    }


}
