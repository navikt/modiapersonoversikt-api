package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import java.io.Serializable;

import static java.util.Arrays.asList;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.WicketAutorizationUtils.accessRestriction;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.HENVENDELSEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.OPPGAVEID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.*;
import static org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class PlukkOppgavePanel extends Panel {

    public static final String TEMAGRUPPE_ATTR = "sos-temagruppe";

    @Inject
    private PlukkOppgaveService plukkOppgaveService;

    private final IModel<Temagruppe> valgtTemagruppe;
    private final AriaFeedbackPanel feedbackPanel;
    private final Label velgtemagruppeKnapp;

    public PlukkOppgavePanel(String id) {
        super(id);

        valgtTemagruppe = new Model<>((Temagruppe) getSession().getAttribute(TEMAGRUPPE_ATTR));
        Form<Temagruppe> form = new Form<>("plukkOppgaveForm", valgtTemagruppe);
        form.setOutputMarkupId(true);

        add(accessRestriction(RENDER).withAttributes(actionId("plukkoppgave"), resourceId("")));

        feedbackPanel = new AriaFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);

        velgtemagruppeKnapp = new Label("velg-temagruppe-knapp");
        velgtemagruppeKnapp.setOutputMarkupId(true);

        RadioGroup radioGroup = new RadioGroup<>("temagruppe", valgtTemagruppe);
        radioGroup.setRenderBodyOnly(false);
        radioGroup.setRequired(true);
        radioGroup.setOutputMarkupPlaceholderTag(true);

        radioGroup.add(new ListView<Temagruppe>("temagrupper", asList(Temagruppe.values())) {
            @Override
            protected void populateItem(ListItem<Temagruppe> item) {
                item.add(new Radio<>("temagruppevalg", item.getModel()));
                item.add(new Label("temagruppenavn", new ResourceModel(item.getModelObject().name())));
            }
        });

        form.add(velgtemagruppeKnapp, new PlukkOppgaveKnapp("plukkOppgave"),
                radioGroup,
                new PlukkOppgaveKnapp("PlukkOppgaveFraTemaliste"),
                feedbackPanel);

        add(form);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(forReference(new JavaScriptResourceReference(PlukkOppgavePanel.class, "plukkoppgave.js")));
    }

    private class PlukkOppgaveKnapp extends AjaxButton {
        public PlukkOppgaveKnapp(String id) {
            super(id);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
            if (brukerHarEnAnnenPlukketOppgavePaaSession() && oppgavePaaSessionKanBehandles()) {
                redirectForAaBesvareOppgave(
                        getSession().getAttribute(VALGT_OPPGAVE_FNR_ATTR),
                        getSession().getAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR),
                        getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR)
                );
                return;
            }

            Optional<Oppgave> oppgave = plukkOppgaveService.plukkOppgave(valgtTemagruppe.getObject().name());
            if (oppgave.isSome()) {
                lagrePlukketOppgavePaaSession(oppgave.get());
                lagreValgtTemagruppePaaSession(valgtTemagruppe.getObject());
                redirectForAaBesvareOppgave(oppgave.get().fnr, oppgave.get().henvendelseId, oppgave.get().oppgaveId);
            } else {
                error(getString("plukkoppgave.ingenoppgaverpaatemagruppe"));
                target.add(feedbackPanel, PlukkOppgavePanel.this.velgtemagruppeKnapp);
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            target.add(feedbackPanel, PlukkOppgavePanel.this.velgtemagruppeKnapp);
            target.prependJavaScript("fokusPlukkOppgaveTemagruppe();");
        }

        private void redirectForAaBesvareOppgave(Serializable fnr, Serializable henvendelseid, Serializable oppgaveid) {
            setResponsePage(PersonPage.class,
                    new PageParameters()
                            .set("fnr", fnr)
                            .set(HENVENDELSEID, henvendelseid)
                            .set(OPPGAVEID, oppgaveid)
            );
        }

        private boolean oppgavePaaSessionKanBehandles() {
            if (plukkOppgaveService.oppgaveErFerdigstilt((String) getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR))) {
                getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, null);
                getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, null);
                getSession().setAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR, null);
                return false;
            }
            return true;
        }

        private boolean brukerHarEnAnnenPlukketOppgavePaaSession() {
            return getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR) != null && getSession().getAttribute(VALGT_OPPGAVE_FNR_ATTR) != null;
        }

        private void lagrePlukketOppgavePaaSession(Oppgave oppgave) {
            getSession().setAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR, oppgave.henvendelseId);
            getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, oppgave.oppgaveId);
            getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, oppgave.fnr);
        }

        private void lagreValgtTemagruppePaaSession(Temagruppe temagruppe) {
            getSession().setAttribute(TEMAGRUPPE_ATTR, temagruppe);
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            attributes.setEventPropagation(EventPropagation.BUBBLE);
        }
    }
}
