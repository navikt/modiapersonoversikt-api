package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.utils.WicketInjectablePropertyResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class LeggTilbakeDelvisSvarPanel extends Panel {

    public static final String WICKET_REACT_PANEL_ID = "reactleggtilbakedelvissvarpanel";
    public static final String WICKET_REACT_WRAPPER_ID = "leggtilbakedelvissvarpanel";
    public static final String REACT_ID = "LeggTilbakeDelvisSvarPanel";
    public static final String SVAR_DELVIS_CALLBACK_ID = "delvisSvarSendt";
    public static final String AVBRYT_CALLBACK_ID = "avbrytDelvisSvar";
    public static final String DEFAULT_SLIDE_DURATION = "400";

    private LeggTilbakeDelvisSvarProps leggTilbakeDelvisSvarProps;

    @Inject
    private WicketInjectablePropertyResolver wicketInjectablePropertyResolver;

    public LeggTilbakeDelvisSvarPanel(Melding sporsmal, String behandlingsId, GrunnInfo grunnInfo, final List<Melding> traad) {
        super(WICKET_REACT_WRAPPER_ID);
        setOutputMarkupPlaceholderTag(true);

        Map<Temagruppe, String> temagruppeMapping = Temagruppe.PLUKKBARE.stream()
                .collect(Collectors.toMap(
                        (temagruppeKode) -> temagruppeKode,
                        (temagruppeKode) -> wicketInjectablePropertyResolver.getProperty(temagruppeKode.name()),
                        (temagruppeKode, temagruppeNavn) -> temagruppeKode,
                        LinkedHashMap :: new
                        )
                );
        leggTilbakeDelvisSvarProps = new LeggTilbakeDelvisSvarProps(sporsmal, behandlingsId, temagruppeMapping, grunnInfo, traad);
        add(lagReactPanel());
    }

    private Component lagReactPanel() {
        ReactComponentPanel reactComponentPanel = new ReactComponentPanel(WICKET_REACT_PANEL_ID, REACT_ID, leggTilbakeDelvisSvarProps);
        reactComponentPanel.addCallback(SVAR_DELVIS_CALLBACK_ID, Void.class, (target, data) -> oppdaterMeldingerUI());
        reactComponentPanel.addCallback(AVBRYT_CALLBACK_ID, Void.class, (target, data) -> lukkDelvisSvarPanel(target));
        reactComponentPanel
                .setOutputMarkupId(true)
                .setVisibilityAllowed(true);

        return reactComponentPanel;
    }

    private void oppdaterMeldingerUI() {
        send(getPage(), BREADTH, new NamedEventPayload(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER));
    }

    private void lukkDelvisSvarPanel(AjaxRequestTarget target) {
        if (isVisibilityAllowed()) {
            this.setVisibilityAllowed(false);
            send(getPage(), BREADTH, AVBRYT_CALLBACK_ID);
            target.prependJavaScript(format("lukket|$('#%s').slideUp(" + DEFAULT_SLIDE_DURATION + ", lukket)", this.getMarkupId()));
            target.add(this);
        }
    }
}
