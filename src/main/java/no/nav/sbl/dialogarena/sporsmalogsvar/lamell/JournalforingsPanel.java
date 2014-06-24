package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Sak;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class JournalforingsPanel extends Panel {

    @Inject
    private MeldingService meldingService;

    public JournalforingsPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        List<Sak> saker = meldingService.hentSakerForBruker(innboksVM.getObject().getFnr());

        Form<Sak> form = new Form<>("plukkSakForm", innboksVM.getObject().getValgtTraad().valgtSak);

        RadioGroup radioGroup = new RadioGroup<>("sakRadiogruppe", form.getModel());
        radioGroup.setRequired(true);
        radioGroup.add(new ListView<Sak>("saker", saker) {
            @Override
            protected void populateItem(ListItem<Sak> item) {
                item.add(new Radio<>("sak", item.getModel()));
                Sak sak = item.getModelObject();
                item.add(new Label("saksTema", sak.tema));
                item.add(new Label("saksId", sak.saksId));
                item.add(new Label("opprettetDato", sak.opprettetDato));
            }
        });

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);

        AjaxSubmitLink journalforTraad = new AjaxSubmitLink("journalforTraad") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Sak sak = innboksVM.getObject().getValgtTraad().valgtSak.getObject();
                meldingService.journalforTraad(innboksVM.getObject().getValgtTraad(), sak);
               lukkJournalforingsPanel(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };

        form.add(feedbackPanel, radioGroup, journalforTraad);

        final AjaxLink<InnboksVM> avbryt = new AjaxLink<InnboksVM>("avbrytJournalforing")
        {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkJournalforingsPanel(target);
            }
        };

        add(form, avbryt);
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        lukkJournalforingsPanel(target);
    }

    private void lukkJournalforingsPanel(AjaxRequestTarget target) {
        this.setVisibilityAllowed(false);
        target.add(this);
    }

}
