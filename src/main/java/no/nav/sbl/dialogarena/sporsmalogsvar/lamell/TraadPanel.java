package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_SBL_INNGAAENDE;

public class TraadPanel extends Panel {
    public TraadPanel(String id, InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(innboksVM));

        add(new PropertyListView<MeldingVM>("valgtTraad.meldinger") {
            @Override
            protected void populateItem(ListItem<MeldingVM> item) {
                String meldingTypeKlasse = meldingKlasse(item.getModelObject());
                item.add(cssClass(meldingTypeKlasse));

                item.add(new JournalfortSkiller("journalfortSkiller", item.getModel()));
                item.add(new FeilsendtInfoPanel("feilsendtInfo", item.getModel()));
                item.add(new AvsenderBilde("avsenderBilde", item.getModel()).add(cssClass(meldingTypeKlasse)));
                item.add(new Label("temagruppe", new StringResourceModel("${temagruppeKey}", item.getModel())));
                item.add(new Label("meldingstatus", new StringResourceModel("${meldingStatusTekstKey}", item.getModel())));
                item.add(new Label("avsenderTekst"));
                item.add(new URLParsingMultiLineLabel("fritekst",
                        item.getModelObject().melding.fritekst != null ?
                                new PropertyModel(item.getModel(), "melding.fritekst") :
                                new ResourceModel("innhold.kassert")));
            }
        });
    }

    private String meldingKlasse(final MeldingVM meldingVM) {
        return asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE).contains(meldingVM.melding.meldingstype) ? "inngaaende" : "utgaaende";
    }
}
