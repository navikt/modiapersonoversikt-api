package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;


public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id, IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupId(true);

        add(new NyesteMeldingPanel("nyeste-melding", innboksVM));
        add(new TidligereMeldingerPanel("tidligere-meldinger"));
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }
}
