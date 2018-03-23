package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import java.util.HashMap;
import java.util.Optional;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;

public class KontorsperreInfoPanel extends Panel {

    private final ReactComponentPanel reactComponent;
    private final InnboksVM innboksVM;


    public KontorsperreInfoPanel(String id, InnboksVM innboksVM) {
        super(id, new PropertyModel<>(innboksVM, "valgtTraad"));
        this.innboksVM = innboksVM;
        setOutputMarkupId(true);

        reactComponent = new ReactComponentPanel("enhet", "AlertStripeSuksessSolid", getProps());

        add(reactComponent);
        add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getValgtTraad().erKontorsperret();
            }
        }));
    }

    @RunOnEvents({TRAAD_MERKET, MELDING_VALGT})
    private void oppdaterReactComponent() {
        PropertyModel<Optional<String>> objectPropertyModel = new PropertyModel<>(getDefaultModel(), "kontorsperretEnhet");
        Optional<String> maybeEnhet = objectPropertyModel.getObject();
        if(maybeEnhet.isPresent()) {
            reactComponent.updateState(getProps());
        }
    }

    private HashMap<String, Object> getProps() {
        String kontorSperretTekst =
                new StringResourceModel("kontorsperreInfo.kontorsperretTekst", this, null, "Kontorsperret til enhet:").getString();
        String enhet = innboksVM.getValgtTraad().getKontorsperretEnhet().orElse("");
        String tekst =  kontorSperretTekst + " " + enhet;

        return new HashMap<String, Object>() {{
            put("tekst", tekst);
        }};
    }
}
