package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;

public class AlleMeldingerPanel extends Panel {

    private InnboksVM innboksVM;

    public AlleMeldingerPanel(String id, final InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        setOutputMarkupId(true);

        this.innboksVM = innboksVM;

        add(new PropertyListView<MeldingVM>("nyesteMeldingerITraad") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                final MeldingVM meldingVM = item.getModelObject();
                item.add(new Label("traadlengde").setVisibilityAllowed(meldingVM.traadlengde > 2));
                item.add(new Label("meldingstatus", new StringResourceModel("${meldingStatusTekstKey}", item.getModel()))
                        .add(cssClass(meldingVM.getStatusIkonKlasse())));
                item.add(new Label("avsenderTekst"));
                item.add(new Label("temagruppe", new StringResourceModel("${temagruppeKey}", item.getModel())));
                item.add(new Label("fritekst",
                        meldingVM.melding.fritekst != null ?
                                new PropertyModel(meldingVM, "melding.fritekst") :
                                new ResourceModel("innhold.kassert")));

                item.add(hasCssClassIf("valgt", innboksVM.erValgtMelding(meldingVM)));
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        AlleMeldingerPanel.this.innboksVM.setValgtMelding(meldingVM);
                        send(getPage(), Broadcast.DEPTH, VALGT_MELDING_EVENT);
                        target.add(AlleMeldingerPanel.this);
                    }
                });
            }
        });
    }

    @RunOnEvents({MELDING_SENDT_TIL_BRUKER, TRAAD_MERKET, TRAAD_JOURNALFORT})
    public void oppdaterMeldinger(AjaxRequestTarget target) {
        if (this.isVisibleInHierarchy()) {
            innboksVM.oppdaterMeldinger();
            if (innboksVM.harTraader()) {
                innboksVM.setValgtMelding(innboksVM.getNyesteMeldingINyesteTraad());
            }
            target.add(this);
        }
    }

}
