package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

public class SkrivestottePanel extends Panel {

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private UnleashService unleashService;

    private final ReactComponentPanel skrivestotte;
    private final GrunnInfo grunnInfo;
    private final EnhancedTextArea tekstfelt;

    public SkrivestottePanel(String id, GrunnInfo grunnInfo, EnhancedTextArea tekstfelt) {
        super(id);
        this.grunnInfo = grunnInfo;
        this.tekstfelt = tekstfelt;

        skrivestotte = new ReactComponentPanel("skrivestotte", "Skrivestotte", skrivestotteProps());
        add(skrivestotte);
    }

    public void vis(AjaxRequestTarget target) {
        skrivestotte.call("vis");
    }

    public void oppdater(AjaxRequestTarget target) {
        skrivestotte.updateState(skrivestotteProps());
        target.add(skrivestotte);
    }

    private Map<String, Object> skrivestotteProps() {
        HashMap<String, Object> skrivestotteProps = new HashMap<>();
        skrivestotteProps.put("tekstfeltId", tekstfelt.get("text").getMarkupId());
        skrivestotteProps.put("autofullfor", grunnInfo);
        if (saksbehandlerInnstillingerService.valgtEnhetErKontaktsenter()) {
            skrivestotteProps.put("knagger", asList("ks"));
        }
        Boolean isEnabled = unleashService.isEnabled(Feature.SVAKSYNT_MODUS);
        skrivestotteProps.put("svaksynt", isEnabled);
        return skrivestotteProps;
    }
}
