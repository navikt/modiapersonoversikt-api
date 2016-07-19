package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.model.StringFormatModel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class WidgetMeldingDetaljer extends Panel {

    public WidgetMeldingDetaljer(String id, WidgetMeldingVM widgetMeldingVM) {
        super(id);
        setOutputMarkupId(true);

        WebMarkupContainer meldingDetaljer = new WebMarkupContainer("meldingDetaljer");

        Label meldingStatus = new Label("meldingsstatus", new StringFormatModel("%s - %s",
                new PropertyModel<>(widgetMeldingVM, "melding.statusTekst"),
                new PropertyModel<>(widgetMeldingVM, "melding.temagruppeNavn")
        ));

        Label dokumentStatus = new Label("dokumentstatus", new StringFormatModel("%s",
                new PropertyModel<>(widgetMeldingVM, "melding.statusTekst")
        ));


        meldingDetaljer.add(hasCssClassIf("ubesvart", not(widgetMeldingVM.erBesvart())));
        meldingDetaljer.add(hasCssClassIf("besvart", widgetMeldingVM.erBesvart()));

        meldingDetaljer.add(new StatusIkon("statusIkon", widgetMeldingVM),
                new Label("traadlengde").setVisibilityAllowed(widgetMeldingVM.traadlengde > 2),
                new Label("visningsDato"),
                meldingStatus.setVisibilityAllowed(!widgetMeldingVM.erDokumentMelding),
                dokumentStatus.setVisibilityAllowed(widgetMeldingVM.erDokumentMelding),
                new Label("fritekst").setVisibilityAllowed(widgetMeldingVM.erDokumentMelding));

        add(meldingDetaljer);
    }
}
