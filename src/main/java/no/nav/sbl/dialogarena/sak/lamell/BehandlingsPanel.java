package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.service.BulletproofCmsService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

import static no.nav.modig.modia.widget.utils.WidgetDateFormatter.date;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static org.apache.wicket.model.Model.of;

public class BehandlingsPanel extends Panel {

    @Inject
    private BulletproofCmsService cms;


    public BehandlingsPanel(String id, String tittel, Model<GenerellBehandling> behandlingModel, String sakstemakode) {
        super(id, behandlingModel);

        GenerellBehandling behandling = behandlingModel.getObject();
        String opprettetDato = date(behandling.opprettetDato);
        String avsluttetDato = date(behandling.behandlingDato);

        boolean erAvsluttet = behandling.behandlingsStatus.equals(AVSLUTTET);
        String beskrivelsesKey = erAvsluttet ? "behandling.beskrivelse.avsluttet" : "behandling.beskrivelse.underArbeid";
        String datoToppTekst = String.format(cms.hentTekst("behandling.dato.topp"), erAvsluttet ? avsluttetDato : opprettetDato);
        String datoOpprettetTekst = String.format(cms.hentTekst("behandling.opprettet.dato"), opprettetDato);

        add(
                new Label("hendelse-tittel", tittel),
                new Label("hendelse-beskrivelse", forsokAaHenteSpesifikBeskrivelseForTemakode(beskrivelsesKey, sakstemakode)),
                new Label("dato-topp", datoToppTekst),
                new Label("dato-bunn", datoOpprettetTekst).add(visibleIf(of(erAvsluttet)))
        );
    }

    private String forsokAaHenteSpesifikBeskrivelseForTemakode(String key, String sakstemakode) {
        String keyMedTemaKode = key + "." + sakstemakode;
        if (cms.eksistererTekst(keyMedTemaKode)) {
            return cms.hentTekst(keyMedTemaKode);
        } else {
            return cms.hentTekst(key);
        }
    }
}
