package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Dokument;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static org.apache.wicket.model.Model.of;

public class KvitteringsPanel extends Panel {

    public KvitteringsPanel(String id, Model<Kvittering> kvitteringsModel, String fnr) {
        super(id, kvitteringsModel);
        Kvittering kvittering = kvitteringsModel.getObject();

        int antallInnsendteVedlegg = kvittering.innsendteDokumenter.size();
        int totalAntallVedlegg = antallInnsendteVedlegg + kvittering.manglendeDokumenter.size();
        String dato = kvittering.behandlingDato.toString("d. MMMM yyyy, HH:mm", new Locale("nb", "no"));

        add(
                new Label("kvitteringsinfo-bottom", getString("kvittering.info.bottom")).setEscapeModelStrings(false),
                new Label("skjul-kvittering", getString("kvittering.skjul")),
                new Label("vis-kvittering", getString("kvittering.vis")),
                new Label("skrivut-lenke", getString("detaljer.skrivut")),
                new Label("sendt-inn", format(getString("kvittering.vedlegg.sendtInn.antall"), antallInnsendteVedlegg, totalAntallVedlegg, dato)),
                new Label("sendtAv", format(getString("kvittering.sendt.av"), fnr))
        );

        leggTilInnsendteVedlegg(kvittering);
        leggTilManglendeVedlegg(kvittering);

    }

    private void leggTilInnsendteVedlegg(Kvittering kvittering) {
        WebMarkupContainer innsendteVedlegg = new WebMarkupContainer("innsendteVedleggSection");
        innsendteVedlegg.add(visibleIf(of(!kvittering.innsendteDokumenter.isEmpty())));
        innsendteVedlegg.add(
                new Label("innsendteDokumenterHeader", getString("behandling.innsendte.dokumenter.header")),
                getDokumenterView("innsendteVedlegg", kvittering.innsendteDokumenter)
        );
        add(innsendteVedlegg);
    }

    private void leggTilManglendeVedlegg(Kvittering kvittering) {
        WebMarkupContainer manglendeVedlegg = new WebMarkupContainer("manglendeVedleggSection");
        manglendeVedlegg.add(visibleIf(of(!kvittering.manglendeDokumenter.isEmpty())));
        manglendeVedlegg.add(
                new Label("manglendeVedleggHeader", getString("behandling.manglende.dokumenter.header")),
                getDokumenterView("manglendeVedlegg", kvittering.manglendeDokumenter)
        );
        add(manglendeVedlegg);
    }

    private PropertyListView<Dokument> getDokumenterView(String listViewId, List<Dokument> dokumenter) {
        return new PropertyListView<Dokument>(listViewId, dokumenter) {

            @Override
            protected void populateItem(ListItem<Dokument> item) {
                final Dokument dokument = item.getModelObject();
                String tilleggsTittelString = "";
                if (dokument.tilleggstittel != null && !dokument.tilleggstittel.isEmpty()) {
                    tilleggsTittelString = ": " + dokument.tilleggstittel;
                }
                item.add(new Label("dokument", dokument.kodeverkRef + tilleggsTittelString));
            }
        };
    }

}
