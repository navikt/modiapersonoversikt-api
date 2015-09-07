package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.modig.modia.aria.AriaHelpers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentCallback;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.HashMap;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class ReactJournalforingsPanel extends Panel {

    private final ReactComponentPanel reactComponentPanel;
    private final HiddenField hiddenField;
    private final IModel<HenvendelseVM> henvendelseVM;

    public ReactJournalforingsPanel(String id, final String fnr, final IModel<HenvendelseVM> henvendelseVM) {
        super(id);
        this.henvendelseVM = henvendelseVM;
        setOutputMarkupPlaceholderTag(true);

        reactComponentPanel = new ReactComponentPanel("reactjournalforing", "VelgSakPanel", new HashMap<String, Object>() {
            {
                put("fnr", fnr);
            }
        });
        reactComponentPanel
                .setOutputMarkupId(true)
                .setVisibilityAllowed(false);
        reactComponentPanel.addCallback("velgSak", Sak.class, new ReactComponentCallback<Sak>() {
            @Override
            public void onCallback(AjaxRequestTarget target, Sak sak) {
                henvendelseVM.getObject().valgtSak = sak;
                reactComponentPanel.setVisibilityAllowed(false);
                target.add(ReactJournalforingsPanel.this);
            }
        });

        WebMarkupContainer ingenSakValgt = new WebMarkupContainer("ingenSakValgt");
        ingenSakValgt.add(visibleIf(not(henvendelseVM.getObject().sakErSatt())));

        WebMarkupContainer sakValgt = new WebMarkupContainer("sakValgt");
        sakValgt.add(visibleIf(henvendelseVM.getObject().sakErSatt()));
        sakValgt.add(new Label("valgtSaksDatoFormatert"));
        sakValgt.add(new Label("valgtSak.temaNavn"));
        sakValgt.add(new Label("valgtSak.saksIdVisning"));

        hiddenField = new HiddenField<>("sak-validering", Model.of(""));
        oppdaterHiddenFelt();
        hiddenField.setOutputMarkupId(true);
        hiddenField.setRequired(true);
        add(hiddenField);


        AjaxLink valgtSakLenke = new AjaxLink("valgtSakLenke") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                reactComponentPanel.setVisibilityAllowed(!reactComponentPanel.isVisibleInHierarchy());
                oppdaterHiddenFelt();
                target.add(hiddenField);
                target.add(ReactJournalforingsPanel.this);
            }
        };
        IModel<Boolean> velgSakPanelOpen = new Model<Boolean>() {
            @Override
            public Boolean getObject() {
                return reactComponentPanel.isVisibilityAllowed();
            }
        };
        valgtSakLenke.add(ingenSakValgt, sakValgt);
        valgtSakLenke.add(new PilOppNed("pilVelgSaker", velgSakPanelOpen));


        AriaHelpers.toggleButtonConnector(valgtSakLenke, reactComponentPanel, velgSakPanelOpen);

        add(reactComponentPanel, valgtSakLenke);
    }

    private void oppdaterHiddenFelt() {
        if (henvendelseVM.getObject().valgtSak != null || reactComponentPanel.isVisibilityAllowed()) {
            hiddenField.setModel(Model.of("valgt"));
        } else {
            hiddenField.setModel(Model.of(""));
        }
    }

}
