package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.Locale;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static org.apache.wicket.model.Model.of;

public class BehandlingsPanel extends Panel {

    @Inject
    private CmsContentRetriever cmsContentRetriever;


    public BehandlingsPanel(String id, Model<GenerellBehandling> behandlingModel) {
        super(id, behandlingModel);

        GenerellBehandling behandling = behandlingModel.getObject();
        String opprettetDato = behandling.opprettetDato.toString("d. MMMM yyyy, HH:mm", new Locale("nb", "no"));
        add(
                new Label("hendelse-beskrivelse", cmsContentRetriever.hentTekst("behandling.beskrivelse")),
                new Label("opprettet-dato", format(cmsContentRetriever.hentTekst("behandling.opprettet.dato"), opprettetDato)),
                lagAvsluttetDato(behandling)
        );
    }

    private Component lagAvsluttetDato(GenerellBehandling behandling) {
        return new Label("avsluttet-dato",
                format(cmsContentRetriever.hentTekst("behandling.avsluttet.dato"), behandling.behandlingDato.toString("d. MMMM yyyy, HH:mm", new Locale("nb", "no"))))
                .add(visibleIf(of(behandling.behandlingsStatus.equals(AVSLUTTET))));
    }

}
