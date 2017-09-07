package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import java.util.HashMap;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class KontorsperreInfoPanel extends Panel {

    public KontorsperreInfoPanel(String id, InnboksVM innboksVM) {
        super(id, new PropertyModel<>(innboksVM, "valgtTraad"));
        setOutputMarkupId(true);

        String kontorSperretTekst = new StringResourceModel("kontorsperreInfo.kontorsperretTekst", this, null, "Kontorsperret til enhet:").getString();

        PropertyModel<Object> objectPropertyModel = new PropertyModel<>(getDefaultModel(), "kontorsperretEnhet.get()");
        String enhet = (String) objectPropertyModel.getObject();

        String tekst = kontorSperretTekst + " " + enhet;
        add(new ReactComponentPanel("enhet", "AlertStripeSuksessSolid", new HashMap<String, Object>(){{
            put("tekst", tekst);
        }}));

        new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getValgtTraad().erKontorsperret();
            }
        };

        add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getValgtTraad().erKontorsperret();
            }
        }));
    }
}
