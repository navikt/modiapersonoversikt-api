package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

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

public class AlleMeldingerPanel extends Panel {

    ListItem<MeldingVM> current;

    public AlleMeldingerPanel(String id, final IModel<InnboksVM> modell) {
        super(id);
        setOutputMarkupId(true);

        add(new PropertyListView<MeldingVM>("nyesteMeldingerITraad") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {

                item.add(new Label("antallMeldingerITraad"));

                item.add(new WebMarkupContainer("indikator-dot").add(new AttributeModifier("class", item.getModelObject().getStatusKlasse())));
                item.add(new Label("indikator-tekst", new StringResourceModel("lamell.${status}", item.getModel())));

                item.add(new Label("opprettetDato"));
                item.add(new Label("avsender"));
                item.add(new Label("tema", new StringResourceModel("${tema}", item.getModel())));

                item.add(new Label("fritekst"));

                item.add(hasCssClassIf("valgt", modell.getObject().erValgtMelding(item.getModelObject())));
                if (modell.getObject().erValgtMelding(item.getModelObject()).getObject()) {
                    current = item;
                }
                item.add(hasCssClassIf("lest", item.getModelObject().erLest()));
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        modell.getObject().setValgtMelding(item.getModelObject());
                        send(getPage(), Broadcast.DEPTH, Innboks.VALGT_MELDING);
                        target.add(item, current);
                        current = item;
                    }
                });
            }
        });
    }
}
