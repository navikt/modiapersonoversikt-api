package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import javax.inject.Inject;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class Innboks extends Panel {

    public static final String VALGT_MELDING_EVENT = "hendelser.valgte_melding";
    public static final String MELDINGER_OPPDATERT = "hendelser.meldinger_oppdatert";

    @Inject
    MeldingService service;

    public Innboks(String id, String fodselsnr) {
        super(id);
        AlleMeldingerModel alleMeldingerModel = new AlleMeldingerModel(fodselsnr, service);
        setDefaultModel(alleMeldingerModel);
        IModel<Melding> valgtMeldingModel = new Model<>();
        setOutputMarkupId(true);

        add(new AlleMeldingerPanel("meldinger", valgtMeldingModel, alleMeldingerModel));
        add(new MeldingstraadPanel("traad", valgtMeldingModel, new MeldingstraadModel(valgtMeldingModel, alleMeldingerModel)));

        add(new AttributeAppender("class", " innboks clearfix"));
    }

    @RunOnEvents(MELDINGER_OPPDATERT)
    public void messagesUpdated(AjaxRequestTarget target) {
        target.add(this);
    }
}
