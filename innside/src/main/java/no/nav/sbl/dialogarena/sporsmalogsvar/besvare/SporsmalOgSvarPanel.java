package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import javax.inject.Inject;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.SporsmalOgSvar;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class SporsmalOgSvarPanel extends Panel {

    @Inject
    private MeldingService service;
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
                model.setObject(plukk(aktorId));
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

    private SporsmalOgSvarVM plukk(String aktorId) {
        SporsmalOgSvar sporsmalOgSvar = service.plukkMelding(aktorId);
        if (sporsmalOgSvar == null) {
            info("Bruker har ingen ubesvarte spørsmål.");
            return new SporsmalOgSvarVM();
        }
        return new SporsmalOgSvarVM(new MeldingVM(sporsmalOgSvar.sporsmal), new SvarMeldingVM(sporsmalOgSvar.svar));
    }

    @RunOnEvents(BesvareSporsmalPanel.SPORSMAL_OPPDATERT)
    public void sporsmalAvbrutt(AjaxRequestTarget target) {
        target.add(this);
    }


}
