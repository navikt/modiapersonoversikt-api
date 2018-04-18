package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;

import java.util.HashMap;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;

public class FerdigstiltUtenSvarPanel extends Panel {

    private final ReactComponentPanel reactComponent;
    private final InnboksVM innboksVM;

    public FerdigstiltUtenSvarPanel(String id, InnboksVM innboksVM) {
        super(id, new PropertyModel<>(innboksVM, "valgtTraad"));
        this.innboksVM = innboksVM;
        setOutputMarkupId(true);

        reactComponent = new ReactComponentPanel("ferdigstiltUtenSvar", "AlertStripeSuksessSolid", getProps());

        add(reactComponent);
        add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getValgtTraad().erFerdigstiltUtenSvar();
            }
        }));
    }

    private HashMap<String, Object> getProps() {
        String veilederNavn = innboksVM.getValgtTraad().getFerdigstiltUtenSvarAv()
                .map(saksbehandler -> saksbehandler.navn)
                .orElse("<navn mangler>");
        String veilederIdent = innboksVM.getValgtTraad().getFerdigstiltUtenSvarAv()
                .map(Saksbehandler::getIdent)
                .orElse("<ident mangler>");
        String ferdigstiltDato = innboksVM.getValgtTraad().getFerdigstiltUtenSvarDato()
                .map(DateUtils::toString)
                .orElse("<dato mangler>");
        return new HashMap<String, Object>() {{
            put("header", "Ferdigstilt uten svar");
            put("tekst", veilederNavn + " (" + veilederIdent + "), " + ferdigstiltDato);
        }};
    }

    @RunOnEvents({TRAAD_MERKET, MELDING_VALGT})
    private void oppdaterReactComponent() {
        PropertyModel<Boolean> objectPropertyModel = new PropertyModel<>(getDefaultModel(), "erFerdigstiltUtenSvar");
        if(objectPropertyModel.getObject()) {
            reactComponent.updateState(getProps());
        }
    }

}
