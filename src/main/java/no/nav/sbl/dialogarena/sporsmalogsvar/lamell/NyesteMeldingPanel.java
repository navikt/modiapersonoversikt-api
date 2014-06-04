package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;


public class NyesteMeldingPanel extends Panel {

    AvsenderBilde avsenderbilde;
    IModel<InnboksVM> innboksVM;

    public NyesteMeldingPanel(String id, IModel<InnboksVM> innboksVM) {
        super(id);

        this.innboksVM = innboksVM;

        this.avsenderbilde = new AvsenderBilde("avsenderbilde", innboksVM.getObject().getNyesteMelding());
        add(avsenderbilde);
        add(new Label("nyesteMelding.opprettetDato"));
        add(new WebMarkupContainer("indikator-dot").add(new AttributeModifier("class", new PropertyModel<>(innboksVM, "nyesteMelding.statusKlasse"))));
        add(new Label("tema", new StringResourceModel("${nyesteMelding.melding.tema}", innboksVM)));
        add(new Label("nyesteMelding.traadlengde"));
        add(new URLParsingMultiLineLabel("nyesteMelding.melding.fritekst"));
    }

    @Override
    protected void onBeforeRender() {
        avsenderbilde.settBildeRessurs(innboksVM.getObject().getNyesteMelding());
        super.onBeforeRender();
    }
}
