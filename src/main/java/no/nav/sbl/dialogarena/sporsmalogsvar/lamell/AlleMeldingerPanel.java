package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.getStatusKlasse;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class AlleMeldingerPanel extends Panel {

    private final InnboksVM innboksVM;

    public AlleMeldingerPanel(String id, InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        setOutputMarkupId(true);

        this.innboksVM = innboksVM;

        add(new PropertyListView<MeldingVM>("nyesteMeldingerITraad") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {

                item.add(new Label("traadlengde"));

                item.add(new WebMarkupContainer("indikator-dot").add(cssClass(getStatusKlasse(item.getModelObject().melding.status))));
                item.add(new Label("indikator-tekst", new StringResourceModel("lamell.${melding.status}", item.getModel())));

                item.add(new Label("opprettetDato"));
                item.add(new Label("avsender", new ResourceModel(item.getModelObject().avsender)));
                item.add(new Label("melding.temagruppe", new StringResourceModel("${melding.temagruppe}", item.getModel())));

                item.add(new Label("melding.fritekst"));

                item.add(hasCssClassIf("valgt", AlleMeldingerPanel.this.innboksVM.erValgtMelding(item.getModelObject())));
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        AlleMeldingerPanel.this.innboksVM.setValgtMelding(item.getModelObject());
                        send(getPage(), Broadcast.DEPTH, VALGT_MELDING_EVENT);
                        target.add(AlleMeldingerPanel.this);
                    }
                });
            }
        });
    }

    @RunOnEvents(MELDING_SENDT_TIL_BRUKER)
    public void meldingSendtTilBruker(AjaxRequestTarget target) {
        if (this.isVisibleInHierarchy()) {
            innboksVM.oppdaterMeldinger();
            innboksVM.setValgtMelding(innboksVM.getNyesteMeldingINyesteTraad());
            target.add(this);
        }
    }

}
