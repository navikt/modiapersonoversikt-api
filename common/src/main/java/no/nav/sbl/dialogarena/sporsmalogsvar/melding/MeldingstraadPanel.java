package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.InnboksModell;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
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
    public void valgteMelding(AjaxRequestTarget target, ValgtMeldingOppdatert valgtMeldingOppdatert) {
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
            item.add(new MeldingsHeader("header"));
            item.add(new Label("fritekst"));

            item.add(hasCssClassIf("valgt", innboksModell.erValgtMelding(item.getModelObject())));
            item.add(hasCssClassIf("ekspandert", innboksModell.erValgtMelding(item.getModelObject())));

            item.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    MeldingVM forrige = innboksModell.getInnboksVM().getValgtMelding();
                    innboksModell.getObject().setValgtMelding(item.getModelObject());
                    send(getPage(), Broadcast.BREADTH, new NamedEventPayload(Innboks.VALGT_MELDING_EVENT, new ValgtMeldingOppdatert(forrige, item.getModelObject(), true)));
                }
            });
        }
    }
}
