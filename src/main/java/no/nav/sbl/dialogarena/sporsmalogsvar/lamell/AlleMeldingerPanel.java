package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.*;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.INNBOKS_OPPDATERT_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;

public class AlleMeldingerPanel extends Panel {

    private InnboksVM innboksVM;

    public AlleMeldingerPanel(String id, final InnboksVM innboksVM, final String traadDetaljerMarkupId) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        setOutputMarkupId(true);

        this.innboksVM = innboksVM;
        final ReactComponentPanel henvendelseSok = new ReactComponentPanel("henvendelseSokContainer", "HenvendelseSok");
        add(henvendelseSok);

        add(new AjaxLink("henvendelseSokToggle") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                henvendelseSok.callFunction(target, "vis");
            }
        });

        add(new PropertyListView<MeldingVM>("nyesteMeldingerITraad") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                final MeldingVM meldingVM = item.getModelObject();
                item.add(new WebMarkupContainer("besvarIndikator").add(visibleIf(blirBesvart(meldingVM.melding.traadId))));
                item.add(new Label("traadlengde").setVisibilityAllowed(meldingVM.traadlengde > 2));
                item.add(new Label("meldingstatus", new StringResourceModel("${meldingStatusTekstKey}", item.getModel()))
                        .add(cssClass(meldingVM.getStatusIkonKlasse())));
                item.add(new Label("avsenderTekst"));
                item.add(new Label("temagruppe", new StringResourceModel("${temagruppeKey}", item.getModel())));
                item.add(new Label("fritekst",
                        meldingVM.melding.fritekst != null ?
                                new PropertyModel(meldingVM, "melding.fritekst") :
                                new ResourceModel("innhold.kassert")
                ));

                item.add(hasCssClassIf("valgt", innboksVM.erValgtMelding(meldingVM)));
                item.add(attributeIf("aria-selected", "true", innboksVM.erValgtMelding(meldingVM), true));
                item.add(attributeIf("aria-controls", traadDetaljerMarkupId, innboksVM.erValgtMelding(meldingVM), true));
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        AlleMeldingerPanel.this.innboksVM.setValgtMelding(meldingVM);
                        send(getPage(), Broadcast.DEPTH, VALGT_MELDING_EVENT);
                        settFokusPaaValgtMelding(target);
                        target.add(AlleMeldingerPanel.this);
                    }
                });
            }
        });
    }

    private AbstractReadOnlyModel<Boolean> blirBesvart(final String traadId) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return traadId.equals(innboksVM.traadBesvares);
            }
        };
    }

    @RunOnEvents({MELDING_SENDT_TIL_BRUKER})
    public void oppdaterMeldingerEtterNyMelding(AjaxRequestTarget target) {
        innboksVM.setValgtMelding(innboksVM.getNyesteMeldingINyesteTraad());
        target.add(this);
    }

    @RunOnEvents({TRAAD_MERKET, TRAAD_JOURNALFORT})
    public void oppdaterMeldingerEtterMerkingEllerJournalforing(AjaxRequestTarget target) {
        if (this.isVisibleInHierarchy()) {
            innboksVM.oppdaterMeldinger();
            if (innboksVM.harTraader()) {
                if (innboksVM.getValgtTraad() == null) {
                    innboksVM.setValgtMelding(innboksVM.getNyesteMeldingINyesteTraad());
                }
                target.appendJavaScript("Meldinger.addKeyNavigation();");
                send(getPage(), Broadcast.DEPTH, VALGT_MELDING_EVENT);
                settFokusPaaValgtMelding(target);
            }
            send(getPage(), Broadcast.DEPTH, INNBOKS_OPPDATERT_EVENT);
            target.add(this);
        }
    }

    private void settFokusPaaValgtMelding(AjaxRequestTarget target) {
        target.appendJavaScript("Meldinger.focusOnSelectedElement();");
    }
}
