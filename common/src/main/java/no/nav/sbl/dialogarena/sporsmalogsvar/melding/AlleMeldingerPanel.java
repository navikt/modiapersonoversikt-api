package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.InnboksModell;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class AlleMeldingerPanel extends Panel {

    private final InnboksModell innboksModell;

    private ListItem<MeldingVM> valgtItem;

    public AlleMeldingerPanel(String id, InnboksModell innboksModell) {
        super(id, innboksModell);
        this.innboksModell = innboksModell;
        add(new Meldingsliste("meldinger"));
    }

    private class Meldingsliste extends PropertyListView<MeldingVM> {

        public Meldingsliste(final String id) {
            super(id);
        }

        @Override
        protected void populateItem(final ListItem<MeldingVM> item) {
            item.add(new MeldingsHeader("header"));
            item.add(new Label("fritekst"));
            IModel<Boolean> erValgtMelding = innboksModell.erValgtMelding(item.getModelObject());
            item.add(hasCssClassIf("valgt", erValgtMelding));

            if (erValgtMelding.getObject()) {
                valgtItem = item;
            }

            item.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    innboksModell.getObject().setValgtMelding(item.getModelObject());
                    ListItem<MeldingVM> forrige = valgtItem;
                    valgtItem = item;
                    if (forrige != null) {
                        target.add(item, forrige);
                    }
                    send(getPage(), Broadcast.BREADTH, new NamedEventPayload(Innboks.VALGT_MELDING_EVENT, item.getModelObject()));
                }
            });
        }
    }
}
