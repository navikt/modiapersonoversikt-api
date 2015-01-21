package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerTogglerPanel.SAKSBEHANDLERINNSTILLINGER_TOGGLET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.AnimasjonsUtils.animertVisningToggle;

public class SaksbehandlerInnstillingerPanel extends Panel {

    public static final String SAKSBEHANDLERINNSTILLINGER_VALGT = "saksbehandlerinnstillinger.valgt";

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Inject
    private AnsattService ansattService;

    @Inject
    private CmsContentRetriever cms;

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

        String href = StringEscapeUtils.unescapeHtml3(cms.hentTekst("opplaeringslenke.href"));

        Component opplaeringslenke = new Label("opplaeringslenke", cms.hentTekst("opplaeringslenke.tekst"))
                .add(new AttributeModifier("href", href));

        oppdaterAriaLabel();
        add(form, opplaeringslenke);
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
