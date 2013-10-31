package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.panel.JournalforPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.VALGT_MELDING;


public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id, String fnr, CompoundPropertyModel<InnboksVM> innboks) {
        super(id);
        setOutputMarkupId(true);

        add(new JournalforPanel("journalfor-panel", innboks.<Traad>bind("valgtTraadForJournalforing"), fnr));
        add(new Label("tema", new StringResourceModel("${valgtTraadTema}", innboks)));
        add(new NyesteMeldingPanel("nyeste-melding"));
        add(new TidligereMeldingerPanel("tidligere-meldinger"));
    }

    @RunOnEvents(VALGT_MELDING)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }
}
