package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.model.StringFormatModel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class MeldingerWidgetPanel extends GenericPanel<WidgetMeldingVM> {

    public MeldingerWidgetPanel(String id, IModel<WidgetMeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        Label meldingStatus = new Label("meldingsstatus", new StringFormatModel("%s - %s",
                new PropertyModel<>(getModel(), "melding.statusTekst"),
                new PropertyModel<>(getModel(), "melding.temagruppeNavn")
        ));

        Label dokumentStatus = new Label("dokumentstatus", new StringFormatModel("%s",
                new PropertyModel<>(getModel(), "melding.statusTekst")
        ));

        add(
                new StatusIkon("statusIkon", getModelObject()),
                new Label("traadlengde").setVisibilityAllowed(getModelObject().traadlengde > 2),
                new Label("opprettetDato"),
                meldingStatus.setVisibilityAllowed(!getModelObject().erDokumentMelding),
                dokumentStatus.setVisibilityAllowed(getModelObject().erDokumentMelding),
                new Label("fritekst")
        );
    }
}