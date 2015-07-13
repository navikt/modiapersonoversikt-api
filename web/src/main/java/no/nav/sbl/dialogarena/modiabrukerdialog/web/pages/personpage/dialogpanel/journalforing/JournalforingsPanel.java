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

    public JournalforingsPanel(String id, String fnr, final IModel<HenvendelseVM> henvendelseVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        WebMarkupContainer ingenSakValgt = new WebMarkupContainer("ingenSakValgt");
        ingenSakValgt.add(visibleIf(not(henvendelseVM.getObject().sakErSatt())));

        WebMarkupContainer sakValgt = new WebMarkupContainer("sakValgt");
        sakValgt.add(visibleIf(henvendelseVM.getObject().sakErSatt()));
        sakValgt.add(new Label("valgtSaksDatoFormatert"));
        sakValgt.add(new Label("valgtSak.temaNavn"));
        sakValgt.add(new Label("valgtSak.saksIdVisning"));

        hiddenField = new HiddenField<>("sak-validering", Model.of(""));

        hiddenField.setOutputMarkupId(true);
        hiddenField.setRequired(true);
        add(hiddenField);

        final AjaxLazyLoadVelgSakPanel velgSakPanel = new AjaxLazyLoadVelgSakPanel("velgSak", fnr, henvendelseVM);

        AjaxLink valgtSakLenke = new AjaxLink("valgtSakLenke") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                velgSakPanel.togglePanel(target);
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

    @RunOnEvents(SAK_VALGT)
    public void oppdaterPanel(AjaxRequestTarget target) {
        hiddenField.setModel(Model.of("valgt"));
        target.add(this);
    }

}
