package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;


public class NyesteMeldingPanel extends Panel {

    private AvsenderBilde avsenderbilde;

    public NyesteMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(new PropertyModel<MeldingVM>(innboksVM, "valgtTraad.nyesteMelding")));

        this.avsenderbilde = new AvsenderBilde("avsenderbilde", (MeldingVM) getDefaultModelObject());
        add(avsenderbilde);
        add(new JournalfortSkiller("journalfortSkiller", getDefaultModel()));
        add(new KontorsperreInfoPanel("kontorsperretInfo", innboksVM));
        add(new FeilsendtInfoPanel("feilsendtInfo", innboksVM));
        add(new Label("meldingstatus", new StringResourceModel("${meldingStatusTekstKey}", getDefaultModel()))
                .add(cssClass(new PropertyModel<String>(getDefaultModel(), "statusIkonKlasse"))));
        add(new Label("opprettetDato"));
        add(new Label("temagruppe", getNullSafeTemagruppeModel(getDefaultModel(), this)));
        add(new URLParsingMultiLineLabel("melding.fritekst"));
    }

    @Override
    protected void onBeforeRender() {
        avsenderbilde.settBildeRessurs((MeldingVM) getDefaultModelObject());
        super.onBeforeRender();
    }

    private IModel<String> getNullSafeTemagruppeModel(final IModel<?> defaultModel, final NyesteMeldingPanel component) {
        return new AbstractReadOnlyModel<String>() {
            private StringResourceModel stringResourceModel = new StringResourceModel("${melding.temagruppe}", component, defaultModel);

            @Override
            public String getObject() {
                if (((MeldingVM) getDefaultModelObject()).melding.temagruppe != null) {
                    return stringResourceModel.getObject();
                } else {
                    return getString("temagruppe.kassert");
                }
            }
        };
    }

}
