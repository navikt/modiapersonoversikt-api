package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.*;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.INNBOKS_OPPDATERT_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;

public class AlleMeldingerPanel extends Panel {

    private InnboksVM innboksVM;
    private final Map<String, String> traadRef = new HashMap<>();

    public AlleMeldingerPanel(String id, final InnboksVM innboksVM, final String traadDetaljerMarkupId) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        setOutputMarkupId(true);

        this.innboksVM = innboksVM;
        Map<String, Object> props = new HashMap<>();
        props.put("fnr", innboksVM.getFnr());
        final ReactComponentPanel henvendelseSok = new ReactComponentPanel("henvendelseSokContainer", "HenvendelseSok", props);
        add(henvendelseSok);

        add(new AjaxLink("henvendelseSokToggle") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                henvendelseSok.callFunction(target, "oppdaterTraadRef", traadRef);
                henvendelseSok.callFunction(target, "vis");
            }
        });

        add(new PropertyListView<MeldingVM>("nyesteMeldingerITraad") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                final MeldingVM meldingVM = item.getModelObject();

                traadRef.put(meldingVM.melding.traadId, item.getMarkupId());

                item.add(new WebMarkupContainer("besvarIndikator").add(visibleIf(blirBesvart(meldingVM.melding.traadId))));
                item.add(new Label("traadlengde").setVisibilityAllowed(meldingVM.traadlengde > 2));
                item.add(new Label("avsenderTekst"));
                item.add(new StatusIkon("statusIkon", meldingVM));
                item.add(new Label("meldingstatus", new PropertyModel<String>(item.getModel(), "melding.statusTekst"))
                        .add(cssClass(meldingVM.melding.statusKlasse)));
                item.add(new Label("temagruppe", new PropertyModel<String>(item.getModel(), "melding.temagruppeNavn")));
                item.add(new Label("fritekst", new PropertyModel<String>(meldingVM, "melding.fritekst")));

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
