package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
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

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.getStatusKlasse;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class AlleMeldingerPanel extends Panel {

    ListItem<MeldingVM> valgtMelding;

    private final IModel<InnboksVM> innboksVMModel;

    public AlleMeldingerPanel(String id, IModel<InnboksVM> model) {
        super(id);
        setOutputMarkupId(true);

        this.innboksVMModel = model;

        add(new PropertyListView<MeldingVM>("nyesteMeldingerITraad") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {

                item.add(new Label("traadlengde"));

                item.add(new WebMarkupContainer("indikator-dot").add(new AttributeModifier("class", getStatusKlasse(item.getModelObject().melding.status))));
                item.add(new Label("indikator-tekst", new StringResourceModel("lamell.${melding.status}", item.getModel())));

                item.add(new Label("opprettetDato"));
                item.add(new Label("avsender", getString(item.getModelObject().avsender)));
                item.add(new Label("melding.tema", new StringResourceModel("${melding.tema}", item.getModel())));

                item.add(new Label("melding.fritekst"));

                final InnboksVM innboksVM = innboksVMModel.getObject();
                item.add(hasCssClassIf("valgt", innboksVM.erValgtMelding(item.getModelObject())));
                if (innboksVM.erValgtMelding(item.getModelObject()).getObject()) {
                    valgtMelding = item;
                }
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        innboksVM.setValgtMelding(item.getModelObject());
                        send(getPage(), Broadcast.DEPTH, VALGT_MELDING_EVENT);
                        target.add(item, valgtMelding);
                        valgtMelding = item;
                    }
                });
            }
        });
    }

    @RunOnEvents(MELDING_SENDT_TIL_BRUKER)
    public void meldingSendtTilBruker(AjaxRequestTarget target){
        if(this.isVisibleInHierarchy()){
            innboksVMModel.getObject().oppdaterMeldinger();
            target.add(this);
        }
    }

}
