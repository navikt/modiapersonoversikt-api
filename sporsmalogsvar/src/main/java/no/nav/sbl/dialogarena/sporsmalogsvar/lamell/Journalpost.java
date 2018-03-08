package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.*;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class Journalpost extends Panel {

    private static final String SLIDE_DURATION = "100";

    public Journalpost(String id, IModel<MeldingVM> meldingVM) {
        super(id);

        PropertyModel<Boolean> meldingErJournalfort = new PropertyModel<>(meldingVM, "journalfort");
        PropertyModel<Boolean> meldingErDokumentMelding = new PropertyModel<>(meldingVM, "erDokumentMelding");
        PropertyModel<Boolean> meldingErOppgaveMelding = new PropertyModel<>(meldingVM, "erOppgaveMelding");

        WebMarkupContainer ingenJournalpostInformasjon = new WebMarkupContainer("ingenJournalpostInformasjon");
        ingenJournalpostInformasjon.setOutputMarkupPlaceholderTag(true);
        ingenJournalpostInformasjon.add(visibleIf(both(not(meldingErJournalfort)).and(not(meldingErDokumentMelding)).and(not(meldingErOppgaveMelding))));
        add(ingenJournalpostInformasjon);

        final JournalpostInformasjon journalpostInformasjon = new JournalpostInformasjon("journalpostInformasjon", meldingVM);
        IModel<Boolean> journalpostInformasjonErSynlig = new Model<Boolean>() {
            @Override
            public Boolean getObject() {
                return journalpostInformasjon.isVisibilityAllowed();
            }
        };

        AjaxLink<String> journalpostLenke = new AjaxLink<String>("aapneJournalpostInformasjon") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                journalpostInformasjon.togglePanel(target, SLIDE_DURATION);
                target.add(this);
            }
        };
        journalpostLenke.add(journalpostInformasjon);
        journalpostLenke.setOutputMarkupPlaceholderTag(true);
        journalpostLenke.add(visibleIf(meldingErJournalfort));
        journalpostLenke.add(
                new WebMarkupContainer("pilVisJournalfortinformasjon").add(
                        hasCssClassIf("opp", journalpostInformasjonErSynlig),
                        hasCssClassIf("ned", not(journalpostInformasjonErSynlig))));
        journalpostLenke.add(
                attributeIf("aria-expanded", "true", journalpostInformasjonErSynlig, true),
                attributeIf("aria-expanded", "false", not(journalpostInformasjonErSynlig), true));
        add(journalpostLenke);
    }

}
