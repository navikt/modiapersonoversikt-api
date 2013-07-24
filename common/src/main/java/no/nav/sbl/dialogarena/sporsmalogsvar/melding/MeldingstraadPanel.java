package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.InnboksModell;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class MeldingstraadPanel extends Panel {

    private final InnboksModell innboksModell;

    public MeldingstraadPanel(String id, InnboksModell innboksModell) {
        super(id);
        this.innboksModell = innboksModell;
        setOutputMarkupId(true);
        add(new Traad("traad"));
    }

    @RunOnEvents(Innboks.VALGT_MELDING_EVENT)
    public void valgteMelding(AjaxRequestTarget target, MeldingVM melding) {
        target.add(this);
    }

    private class Traad extends PropertyListView<MeldingVM> {

        public Traad(String id) {
            super(id);
            setOutputMarkupId(true);
        }

        @Override
        protected void populateItem(final ListItem<MeldingVM> item) {
            item.setOutputMarkupId(true);
            Label overskrift = new Label("overskrift");
            Label fritekst = new Label("fritekst");

            item.add(overskrift, fritekst);

            item.add(hasCssClassIf("valgt", innboksModell.erValgtMelding(item.getModelObject())));
            item.add(hasCssClassIf("expanded", innboksModell.erValgtMelding(item.getModelObject())));
        }
    }
}
