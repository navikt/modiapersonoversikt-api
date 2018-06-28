package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.util.HashMap;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class FeilsendtInfoPanel extends Panel {

    public FeilsendtInfoPanel(String id, IModel<MeldingVM> meldingVM) {
        super(id, meldingVM);
        setOutputMarkupId(true);

        String veilederIdent = meldingVM.getObject().getMarkertSomFeilsendtAv()
                .map(Saksbehandler::getIdent)
                .orElse("<ident mangler>");
        String veilederNavn = meldingVM.getObject().getMarkertSomFeilsendtAv()
                .map(person -> person.navn)
                .orElse("<navn mangler>");
        String markertDato = meldingVM.getObject().getMarkertSomFeilsendtDato()
                .map(DateUtils::toString)
                .orElse("<dato mangler>");

        add(new ReactComponentPanel("markertAv", "AlertStripeSuksessSolid", new HashMap<String, Object>(){{
            put("header", "Feilsendt post");
            put("tekst", veilederNavn + " (" + veilederIdent + "), " + markertDato);
        }}));

        add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return meldingVM.getObject().erFeilsendt();
            }
        }));
    }
}
