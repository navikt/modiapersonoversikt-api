package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import java.util.HashMap;
import java.util.function.Supplier;

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

        add(visibleIf(arom(() -> innboksVM.getValgtTraad().erFerdigstiltUtenSvar())));
    }

    public static <T> AbstractReadOnlyModel<T> arom(Supplier<T> data) {
        return new AbstractReadOnlyModel<T>() {
            @Override
            public T getObject() {
                return data.get();
            }
        };
    }
}
