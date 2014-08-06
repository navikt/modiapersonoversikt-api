package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

import javax.inject.Inject;
import java.util.List;

import static java.lang.String.format;
import static no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService.BEHANDLINGSTEMA;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsType.BEHANDLING;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsType.KVITTERING;
import static org.apache.wicket.model.Model.of;
import static org.apache.wicket.model.Model.ofList;


public class BehandlingerListView extends PropertyListView<GenerellBehandling> {

    @Inject
    private CmsContentRetriever cms;

    @Inject
    private BulletProofKodeverkService kodeverk;

    private String fnr;

    public BehandlingerListView(String id, List<GenerellBehandling> behandlinger, String fnr) {
        super(id);
        setDefaultModel(ofList(behandlinger));
        this.fnr = fnr;
    }

    @Override
    protected void populateItem(ListItem<GenerellBehandling> item) {
        GenerellBehandling behandling = item.getModelObject();
        item.add(new Label("hendelse-tittel", getTittel(behandling)));
        if (BEHANDLING.equals(behandling.behandlingsType)) {
            item.add(new BehandlingsPanel("behandling", of(behandling)));
        } else if (KVITTERING.equals(behandling.behandlingsType)) {
            item.add(new KvitteringsPanel("behandling", of((Kvittering)behandling), fnr));
        }
    }

    private String getTittel(GenerellBehandling behandling) {
        String behandlingstema = kodeverk.getTemanavnForTemakode(behandling.behandlingstema, BEHANDLINGSTEMA);
        switch (behandling.behandlingsType) {
            case BEHANDLING:
                return format(cms.hentTekst(resolveMarkupKey(behandling)), behandlingstema);
            case KVITTERING:
                return format(hentKvitteringsstreng(behandling), behandlingstema);
            default:
                throw new ApplicationException("Ukjent behandlingstype: " + behandling.behandlingsType);
        }
    }

    private String hentKvitteringsstreng(GenerellBehandling behandling) {
        return cms.hentTekst(behandling.ettersending ? "hendelse.kvittering.ettersending.tittel" : "hendelse.kvittering.tittel");
    }

    private String resolveMarkupKey(GenerellBehandling behandling) {
        return AVSLUTTET.equals(behandling.behandlingsStatus) ? "hendelse.avsluttet.tittel" : "hendelse.opprettet.tittel";
    }

}
