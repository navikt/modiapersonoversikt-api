package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RefreshOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel.PANEL_LUKKET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel.PANEL_TOGGLET;
import static org.apache.wicket.event.Broadcast.BREADTH;

@RefreshOnEvents({PANEL_LUKKET, VALGT_MELDING_EVENT})
public class MeldingValgPanel extends Panel {

    public MeldingValgPanel(String id, IModel<Boolean> enabled, final AnimertPanel tilknyttetPanel) {
        super(id);
        setOutputMarkupId(true);

        add(
                enabledIf(enabled),
                hasCssClassIf("inaktiv", not(enabled))
        );

        add(
                new AjaxLink("link") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        send(MeldingValgPanel.this.getParent(), BREADTH, new NamedEventPayload(PANEL_TOGGLET, tilknyttetPanel.getClass()));
                        target.add(MeldingValgPanel.this);
                    }
                }.add(new Label("linkTekst", new ResourceModel(id + ".linkTekst")))
        );

        WebMarkupContainer pil = new WebMarkupContainer("pil");
        IModel<Boolean> panelErSynlig = new PropertyModel<>(tilknyttetPanel, "visibilityAllowed");
        pil.add(
                hasCssClassIf("opp", panelErSynlig),
                hasCssClassIf("ned", not(panelErSynlig))
        );

        add(pil);
    }
}
