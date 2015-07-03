package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SVAR_SBL_INNGAAENDE;

public class TraadPanel extends Panel {

    @Inject
    private LDAPService ldapService;

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
                item.add(new Label("meldingstatus", new PropertyModel<String>(item.getModel(), "melding.statusTekst")));
                item.add(new Label("lestStatus", lestStatusModel)
                        .add(visibleIf(not(isEqualTo(lestStatusModel, "")))));

                item.add(new Label("temagruppe", new PropertyModel<String>(item.getModel(), "melding.temagruppeNavn")));
                item.add(new Label("avsenderTekst"));
                item.add(new URLParsingMultiLineLabel("fritekst", new PropertyModel<String>(item.getModel(), "melding.fritekst")));
                item.add(new Journalpost("journalpost", item.getModel()));


                String navIdent = item.getModelObject().melding.navIdent;
                String skrevetAvNavn = ldapService.hentSaksbehandler(navIdent).navn;
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
