package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;


public class NyesteMeldingPanel extends GenericPanel<MeldingVM> {

    public NyesteMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(new PropertyModel<MeldingVM>(innboksVM, "valgtTraad.nyesteMelding")));

        add(new AvsenderBilde("avsenderbilde", getModel()));
        add(new JournalfortSkiller("journalfortSkiller", getModel()));
        add(new KontorsperreInfoPanel("kontorsperretInfo", innboksVM));
        add(new FeilsendtInfoPanel("feilsendtInfo", getModel()));
        add(new Label("meldingstatus", new StringResourceModel("${meldingStatusTekstKey}", getModel()))
                .add(cssClass(new PropertyModel<String>(getModel(), "statusIkonKlasse"))));
        add(new Label("avsenderTekst"));
        add(new Label("temagruppe", new StringResourceModel("${temagruppeKey}", getModel())));
        add(new URLParsingMultiLineLabel("fritekst", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return getModelObject().melding.fritekst != null ?
                        getModelObject().melding.fritekst :
                        new StringResourceModel("innhold.kassert", NyesteMeldingPanel.this, getModel()).getObject();
            }
        }));
    }

}
