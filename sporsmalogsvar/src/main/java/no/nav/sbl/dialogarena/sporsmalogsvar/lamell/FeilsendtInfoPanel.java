package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import java.util.HashMap;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class FeilsendtInfoPanel extends Panel {

    public FeilsendtInfoPanel(String id, IModel<MeldingVM> meldingVM) {
        super(id, meldingVM);
        setOutputMarkupId(true);

        String feilsendtPostString = new StringResourceModel("feilsendtInfo.feilsendtTekst", this, null, "Feilsendt post").getString();
        String markertAv = new StringResourceModel("feilsendtInfo.markertAv", this, null, "Markert som feilsendt av:").getString();
        String veilederIdent = meldingVM.getObject().getMarkertSomFeilsendtAv().getOrElse("");

        String tekst = feilsendtPostString + " | " + markertAv + " " + veilederIdent;
        add(new ReactComponentPanel("markertAv", "AlertStripeSuksessSolid", new HashMap<String, Object>(){{
            put("tekst", tekst);
        }}));

        new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return meldingVM.getObject().erFeilsendt();
            }
        };

        add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return meldingVM.getObject().erFeilsendt();
            }
        }));
    }
}
