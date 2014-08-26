package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.SaksbehandlerInnstillingerService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInstillingerTogglerPanel.SAKSBEHANDLERINSTILLINGER_TOGGLET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.AnimasjonsUtils.animertVisningToggle;

public class SaksbehandlerInnstillingerPanel extends Panel {

    public static final String SAKSBEHANDLERINSTILLINGER_VALGT = "saksbehandlerinstillinger.valgt";

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public String valgtEnhet;

    public SaksbehandlerInnstillingerPanel(String id) {
        super(id);

        setOutputMarkupPlaceholderTag(true);
        setVisibilityAllowed(
                saksbehandlerInnstillingerService.hentEnhetsListe().size() > 1 &&
                        saksbehandlerInnstillingerService.saksbehandlerInstillingerErUtdatert());

        valgtEnhet = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();

        RadioGroup<String> gruppe = new RadioGroup<>("enhet", new PropertyModel<String>(this, "valgtEnhet"));
        gruppe.setRequired(true);

        gruppe.add(new ListView<AnsattEnhet>("enhetsvalg", saksbehandlerInnstillingerService.hentEnhetsListe()) {
            protected void populateItem(ListItem<AnsattEnhet> item) {
                item.add(new Radio<>("enhetId", Model.of(item.getModelObject().enhetId)));
                item.add(new Label("enhetNavn", item.getModelObject().enhetNavn));
            }
        });

        final Form form = new Form<>("enhetsform");
        form.add(gruppe);

        form.add(new AjaxButton("velg") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                saksbehandlerInnstillingerService.setSaksbehandlerValgtEnhetCookie(valgtEnhet);
                send(getPage(), Broadcast.DEPTH, SAKSBEHANDLERINSTILLINGER_VALGT);
                toggleSaksbehandlerPanel(target);
            }
        });

        add(form);
    }

    @RunOnEvents(SAKSBEHANDLERINSTILLINGER_TOGGLET)
    private void toggleSaksbehandlerPanel(AjaxRequestTarget target) {
        animertVisningToggle(target, this);
        target.add(this);
    }

}
