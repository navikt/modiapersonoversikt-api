package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

public class SkrivestottePanel extends Panel {

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

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
        skrivestotte.callFunction(target, "vis");
    }

    public void oppdater(AjaxRequestTarget target) {
        skrivestotte.updateState(target, skrivestotteProps());
        target.add(skrivestotte);
    }

    public Map<String, Object> skrivestotteProps() {
        HashMap<String, Object> skrivestotteProps = new HashMap<>();
        skrivestotteProps.put("tekstfeltId", tekstfelt.get("text").getMarkupId());
        skrivestotteProps.put("autofullfor", grunnInfo);
        if (saksbehandlerInnstillingerService.valgtEnhetErKontaktsenter()) {
            skrivestotteProps.put("knagger", asList("ks"));
        }
        return skrivestotteProps;
    }
}
