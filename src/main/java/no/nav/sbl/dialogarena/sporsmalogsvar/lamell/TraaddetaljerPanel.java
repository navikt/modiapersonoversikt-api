package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RefreshOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.MeldingActionPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingValgPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.INNBOKS_OPPDATERT_EVENT;
import static org.apache.wicket.event.Broadcast.BREADTH;

@RefreshOnEvents({MELDING_VALGT, INNBOKS_OPPDATERT_EVENT})
public class TraaddetaljerPanel extends GenericPanel<InnboksVM> {

    public TraaddetaljerPanel(String id, final InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        setOutputMarkupId(true);

        MeldingActionPanel meldingActionPanel = new MeldingActionPanel("haandterMeldingPanel", innboksVM);

        add(meldingActionPanel);
        add(new HaandterMeldingValgPanel("haandterMelding", innboksVM, meldingActionPanel));
        add(new KontorsperreInfoPanel("kontorsperretInfo", innboksVM));
        add(new WebMarkupContainer("journalfortTemaContainer")
                .add(new Label("valgtTraad.eldsteMelding.melding.journalfortTemanavn"))
                .add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return innboksVM.getValgtTraad().getEldsteMelding().isJournalfort();
                    }
                })));
        add(new NyMeldingContainer("nyMelding"));
        add(new TraadPanel("traad", innboksVM));
    }

    private class NyMeldingContainer extends WebMarkupContainer {

        public NyMeldingContainer(String id) {
            super(id);
            setOutputMarkupPlaceholderTag(true);

            add(visibleIf(new PropertyModel<Boolean>(getModel(), "valgtTraad.traadKanBesvares()")));
            add(hasCssClassIf("besvares", traadBesvares()));

            add(new AjaxLink<InnboksVM>("besvar", TraaddetaljerPanel.this.getModel()) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    send(getPage(), BREADTH, new NamedEventPayload(Events.SporsmalOgSvar.SVAR_PAA_MELDING, getModelObject().getValgtTraad().getEldsteMelding().melding.id));
                }

                @Override
                protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                    attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.BUBBLE);
                }
            }.add(visibleIf(not(traadBesvares()))));
        }

        private AbstractReadOnlyModel<Boolean> traadBesvares() {
            return new AbstractReadOnlyModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return getModelObject().getValgtTraad().getEldsteMelding().melding.traadId.equals(getModelObject().traadBesvares);
                }
            };
        }
    }
}
