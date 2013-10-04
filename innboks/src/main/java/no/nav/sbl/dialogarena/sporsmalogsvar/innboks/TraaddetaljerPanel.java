package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks.VALGT_MELDING;

public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id, final IModel<InnboksVM> model) {
        super(id);
        setOutputMarkupId(true);
        add(new Label("tema", new StringResourceModel("${valgtTraadTema}", model)));
        add(new NyesteMeldingPanel("nyeste-melding"));
        add(new TidligereMeldingerPanel("tidligere-meldinger"));
    }

    @RunOnEvents(VALGT_MELDING)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }
}
