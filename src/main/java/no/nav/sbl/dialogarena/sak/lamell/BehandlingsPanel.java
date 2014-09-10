package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.service.BulletproofCmsService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sak.util.SakDateFormatter.printFullDate;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static org.apache.wicket.model.Model.of;

public class BehandlingsPanel extends Panel {

    @Inject
    private BulletproofCmsService cms;


    public BehandlingsPanel(String id, Model<GenerellBehandling> behandlingModel) {
        super(id, behandlingModel);

        GenerellBehandling behandling = behandlingModel.getObject();
        String opprettetDato = printFullDate(behandling.opprettetDato);
        String beskrivelsesKey = behandling.behandlingsStatus.equals(AVSLUTTET) ? "behandling.beskrivelse.avsluttet" : "behandling.beskrivelse.underArbeid";
        
        add(
                new Label("hendelse-beskrivelse", cms.hentTekst(beskrivelsesKey)),
                new Label("opprettet-dato", format(cms.hentTekst("behandling.opprettet.dato"), opprettetDato)),
                lagAvsluttetDato(behandling)
        );
    }

    private Component lagAvsluttetDato(GenerellBehandling behandling) {
        return new Label("avsluttet-dato", format(cms.hentTekst("behandling.avsluttet.dato"), printFullDate(behandling.behandlingDato)))
                .add(visibleIf(of(behandling.behandlingsStatus.equals(AVSLUTTET))));
    }

}
