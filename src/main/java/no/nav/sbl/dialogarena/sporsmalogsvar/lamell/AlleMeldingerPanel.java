package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.model.StringFormatModel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.INNBOKS_OPPDATERT_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.AnimertJournalforingsPanel.TRAAD_JOURNALFORT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;

public class AlleMeldingerPanel extends Panel {

    public static final String TRAAD_ID_PREFIX = "allemeldingertraad-";
    private InnboksVM innboksVM;

    public AlleMeldingerPanel(String id, final InnboksVM innboksVM, final String traadDetaljerMarkupId) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        setOutputMarkupId(true);

        this.innboksVM = innboksVM;

        add(new PropertyListView<MeldingVM>("nyesteMeldingerITraad") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                final MeldingVM meldingVM = item.getModelObject();

                item.setMarkupId(TRAAD_ID_PREFIX + meldingVM.melding.traadId);

                item.add(new WebMarkupContainer("besvarIndikator").add(visibleIf(blirBesvart(meldingVM.melding.traadId))).setOutputMarkupPlaceholderTag(true));
                item.add(new Label("traadlengde").setVisibilityAllowed(meldingVM.traadlengde > 2));
                item.add(new Label("avsenderDato"));
                item.add(new StatusIkon("statusIkon",
                                blirBesvart(meldingVM.melding.traadId).getObject(),
                                innboksVM.erValgtMelding(meldingVM).getObject(),
                                meldingVM)
                );
                item.add(new Label("meldingstatus", new StringFormatModel("%s - %s",
                        new PropertyModel<String>(item.getModel(), "melding.statusTekst"),
                        new PropertyModel<String>(item.getModel(), "melding.temagruppeNavn")
                )));
                item.add(new Label("fritekst", new PropertyModel<String>(meldingVM, "melding.fritekst")));

                item.add(hasCssClassIf("valgt", innboksVM.erValgtMelding(meldingVM)));
                item.add(attributeIf("aria-selected", "true", innboksVM.erValgtMelding(meldingVM), true));
                item.add(attributeIf("aria-controls", traadDetaljerMarkupId, innboksVM.erValgtMelding(meldingVM), true));
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        if (!meldingVM.melding.id.equals(innboksVM.getValgtTraad().getNyesteMelding().melding.id)) {
                            AlleMeldingerPanel.this.innboksVM.setValgtMelding(meldingVM);
                            send(getPage(), Broadcast.DEPTH, MELDING_VALGT);
                            settFokusPaaValgtMelding(target);
                            target.add(AlleMeldingerPanel.this);
                            target.focusComponent(item);
                        }
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

    @RunOnEvents({Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER})
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
                    send(getPage(), Broadcast.DEPTH, MELDING_VALGT);
                }
                target.appendJavaScript("Meldinger.addKeyNavigation();");
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
