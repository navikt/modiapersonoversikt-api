package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.INNBOKS_OPPDATERT_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.ReactJournalforingsPanel.TRAAD_JOURNALFORT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;
import static org.apache.wicket.markup.head.OnDomReadyHeaderItem.forScript;

public class AlleMeldingerPanel extends Panel {

    public static final String TRAAD_ID_PREFIX = "allemeldingertraad-";
    public static final Behavior FORCE_FOCUS_BEHAVIOUR = new Behavior() {
        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            response.render(forScript(format(
                    "setTimeout(function(){$('#%s input[type=radio]:checked').focus();},100);",
                    component.getMarkupId()
            )));
        }
    };
    private InnboksVM innboksVM;

    public AlleMeldingerPanel(String id, final InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        setOutputMarkupId(true);

        this.innboksVM = innboksVM;

        RadioGroup<MeldingVM> alletraader = new RadioGroup<>("alletraader", new AbstractReadOnlyModel<MeldingVM>() {
            @Override
            public MeldingVM getObject() {
                return innboksVM.getValgtTraad().getNyesteMelding();
            }
        });

        initAlleTraader(innboksVM, alletraader);

        add(alletraader);
    }

    private MarkupContainer initAlleTraader(final InnboksVM innboksVM, RadioGroup<MeldingVM> alletraader) {
        return alletraader.add(new PropertyListView<MeldingVM>("nyesteMeldingerITraader") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                final MeldingVM meldingVM = item.getModelObject();
                item.setModel(new CompoundPropertyModel<>(meldingVM));

                item.add(new MeldingDetaljer("meldingDetaljer", innboksVM, meldingVM));

                final Radio<MeldingVM> radio = new Radio<>("meldingslistetraad", item.getModel());
                radio.setMarkupId("meldingslistetraad-" + meldingVM.melding.id);

                radio.add(hasCssClassIf("besvart", meldingVM.erBesvart()));

                item.setMarkupId(TRAAD_ID_PREFIX + meldingVM.melding.traadId);

                item.add(radio);

                item.add(hasCssClassIf("valgt", innboksVM.erValgtMelding(meldingVM)));
                if (innboksVM.getSessionHenvendelseId().isPresent() && innboksVM.erValgtMelding(meldingVM).getObject()) {
                    item.add(FORCE_FOCUS_BEHAVIOUR);
                }

                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        if (!meldingVM.melding.id.equals(innboksVM.getValgtTraad().getNyesteMelding().melding.id)) {
                            AlleMeldingerPanel.this.innboksVM.setValgtMelding(meldingVM);
                            send(getPage(), Broadcast.DEPTH, MELDING_VALGT);
                            target.add(AlleMeldingerPanel.this);
                        }
                        target.focusComponent(radio);
                        target.appendJavaScript("Meldinger.scrollToValgtMelding();");
                    }
                });
            }
        });
    }

    @RunOnEvents({Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER})
    public void oppdaterMeldingerEtterNyMelding(AjaxRequestTarget target) {
        innboksVM.setValgtMelding(innboksVM.getNyesteMeldingINyesteTraad());
        target.add(this);
    }

    @RunOnEvents({TRAAD_MERKET, TRAAD_JOURNALFORT, Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT})
    public void oppdaterMeldingerEtterMerkingEllerJournalforing(AjaxRequestTarget target, IEvent<?> event, Object payload) {
        //Object payload må være med i metodesignaturen for å tvinge modig-wicket til å populere IEvent med noe annet enn null.

        if (this.isVisibleInHierarchy()) {
            innboksVM.oppdaterMeldinger();
            if (innboksVM.harTraader()) {
                if (innboksVM.getValgtTraad() == null) {
                    innboksVM.setValgtMelding(innboksVM.getNyesteMeldingINyesteTraad());
                    send(getPage(), Broadcast.DEPTH, MELDING_VALGT);
                }
                if (!Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT.equals(event.getPayload())) {
                    target.appendJavaScript("Meldinger.focusOnSelectedElement();");
                }
            } else {
                innboksVM.setValgtMelding((MeldingVM) null);
            }
            send(getPage(), Broadcast.DEPTH, INNBOKS_OPPDATERT_EVENT);
            target.add(this);
        }
    }
}
