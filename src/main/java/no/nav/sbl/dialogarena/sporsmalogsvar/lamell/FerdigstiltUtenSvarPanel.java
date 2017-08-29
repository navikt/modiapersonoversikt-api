package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import java.util.HashMap;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class FerdigstiltUtenSvarPanel extends Panel {

    public FerdigstiltUtenSvarPanel(String id, InnboksVM innboksVM) {
        super(id, new PropertyModel<>(innboksVM, "valgtTraad"));
        setOutputMarkupId(true);

        String tekst = new StringResourceModel("ferdigstiltUtenSvar.ferdigstiltTekst", this, null, "Samtalen er avsluttet uten å svare bruker").getString();

        add(new ReactComponentPanel("ferdigstiltUtenSvar", "AlertStripeSuksessSolid", new HashMap<String, Object>(){{
            put("tekst", tekst);
        }}));

        new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getValgtTraad().erFerdigstiltUtenSvar();
            }
        };

        add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getValgtTraad().erFerdigstiltUtenSvar();
            }
        }));
    }
}
