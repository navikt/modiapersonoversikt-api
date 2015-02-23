package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static org.apache.wicket.AttributeModifier.append;

public class JournalforingsPanel extends Panel {

    public static final String SAK_VALGT = "events.local.journalforing.sak.valgt";

    public JournalforingsPanel(String id, String fnr, IModel<HenvendelseVM> henvendelseVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        WebMarkupContainer ingenSakValgt = new WebMarkupContainer("ingenSakValgt");
        ingenSakValgt.add(visibleIf(not(henvendelseVM.getObject().sakErSatt())));

        WebMarkupContainer sakValgt = new WebMarkupContainer("sakValgt");
        sakValgt.add(visibleIf(henvendelseVM.getObject().sakErSatt()));
        sakValgt.add(new Label("valgtSaksDatoFormatert"));
        sakValgt.add(new Label("valgtSak.temaNavn"));
        sakValgt.add(new Label("valgtSak.saksId"));

        final VelgSakPanel velgSakPanel = new VelgSakPanel("velgSak", fnr, henvendelseVM);

        AjaxLink valgtSakLenke = new AjaxLink("valgtSakLenke") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                velgSakPanel.togglePanel(target);
                target.add(this);
            }
        };
        valgtSakLenke.add(append("aria-controls", velgSakPanel.getMarkupId()));
        IModel<Boolean> velgSakPanelOpen = new Model<Boolean>() {
            @Override
            public Boolean getObject() {
                return velgSakPanel.isVisibilityAllowed();
            }
        };
        valgtSakLenke.add(ingenSakValgt, sakValgt, new PilOppNed("pilVelgSaker", valgtSakLenke, velgSakPanelOpen));

        add(valgtSakLenke, velgSakPanel);
    }

    @RunOnEvents(SAK_VALGT)
    public void oppdaterPanel(AjaxRequestTarget target) {
        target.add(this);
    }

}
