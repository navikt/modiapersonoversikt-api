package no.nav.sbl.dialogarena.sporsmalogsvar.common.components;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.apache.wicket.markup.html.WebMarkupContainer;

import static no.nav.modig.wicket.conditional.ConditionalUtils.attributeIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class StatusIkon extends WebMarkupContainer {

    private MeldingVM meldingVM;

    public StatusIkon(String id, MeldingVM meldingVM) {
        super(id);
        this.meldingVM = meldingVM;

        add(hasCssClassIf("ubesvart", not(meldingVM.erBesvart())));
        add(hasCssClassIf("besvart", meldingVM.erBesvart()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(attributeIf("aria-label", getString("innboks.melding.ubesvart"), not(meldingVM.erBesvart())));
        add(attributeIf("aria-label", getString("innboks.melding.besvart"), meldingVM.erBesvart()));
    }
}
