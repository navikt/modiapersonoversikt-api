package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.nysykepenger;

import no.nav.modig.wicket.errorhandling.panels.ErrorHandlingPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import java.util.HashMap;

public class NyttSykmeldingsperiodePanel extends ErrorHandlingPanel {
    public NyttSykmeldingsperiodePanel(String id, IModel<String> model, IModel<String> payloadModel) {
        super(id, model);

        add(getNyttSykepengerPanel(model.getObject(), payloadModel.getObject()));
        setOutputMarkupId(true);
    }

    private Component getNyttSykepengerPanel(String fnr, String idDato) {
        HashMap<String, Object> props = new HashMap<String, Object>() {{
            put("fødselsnummer", fnr);
            put("sykmeldtFraOgMed", idDato);
        }};
        return new ReactComponentPanel("ny-sykepenger", "NySykepenger", props)
                .setOutputMarkupId(true)
                .setVisibilityAllowed(true);
    }
}
