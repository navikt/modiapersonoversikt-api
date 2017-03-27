package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.lenkepanel;

import no.nav.metrics.Timer;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.feedbackform.FeedbackLabel;
import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
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
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class LenkePanel extends Panel {

    public LenkePanel(String id, boolean oppfolgingVisibility, String enhetNr) {
        super(id);
        boolean oppfolgingVisiblityLocal = oppfolgingVisibility;
        addOppfolgingLink(oppfolgingVisiblityLocal, enhetNr);
    }

    private void addOppfolgingLink(boolean oppfolgingVisiblityLocal, String enhetNr) {
        ExternalLink enhetlink = (new ExternalLink("enhetLenke", "/veilarbportefoljeflatefs/enhet/" + enhetNr));
        ExternalLink veilederlink = (new ExternalLink("veilederLenke", "/veilarbportefoljeflatefs/portefolje/"  + enhetNr));
        add(enhetlink);
        add(veilederlink);
        if (oppfolgingVisiblityLocal) {
            enhetlink.setVisible(true);
            veilederlink.setVisible(true);
        } else {
            enhetlink.setVisible(false);
            veilederlink.setVisible(false);
        }
    }

}
