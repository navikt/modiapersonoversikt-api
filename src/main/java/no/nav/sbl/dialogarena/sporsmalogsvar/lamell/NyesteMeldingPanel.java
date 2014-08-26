package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;


public class NyesteMeldingPanel extends Panel {

    AvsenderBilde avsenderbilde;

    public NyesteMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(new PropertyModel<MeldingVM>(innboksVM, "valgtTraad.nyesteMelding")));

        this.avsenderbilde = new AvsenderBilde("avsenderbilde", (MeldingVM) getDefaultModelObject());
        add(avsenderbilde);
        add(new JournalfortSkiller("journalfortSkiller", getDefaultModel()));
        add(new Label("opprettetDato"));
        add(new WebMarkupContainer("indikator-dot").add(cssClass(new PropertyModel<String>(getDefaultModel(), "statusKlasse"))));
        add(new Label("temagruppe", new StringResourceModel("${melding.temagruppe}", getDefaultModel())));
        add(new Label("traadlengde"));
        add(new Label("melding.navIdent").add(visibleIf(both(
                        not(isEqualTo(new PropertyModel<>(getDefaultModel(), "melding.meldingstype"), Meldingstype.SPORSMAL)))
                        .and(not(isEmptyString(new PropertyModel<String>(getDefaultModel(), "melding.navIdent"))))
        )));
        add(new URLParsingMultiLineLabel("melding.fritekst"));
    }

    @Override
    protected void onBeforeRender() {
        avsenderbilde.settBildeRessurs((MeldingVM) getDefaultModelObject());
        super.onBeforeRender();
    }

}
