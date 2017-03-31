package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.lenkepanel;

import no.nav.metrics.Timer;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.feedbackform.FeedbackLabel;
import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import java.io.Serializable;

import static no.nav.metrics.MetricsFactory.createTimer;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.WicketAutorizationUtils.accessRestriction;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerTogglerPanel.SAKSBEHANDLERINNSTILLINGER_TOGGLET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.AnimasjonsUtils.animertVisningToggle;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class LenkePanel extends Panel {

    ExternalLink enhetlink;
    ExternalLink veilederlink;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    public static final String SAKSBEHANDLERINNSTILLINGER_VALGT = "saksbehandlerinnstillinger.valgt";

    public LenkePanel(String id, boolean oppfolgingVisibility, String enhetNr) {
        super(id);
        addOppfolgingLink(oppfolgingVisibility, enhetNr);
        setOutputMarkupId(true);
    }

    public void addOppfolgingLink(boolean oppfolgingVisiblityLocal, String enhetNr) {
            enhetlink = (new ExternalLink("enhetLenke", "/veilarbportefoljeflatefs/enhet" + enhetNr));
            veilederlink = (new ExternalLink("veilederLenke", "/veilarbportefoljeflatefs/portefolje" + enhetNr));
            add(enhetlink);
            add(veilederlink);
            if (oppfolgingVisiblityLocal && isNotBlank(enhetNr)) {
                enhetlink.setVisible(true);
                veilederlink.setVisible(true);
            } else {
                enhetlink.setVisible(false);
                veilederlink.setVisible(false);
            }

    }

    @RunOnEvents(SAKSBEHANDLERINNSTILLINGER_VALGT)
    private void updateValgtEnhet(AjaxRequestTarget target) {
        enhetlink.setVisible(true);
        veilederlink.setVisible(true);
        String enhetNr = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();
        enhetlink = (new ExternalLink("enhetLenke", "/veilarbportefoljeflatefs/enhet" + enhetNr));
        veilederlink = (new ExternalLink("veilederLenke", "/veilarbportefoljeflatefs/portefolje" + enhetNr));
        target.add(this);
    }
}
