package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;


public class NyesteMeldingPanel extends Panel {

    AvsenderBilde avsenderbilde;
    IModel<InnboksVM> innboksVM;

    public NyesteMeldingPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);

        this.innboksVM = innboksVM;
        final MeldingVM meldingVM = innboksVM.getObject().getNyesteMeldingIValgtTraad();
        this.avsenderbilde = new AvsenderBilde("avsenderbilde", meldingVM);

        add(avsenderbilde);
        add(new Label("nyesteMeldingIValgtTraad.opprettetDato"));
        add(new WebMarkupContainer("indikator-dot").add(new AttributeModifier("class", new PropertyModel<>(innboksVM, "nyesteMeldingIValgtTraad.statusKlasse"))));
        add(new Label("tema", new StringResourceModel("${nyesteMeldingIValgtTraad.melding.tema}", innboksVM)));
        add(new Label("nyesteMeldingIValgtTraad.traadlengde"));
        add(new Label("nyesteMeldingIValgtTraad.melding.navIdent").add(enabledIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return meldingVM.melding.navIdent!= null && !meldingVM.melding.navIdent.isEmpty();
            }
        })));
        add(new URLParsingMultiLineLabel("nyesteMeldingIValgtTraad.melding.fritekst"));
    }

    @Override
    protected void onBeforeRender() {
        avsenderbilde.settBildeRessurs(innboksVM.getObject().getNyesteMeldingIValgtTraad());
        super.onBeforeRender();
    }

}
