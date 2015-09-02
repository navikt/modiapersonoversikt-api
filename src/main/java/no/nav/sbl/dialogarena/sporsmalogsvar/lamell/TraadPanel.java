package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SVAR_SBL_INNGAAENDE;
import static org.apache.wicket.AttributeModifier.append;

public class TraadPanel extends Panel {

    public TraadPanel(String id, InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        add(new PropertyListView<MeldingVM>("valgtTraad.meldinger") {
            @Override
            protected void populateItem(ListItem<MeldingVM> item) {
                String meldingTypeKlasse = meldingKlasse(item.getModelObject());
                item.add(cssClass(meldingTypeKlasse));
                PropertyModel<String> lestStatusModel = new PropertyModel<>(item.getModel(), "melding.lestStatus");

                item.add(new FeilsendtInfoPanel("feilsendtInfo", item.getModel()));
                item.add(new AvsenderBilde("avsenderBilde", item.getModel()).add(cssClass(meldingTypeKlasse)));

                WebMarkupContainer meldingstatusContainer = new WebMarkupContainer("meldingstatusContainer");
                meldingstatusContainer.setOutputMarkupId(true);

                meldingstatusContainer.add(new Label("meldingstatus", new PropertyModel<String>(item.getModel(), "melding.statusTekst")));
                meldingstatusContainer.add(new Label("lestStatus", lestStatusModel).add(visibleIf(not(isEqualTo(lestStatusModel, "")))));
                meldingstatusContainer.add(new Label("temagruppe", new PropertyModel<String>(item.getModel(), "melding.temagruppeNavn")));

                item.add(meldingstatusContainer);
                item.add(new Label("avsenderDato"));
                item.add(new URLParsingMultiLineLabel("fritekst", new PropertyModel<String>(item.getModel(), "melding.fritekst")));
                item.add(new Journalpost("journalpost", item.getModel()));
                item.add(append("aria-labelledby", meldingstatusContainer.getMarkupId()));


                String navIdent = item.getModelObject().melding.navIdent;
                String skrevetAvNavn = item.getModelObject().melding.skrevetAv.navn;
                WebMarkupContainer skrevetAvContainer = new WebMarkupContainer("skrevetAvContainer");
                skrevetAvContainer.add(visibleIf(new PropertyModel<Boolean>(item.getModel(), "erFraSaksbehandler()")));
                skrevetAvContainer.add(new Label("skrevetAvLabel", new ResourceModel("melding.skrevet-av")));
                skrevetAvContainer.add(new Label("skrevetAv", String.format("%s (%s)", skrevetAvNavn, navIdent)));
                item.add(skrevetAvContainer);
            }
        });
    }

    private String meldingKlasse(final MeldingVM meldingVM) {
        return asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE).contains(meldingVM.melding.meldingstype) ? "inngaaende" : "utgaaende";
    }
}
