package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static org.apache.wicket.AttributeModifier.append;

public class TraadPanel extends Panel {

    public TraadPanel(String id, InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        add(new PropertyListView<MeldingVM>("valgtTraad.meldinger") {
            @Override
            protected void populateItem(ListItem<MeldingVM> item) {
                leggTilCSSKlasser(item);

                PropertyModel<String> lestStatusModel = new PropertyModel<>(item.getModel(), "melding.lestStatus");
                item.add(new FeilsendtInfoPanel("feilsendtInfo", item.getModel()));

                WebMarkupContainer meldingstatusContainer = new WebMarkupContainer("meldingstatusContainer");
                meldingstatusContainer.setOutputMarkupId(true);

                meldingstatusContainer.add(new Label("meldingstatus", new PropertyModel<String>(item.getModel(), "melding.statusTekst")));
                meldingstatusContainer.add(new Label("lestStatus", lestStatusModel).add(visibleIf(not(isEqualTo(lestStatusModel, "")))));
                meldingstatusContainer.add(
                        new Label("temagruppe", new PropertyModel<String>(item.getModel(), "melding.temagruppeNavn"))
                                .setVisibilityAllowed(!item.getModelObject().erDokumentMelding && !item.getModelObject().erOppgaveMelding)
                );

                item.add(meldingstatusContainer);
                item.add(new Label("visningsDato"));
                item.add(new URLParsingMultiLineLabel("fritekst", getFritekst(item.getModelObject().melding)));
                item.add(new Journalpost("journalpost", item.getModel()));
                item.add(append("aria-labelledby", meldingstatusContainer.getMarkupId()));

                WebMarkupContainer skrevetAvContainer = new WebMarkupContainer("skrevetAvContainer");
                skrevetAvContainer.add(visibleIf(new PropertyModel<>(item.getModel(), "erFraSaksbehandler()")));
                skrevetAvContainer.add(new Label("skrevetAvLabel", new ResourceModel("melding.skrevet-av")));
                skrevetAvContainer.add(new Label("skrevetAv", getSkrevetAv(item.getModelObject().melding)));
                skrevetAvContainer.setVisible(!item.getModelObject().erDokumentMelding && !item.getModelObject().erOppgaveMelding);
                item.add(skrevetAvContainer);
            }

            private void leggTilCSSKlasser(ListItem<MeldingVM> item) {
                leggTilCSSKlasserForMeldingstype(item);

                MeldingVM meldingVm = item.getModelObject();
                if (meldingVm.erDelsvar()) {
                    item.add(cssClass("delsvar"));
                }
            }

            private void leggTilCSSKlasserForMeldingstype(ListItem<MeldingVM> item) {
                MeldingVM meldingVm = item.getModelObject();
                String meldingTypeKlasse = meldingKlasse(meldingVm);
                item.add(cssClass(meldingTypeKlasse));
                item.add(new AvsenderBilde("avsenderBilde", item.getModel()).add(cssClass(meldingTypeKlasse)));
            }

            private String getFritekst(Melding melding) {
                String linjeskift = "\n\u00A0\n";
                return melding.getFriteksterMedEldsteForst().stream()
                        .map(Fritekst::getFritekst)
                        .collect(Collectors.joining(linjeskift));
            }

            private String getSkrevetAv(Melding melding) {
                return melding.getFriteksterMedEldsteForst().stream()
                        .map(this::formaterSaksbehandlersNavnOgIdent)
                        .collect(Collectors.joining(" og "));
            }

            private String formaterSaksbehandlersNavnOgIdent(Fritekst fritekst) {
                return fritekst.getSaksbehandler()
                        .map(saksbehandler -> String.format("%s (%s)", saksbehandler.navn, saksbehandler.getIdent()))
                        .orElse("Ukjent");
            }
        });
    }

    private String meldingKlasse(final MeldingVM meldingVM) {
        return asList(SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG_DIREKTE, SVAR_SBL_INNGAAENDE)
                .contains(meldingVM.melding.meldingstype) ? "inngaaende" : "utgaaende";
    }
}
