package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;

import java.util.HashMap;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class FerdigstiltUtenSvarPanel extends Panel {

    public FerdigstiltUtenSvarPanel(String id, InnboksVM innboksVM) {
        super(id, new PropertyModel<>(innboksVM, "valgtTraad"));
        setOutputMarkupId(true);

        String veilederNavn = innboksVM.getValgtTraad().getFerdigstiltUtenSvarAv()
                .map(saksbehandler -> saksbehandler.navn)
                .orElse("<navn mangler>");
        String veilederIdent = innboksVM.getValgtTraad().getFerdigstiltUtenSvarAv()
                .map(Saksbehandler::getIdent)
                .orElse("<ident mangler>");
        String ferdigstiltDato = innboksVM.getValgtTraad().getFerdigstiltUtenSvarDato()
                .map(DateUtils::toString)
                .orElse("<dato mangler>");

        add(new ReactComponentPanel("ferdigstiltUtenSvar", "AlertStripeSuksessSolid", new HashMap<String, Object>(){{
            put("tekst", "Ferdigstilt uten svar av " + veilederNavn + " (" + veilederIdent + "), " + ferdigstiltDato);
        }}));

        add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getValgtTraad().erFerdigstiltUtenSvar();
            }
        }));
    }
}
