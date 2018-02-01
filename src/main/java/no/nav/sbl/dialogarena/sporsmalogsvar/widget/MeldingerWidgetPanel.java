package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.model.StringFormatModel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.*;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class MeldingerWidgetPanel extends GenericPanel<WidgetMeldingVM> {

    public MeldingerWidgetPanel(String id, IModel<WidgetMeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        WebMarkupContainer meldingDetaljer = new WebMarkupContainer("meldingDetaljer");

        Label meldingStatus = new Label("meldingsstatus", new StringFormatModel("%s – %s",
                new PropertyModel<>(model.getObject(), "melding.statusTekst"),
                new PropertyModel<>(model.getObject(), "melding.temagruppeNavn")
        ));

        Label dokumentStatus = new Label("dokumentstatus", new StringFormatModel("%s",
                new PropertyModel<>(model.getObject(), "melding.statusTekst")
        ));

        meldingDetaljer.add(hasCssClassIf("ubesvart", not(model.getObject().erBesvart())));
        meldingDetaljer.add(hasCssClassIf("besvart", model.getObject().erBesvart()));

        meldingDetaljer.add(new StatusIkon("statusIkon", model.getObject()),
                new Label("visningsDato"),
                meldingStatus.setVisibilityAllowed(!model.getObject().erDokumentMelding && !model.getObject().erOppgaveMelding),
                dokumentStatus.setVisibilityAllowed(model.getObject().erDokumentMelding || model.getObject().erOppgaveMelding),
                new Label("fritekst"));

        add(meldingDetaljer);
    }
}