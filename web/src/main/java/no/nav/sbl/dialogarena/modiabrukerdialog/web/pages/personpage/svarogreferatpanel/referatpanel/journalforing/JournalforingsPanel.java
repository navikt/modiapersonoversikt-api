package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel.journalforing;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.HenvendelseVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class JournalforingsPanel extends Panel {

    public static final String JOURNALFORING_VELG_SAK_PANEL_TOGGLET = "events.local.journalforing.velg.sak.panel.togglet";

    private final JournalforingsPanelVelgSak velgSakPanel;
    private final WebMarkupContainer valgtSakContainer;

    public JournalforingsPanel(String id, String fnr, IModel<HenvendelseVM> henvendelseVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        velgSakPanel = new JournalforingsPanelVelgSak("velgSak", fnr, henvendelseVM);

        valgtSakContainer = new WebMarkupContainer("valgtSakContainer");
        valgtSakContainer.setOutputMarkupPlaceholderTag(true);

        AjaxLink valgtSakLenke = new AjaxLink("valgtSakLenke") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                velgSakPanel.togglePanel(target);
                toggleValgtSak(target);
            }
        };

        valgtSakContainer.add(valgtSakLenke);
        add(valgtSakContainer, velgSakPanel);
    }

    @RunOnEvents({JOURNALFORING_VELG_SAK_PANEL_TOGGLET})
    public void toggleValgtSak(AjaxRequestTarget target) {
        valgtSakContainer.setVisibilityAllowed(!valgtSakContainer.isVisibilityAllowed());
        target.add(valgtSakContainer);
    }

}
