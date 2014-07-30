package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.Locale;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static org.apache.wicket.model.Model.of;

public class BehandlingsPanel extends Panel {

    public BehandlingsPanel(String id, Model<GenerellBehandling> behandlingModel) {
        super(id, behandlingModel);

        GenerellBehandling behandling = behandlingModel.getObject();
        String opprettetDato = behandling.opprettetDato.toString("d. MMMM yyyy, HH:mm", new Locale("nb", "no"));
        add(
                new Label("opprettet-dato", format(getString("behandling.opprettet"), opprettetDato)),
                lagAvsluttetDato(behandling)
        );
    }

    private Component lagAvsluttetDato(GenerellBehandling behandling) {
        return new Label("avsluttet-dato",
                format(getString("behandling.avsluttet"), behandling.behandlingDato.toString("d. MMMM yyyy, HH:mm", new Locale("nb", "no"))))
                .add(visibleIf(of(behandling.behandlingsStatus.equals(AVSLUTTET))));
    }

}
