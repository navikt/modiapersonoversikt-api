package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.LinkedHashMap;
import java.util.Map;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class SporsmalOgSvarPanel extends Panel {

    private final BesvareModell model;

    public SporsmalOgSvarPanel(String id, final String aktorId) {
        super(id);
        setOutputMarkupId(true);
        add(new AttributeAppender("class", "besvare-panel"));

        FeedbackPanel feedbackPanel = new FeedbackPanel("feedback-panel");
        feedbackPanel.setOutputMarkupId(true);
        model = new BesvareModell();
        final BesvareSporsmalPanel besvare = new BesvareSporsmalPanel("besvare-sporsmal", model, feedbackPanel);
        besvare.add(visibleIf(model.besvarerSporsmal()));

        AjaxLink<Void> plukk = new AjaxLink<Void>("plukk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(SporsmalOgSvarPanel.this);
            }
        };
        plukk.add(visibleIf(not(model.besvarerSporsmal())));

        add(
                feedbackPanel,
                plukk,
                besvare,
                new AjaxLink<Void>("brukerhenvendelser-link") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(InternalEvents.FEED_ITEM_CLICKED, new FeedItemPayload(null, null, "brukerhenvendelser")));
                    }
                });
    }

    @SuppressWarnings("unused")
    @RunOnEvents(BesvareSporsmalPanel.SPORSMAL_OPPDATERT)
    public void sporsmalAvbrutt(AjaxRequestTarget target) {
        target.add(this);
    }


}
