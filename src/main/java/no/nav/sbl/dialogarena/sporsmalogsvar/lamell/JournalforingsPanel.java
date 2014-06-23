package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Sak;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
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
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.List;

public class JournalforingsPanel extends Panel {

    @Inject
    private MeldingService meldingService;

    final IModel<Sak> valgtSak;

    public JournalforingsPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        List<Sak> saker = meldingService.hentSakerForBruker(innboksVM.getObject().getFnr());

        valgtSak = new Model<>();

        Form<Sak> form = new Form<>("plukkSakForm", valgtSak);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);

        RadioGroup radioGroup = new RadioGroup<>("sakRadiogruppe", valgtSak);
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

        AjaxSubmitLink journalforTraad = new AjaxSubmitLink("journalforTraad") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
               Sak sak = valgtSak.getObject();
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

    private void lukkJournalforingsPanel(AjaxRequestTarget target) {
        this.setVisibilityAllowed(false);
        target.add(this);
    }

}
