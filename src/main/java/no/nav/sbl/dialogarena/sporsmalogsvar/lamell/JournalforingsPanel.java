package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;

public class JournalforingsPanel extends Panel {

    @Inject
    private MeldingService meldingService;

    public JournalforingsPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final AjaxLink<InnboksVM> journalfor = new AjaxLink<InnboksVM>("journalforTraad") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                meldingService.journalforTraad(innboksVM.getObject().getValgtTraad());
                JournalforingsPanel.this.setVisibilityAllowed(false);
                target.add(JournalforingsPanel.this);
            }
        };

        final AjaxLink<InnboksVM> avbryt = new AjaxLink<InnboksVM>("avbrytJournalforing")
        {
            @Override
            public void onClick(AjaxRequestTarget target) {
                JournalforingsPanel.this.setVisibilityAllowed(false);
                target.add(JournalforingsPanel.this);
            }
        };

        add(journalfor, avbryt);
    }

}
