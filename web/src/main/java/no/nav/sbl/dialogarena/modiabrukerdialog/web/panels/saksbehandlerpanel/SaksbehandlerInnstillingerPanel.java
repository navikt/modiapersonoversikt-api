package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerTogglerPanel.SAKSBEHANDLERINNSTILLINGER_TOGGLET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.AnimasjonsUtils.animertVisningToggle;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml3;

public class SaksbehandlerInnstillingerPanel extends Panel {

    public static final String SAKSBEHANDLERINNSTILLINGER_VALGT = "saksbehandlerinnstillinger.valgt";

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private AnsattService ansattService;

    public String valgtEnhet;

    public SaksbehandlerInnstillingerPanel(String id) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        setVisibilityAllowed(
                ansattService.hentEnhetsliste().size() > 1 &&
                        saksbehandlerInnstillingerService.saksbehandlerInnstillingerErUtdatert());

        valgtEnhet = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();

        RadioGroup<String> gruppe = new RadioGroup<>("enhet", new PropertyModel<String>(this, "valgtEnhet"));
        gruppe.setRequired(true);

        gruppe.add(new PropertyListView<AnsattEnhet>("enhetsvalg", ansattService.hentEnhetsliste()) {
            protected void populateItem(ListItem<AnsattEnhet> item) {
                item.add(new Radio<>("enhetId"));
                item.add(new Label("enhetNavn"));
            }
        });

        final Form form = new Form<>("enhetsform");
        form.add(gruppe);

        form.add(new AjaxButton("velg") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                saksbehandlerInnstillingerService.setSaksbehandlerValgtEnhetCookie(valgtEnhet);
                send(getPage(), Broadcast.DEPTH, SAKSBEHANDLERINNSTILLINGER_VALGT);
                toggleSaksbehandlerPanel(target);
            }
        });

        oppdaterAriaLabel();

        add(form);
        add(new ExternalLink("opplaeringslenke", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return unescapeHtml3(getString("opplaeringslenke.href"));
            }
        }));
        add(new ExternalLink("skrivestotteForslagLenke", getProperty("modiabrukerdialog.standardtekster.tilbakemelding.url")));
    }

    public final void oppdaterAriaLabel() {
        add(AttributeModifier.replace("aria-expanded", isVisibilityAllowed()));
    }

    @RunOnEvents(SAKSBEHANDLERINNSTILLINGER_TOGGLET)
    private void toggleSaksbehandlerPanel(AjaxRequestTarget target) {
        animertVisningToggle(target, this);
        oppdaterAriaLabel();
        target.appendJavaScript("SaksbehandlerInnstillinger.focus();");
        target.add(this);
    }

}
