package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
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

        List<? extends GenerellBehandling> list = getList();

        Model<Boolean> erNyttAar =  Model.of(getErNyttAarModell(behandling, list));
        WebMarkupContainer aarContainer = new WebMarkupContainer("aar-container");
        aarContainer.add(visibleIf(erNyttAar));
        aarContainer.add(new Label("aar", behandling.behandlingDato.getYear()));
        item.add(hasCssClassIf("ikke-nytt-aar", not(erNyttAar)));

        item.add(
                new Label("hendelse-tittel", getTittel(behandling)),
                getDato(behandling),
                aarContainer
        );

        if (BEHANDLING.equals(behandling.behandlingsType)) {
            item.add(new BehandlingsPanel("behandling", of(behandling)));
        } else if (KVITTERING.equals(behandling.behandlingsType)) {
            item.add(new KvitteringsPanel("behandling", of((Kvittering)behandling), fnr));
        }
    }

    private WebMarkupContainer getDato(GenerellBehandling behandling) {
        WebMarkupContainer dato = new WebMarkupContainer("dato");
        dato.add(hasCssClassIf("avsluttet", Model.of(behandling.behandlingsStatus.equals(AVSLUTTET))));
        dato.add(
                new Label("dag", behandling.behandlingDato.getDayOfMonth()),
                new Label("maaned", behandling.behandlingDato.toString("MMM", new Locale("no")))
        );
        return dato;
    }

    private Boolean getErNyttAarModell(GenerellBehandling behandling, List<? extends GenerellBehandling> list) {
        int index = list.indexOf(behandling);

        if (index == 0) {
            return true;
        }

        DateTime forrigeBehandlingDato = list.get(index - 1).behandlingDato;

        return forrigeBehandlingDato.getYear() != behandling.behandlingDato.getYear();
    }

    private String getTittel(GenerellBehandling behandling) {
        String behandlingstema = kodeverk.getTemanavnForTemakode(behandling.behandlingstema, BEHANDLINGSTEMA);
        switch (behandling.behandlingsType) {
            case BEHANDLING:
                return format(cms.hentTekst(resolveMarkupKey(behandling)), behandlingstema);
            case KVITTERING:
                return format(hentKvitteringsstreng((Kvittering) behandling), behandlingstema);
            default:
                throw new ApplicationException("Ukjent behandlingstype: " + behandling.behandlingsType);
        }
    }

    private String hentKvitteringsstreng(Kvittering kvittering) {
        return cms.hentTekst(kvittering.ettersending ? "hendelse.kvittering.ettersending.tittel" : "hendelse.kvittering.tittel");
    }

    private String resolveMarkupKey(GenerellBehandling behandling) {
        return AVSLUTTET.equals(behandling.behandlingsStatus) ? "hendelse.avsluttet.tittel" : "hendelse.opprettet.tittel";
    }

}
