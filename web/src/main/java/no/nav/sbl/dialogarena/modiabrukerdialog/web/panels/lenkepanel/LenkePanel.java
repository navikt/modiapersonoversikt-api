package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.lenkepanel;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import javax.inject.Inject;

import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.WicketAutorizationUtils.accessRestriction;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml3;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LenkePanel extends Panel {
    public static final String SAKSBEHANDLERINNSTILLINGER_VALGT = "saksbehandlerinnstillinger.valgt";
    public static final String SAKSBEHANDLERINNSTILLINGER_TOGGLET = "saksbehandlerinnstillinger.togglet";
    public static final String OPPFOLGING_ACTION = "oppfolging";

    private final String VALGT_ENHET_PARAMETER = "?enhet=";
    private final String VEILARBPORTEFOLJEFLATE_BASE_URL = System.getProperty("server.veilarbportefoljeflatefs.url");

    private ExternalLink enhetlink;
    private ExternalLink veilederlink;
    private Label lenkeoverskrift;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private AnsattService ansattService;

    public LenkePanel(String id, String saksbehandlersValgteEnhet) {
        super(id);
        add(accessRestriction(RENDER).withAttributes(actionId(OPPFOLGING_ACTION), resourceId("")));
        addOppfolgingLink(saksbehandlersValgteEnhet);
        setOutputMarkupId(true);
    }

    private void addOppfolgingLink(String enhetId) {
        lenkeoverskrift = new Label("lenkeoverskrift", "ARBEIDSRETTET OPPFØLGING");
        add(lenkeoverskrift);
        enhetlink = (new ExternalLink("enhetLenke", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return unescapeHtml3(getEnhetUrl(enhetId));
            }
        }));
        veilederlink = (new ExternalLink("veilederLenke", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return unescapeHtml3(getPortefoljeUrl(enhetId));
            }
        }));

        add(enhetlink);
        add(veilederlink);

        if (isNotBlank(enhetId)) {
             makeLinkPanelVisible();
        } else {
            makeLinkPanelInvisible();
        }

    }

    private String getPortefoljeUrl(String enhetId) {
        String enhetQuery = VALGT_ENHET_PARAMETER + enhetId;
        return VEILARBPORTEFOLJEFLATE_BASE_URL + "/portefolje" + enhetQuery;
    }

    private String getEnhetUrl(String enhetId) {
        String enhetQuery = VALGT_ENHET_PARAMETER + enhetId;
        return VEILARBPORTEFOLJEFLATE_BASE_URL + "/enhet" + enhetQuery;
    }

    @RunOnEvents(SAKSBEHANDLERINNSTILLINGER_VALGT)
    private void updateValgtEnhet(AjaxRequestTarget target) {
        String enhetId = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();
        veilederlink.add(new AttributeModifier("href", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                return unescapeHtml3(getPortefoljeUrl(enhetId));
            }
        }));
        enhetlink.add(new AttributeModifier("href", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                return unescapeHtml3(getEnhetUrl(enhetId));
            }
        }));
        makeLinkPanelVisible();
        target.add(this);

    }

    @RunOnEvents(SAKSBEHANDLERINNSTILLINGER_TOGGLET)
    private void updatePorfolioLinks(AjaxRequestTarget target) {
        lenkeoverskrift.setVisible(!lenkeoverskrift.isVisible());
        enhetlink.setVisible(!enhetlink.isVisible());
        veilederlink.setVisible(!veilederlink.isVisible());
        target.add(this);

    }


    private void makeLinkPanelVisible() {
        lenkeoverskrift.setVisible(true);
        enhetlink.setVisible(true);
        veilederlink.setVisible(true);
    }


    private void makeLinkPanelInvisible() {
        lenkeoverskrift.setVisible(false);
        enhetlink.setVisible(false);
        veilederlink.setVisible(false);
    }

}
