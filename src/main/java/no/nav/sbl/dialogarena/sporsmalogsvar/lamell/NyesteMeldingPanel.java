package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;


public class NyesteMeldingPanel extends Panel {

    AvsenderBilde avsenderbilde;
    IModel<InnboksVM> innboksVM;

    public NyesteMeldingPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);

        this.innboksVM = innboksVM;
        final MeldingVM meldingVM = innboksVM.getObject().getValgtTraad().getNyesteMelding();

        add(new JournalfortSkiller("journalfortSkiller", new Model<MeldingVM>(){
            @Override
            public MeldingVM getObject() {
                return innboksVM.getObject().getValgtTraad().getNyesteMelding();
            }
        }));
        this.avsenderbilde = new AvsenderBilde("avsenderbilde", meldingVM);
        add(avsenderbilde);
        add(new Label("valgtTraad.nyesteMelding.opprettetDato"));
        add(new WebMarkupContainer("indikator-dot").add(new AttributeModifier("class", new PropertyModel<>(innboksVM, "valgtTraad.nyesteMelding.statusKlasse"))));
        add(new Label("temagruppe", new StringResourceModel("${valgtTraad.nyesteMelding.melding.temagruppe}", innboksVM)));
        add(new Label("valgtTraad.nyesteMelding.traadlengde"));
        add(new Label("valgtTraad.nyesteMelding.melding.navIdent").add(enabledIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return !meldingVM.melding.meldingstype.equals(Meldingstype.SPORSMAL) && meldingVM.melding.navIdent!= null && !meldingVM.melding.navIdent.isEmpty();
            }
        })));
        add(new URLParsingMultiLineLabel("valgtTraad.nyesteMelding.melding.fritekst"));
    }

    @Override
    protected void onBeforeRender() {
        avsenderbilde.settBildeRessurs(innboksVM.getObject().getValgtTraad().getNyesteMelding());
        super.onBeforeRender();
    }

}
