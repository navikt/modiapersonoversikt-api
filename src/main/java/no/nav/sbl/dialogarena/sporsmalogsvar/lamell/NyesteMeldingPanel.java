package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;


public class NyesteMeldingPanel extends Panel {

    AvsenderBilde avsenderbilde;
    final IModel<InnboksVM> innboksVM;

    public NyesteMeldingPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);

        this.innboksVM = innboksVM;
        final MeldingVM meldingVM = innboksVM.getObject().getValgtTraad().getNyesteMelding();

        this.avsenderbilde = new AvsenderBilde("avsenderbilde", meldingVM);
        add(avsenderbilde);
        add(new JournalfortSkiller("journalfortSkiller", new PropertyModel<MeldingVM>(innboksVM, "valgtTraad.nyesteMelding")));
        add(new Label("valgtTraad.nyesteMelding.opprettetDato"));
        add(new WebMarkupContainer("indikator-dot").add(cssClass(new PropertyModel<String>(innboksVM, "valgtTraad.nyesteMelding.statusKlasse"))));
        add(new Label("temagruppe", new ResourceModel(meldingVM.melding.temagruppe)));
        add(new Label("valgtTraad.nyesteMelding.traadlengde"));
        add(new Label("valgtTraad.nyesteMelding.melding.navIdent").add(visibleIf(
                both(not(isEqualTo(new PropertyModel<>(meldingVM, "melding.meldingstype"), Meldingstype.SPORSMAL)))
                        .and(not(isEmptyString(new PropertyModel<String>(meldingVM, "melding.navIdent"))))
        )));
        add(new URLParsingMultiLineLabel("valgtTraad.nyesteMelding.melding.fritekst"));
    }

    @Override
    protected void onBeforeRender() {
        avsenderbilde.settBildeRessurs(innboksVM.getObject().getValgtTraad().getNyesteMelding());
        super.onBeforeRender();
    }

}
