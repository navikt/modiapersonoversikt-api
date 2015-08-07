package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.modig.modia.aria.AriaHelpers;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class JournalforingsPanel extends Panel {

    public static final String SAK_VALGT = "events.local.journalforing.sak.valgt";
    private final HiddenField hiddenField;
    private final IModel<HenvendelseVM> henvendelseVM;
    private final AjaxLazyLoadVelgSakPanel velgSakPanel;

    public JournalforingsPanel(String id, String fnr, final IModel<HenvendelseVM> henvendelseVM, boolean visSosialeTjenester) {
        super(id);
        this.henvendelseVM = henvendelseVM;
        setOutputMarkupPlaceholderTag(true);

        WebMarkupContainer ingenSakValgt = new WebMarkupContainer("ingenSakValgt");
        ingenSakValgt.add(visibleIf(not(henvendelseVM.getObject().sakErSatt())));

        WebMarkupContainer sakValgt = new WebMarkupContainer("sakValgt");
        sakValgt.add(visibleIf(henvendelseVM.getObject().sakErSatt()));
        sakValgt.add(new Label("valgtSaksDatoFormatert"));
        sakValgt.add(new Label("valgtSak.temaNavn"));
        sakValgt.add(new Label("valgtSak.saksIdVisning"));
        velgSakPanel = new AjaxLazyLoadVelgSakPanel("velgSak", fnr, henvendelseVM, visSosialeTjenester);

        hiddenField = new HiddenField<>("sak-validering", Model.of(""));
        oppdaterHiddenFelt();
        hiddenField.setOutputMarkupId(true);
        hiddenField.setRequired(true);
        add(hiddenField);


        AjaxLink valgtSakLenke = new AjaxLink("valgtSakLenke") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                velgSakPanel.togglePanel(target);
                oppdaterHiddenFelt();
                target.add(hiddenField);
                target.add(this);
            }
        };
        velgSakPanel.settFokusEtterLukking(valgtSakLenke.getMarkupId());
        IModel<Boolean> velgSakPanelOpen = new Model<Boolean>() {
            @Override
            public Boolean getObject() {
                return velgSakPanel.isVisibilityAllowed();
            }
        };
        valgtSakLenke.add(ingenSakValgt, sakValgt);
        valgtSakLenke.add(new PilOppNed("pilVelgSaker", velgSakPanelOpen));


        AriaHelpers.toggleButtonConnector(valgtSakLenke, velgSakPanel, velgSakPanelOpen);

        add(valgtSakLenke, velgSakPanel);
    }

    private void oppdaterHiddenFelt() {
        if (henvendelseVM.getObject().valgtSak != null || velgSakPanel.isVisibilityAllowed()) {
            hiddenField.setModel(Model.of("valgt"));
        } else {
            hiddenField.setModel(Model.of(""));
        }
    }

    @RunOnEvents(SAK_VALGT)
    public void oppdaterPanel(AjaxRequestTarget target) {
        oppdaterHiddenFelt();
        target.add(this);
    }

}
