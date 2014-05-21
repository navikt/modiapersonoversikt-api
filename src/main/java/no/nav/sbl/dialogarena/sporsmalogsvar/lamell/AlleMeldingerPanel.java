package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.VALGT_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.getStatusKlasse;

public class AlleMeldingerPanel extends Panel {

    ListItem<MeldingVM> current;

    public AlleMeldingerPanel(String id, final IModel<InnboksVM> model) {
        super(id);
        setOutputMarkupId(true);

        add(new PropertyListView<MeldingVM>("nyesteMeldingerITraad") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {

                item.add(new Label("traadLengde"));

                item.add(new WebMarkupContainer("indikator-dot").add(new AttributeModifier("class", getStatusKlasse(item.getModelObject().melding.status))));
                item.add(new Label("indikator-tekst", new StringResourceModel("lamell.${melding.status}", item.getModel())));

                item.add(new Label("opprettetDato"));
                item.add(new Label("avsender"));
                item.add(new Label("melding.tema", new StringResourceModel("${melding.tema}", item.getModel())));

                item.add(new Label("melding.fritekst"));

                final InnboksVM innboksVM = model.getObject();
                item.add(hasCssClassIf("valgt", innboksVM.erValgtMelding(item.getModelObject())));
                if (innboksVM.erValgtMelding(item.getModelObject()).getObject()) {
                    current = item;
                }
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        innboksVM.setValgtMelding(item.getModelObject());
                        send(getPage(), Broadcast.DEPTH, VALGT_MELDING);
                        target.add(item, current);
                        current = item;
                    }
                });
            }
        });
    }
}
