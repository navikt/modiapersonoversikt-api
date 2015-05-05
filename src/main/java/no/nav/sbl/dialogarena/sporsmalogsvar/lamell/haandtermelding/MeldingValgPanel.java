package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RefreshOnEvents;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import static no.nav.modig.modia.aria.AriaHelpers.toggleButtonConnector;
import static no.nav.modig.wicket.conditional.ConditionalUtils.*;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel.PANEL_LUKKET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel.PANEL_TOGGLET;
import static org.apache.wicket.event.Broadcast.BREADTH;

@RefreshOnEvents({PANEL_LUKKET, VALGT_MELDING_EVENT})
public class MeldingValgPanel extends Panel {

    private final AnimertPanel tilknyttetPanel;

    public MeldingValgPanel(String id, IModel<Boolean> enabled, final AnimertPanel tilknyttetPanel) {
        super(id);
        setOutputMarkupId(true);

        this.tilknyttetPanel = tilknyttetPanel;
        IModel<Boolean> panelErSynlig = new PropertyModel<>(tilknyttetPanel, "visibilityAllowed");


        AjaxLink linkTekst = new AjaxLink("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onclick(target);
            }
        };
        linkTekst.add(new Label("linkTekst", new ResourceModel(id + ".linkTekst")));


        WebMarkupContainer pil = new WebMarkupContainer("pil");
        pil.add(hasCssClassIf("opp", panelErSynlig),
                hasCssClassIf("ned", not(panelErSynlig)),
                new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        onclick(target);
                    }
                });

        linkTekst.add(attributeIf("aria-disabled", "true", not(enabled)));
        toggleButtonConnector(linkTekst, tilknyttetPanel, panelErSynlig);

        add(linkTekst, pil);
        add(enabledIf(enabled), hasCssClassIf("inaktiv", not(enabled)));
    }

    private void onclick(AjaxRequestTarget target) {
        send(MeldingValgPanel.this.getParent(), BREADTH, new NamedEventPayload(PANEL_TOGGLET, tilknyttetPanel.getClass()));
        target.add(MeldingValgPanel.this);
    }
}
