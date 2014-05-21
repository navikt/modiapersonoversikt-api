package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;


public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id, CompoundPropertyModel<InnboksVM> innboks) {
        super(id);
        setOutputMarkupId(true);

        add(new Label("tema", new StringResourceModel("${valgtTraadTema}", innboks)));
        add(new NyesteMeldingPanel("nyeste-melding"));
        add(new TidligereMeldingerPanel("tidligere-meldinger"));
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }
}
