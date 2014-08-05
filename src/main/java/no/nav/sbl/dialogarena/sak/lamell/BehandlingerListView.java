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
<<<<<<< HEAD
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
=======
import static no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService.BEHANDLINGSTEMA;
>>>>>>> develop
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
        int idx = list.indexOf(behandling);

        Model<Boolean> erNyttAarModell = getErNyttAarModell(behandling, list, idx);
        item.add(hasCssClassIf("ikke-nytt-aar", not(erNyttAarModell)));
        Label aar = new Label("aar", behandling.behandlingDato.getYear());
        aar.add(visibleIf(erNyttAarModell));

        WebMarkupContainer dato = new WebMarkupContainer("dato");

        boolean erAvsluttet = behandling.behandlingsStatus.equals(AVSLUTTET);
        dato.add(hasCssClassIf("avsluttet", Model.of(erAvsluttet)));
        dato.add(
                new Label("dag", behandling.behandlingDato.getDayOfMonth()),
                new Label("maaned", behandling.behandlingDato.toString("MMM", new Locale("no")))
        );

        item.add(
                new Label("hendelse-tittel", getTittel(behandling)),
                dato,
                aar
        );

        if (BEHANDLING.equals(behandling.behandlingsType)) {
            item.add(new BehandlingsPanel("behandling", of(behandling)));
        } else if (KVITTERING.equals(behandling.behandlingsType)) {
            item.add(new KvitteringsPanel("behandling", of((Kvittering)behandling), fnr));
        }
    }

    private Model<Boolean> getErNyttAarModell(GenerellBehandling behandling, List<? extends GenerellBehandling> list, int idx) {
        boolean erNyttAar = false;
        if (idx > 0) {
            DateTime forrigeBehandlingDato = list.get(idx - 1).behandlingDato;

            if (forrigeBehandlingDato.getYear() != behandling.behandlingDato.getYear()) {
                erNyttAar = true;
            }
        } else {
            erNyttAar = true;
        }
        return Model.of(erNyttAar);
    }

    private String getTittel(GenerellBehandling behandling) {
        String behandlingstema = kodeverk.getTemanavnForTemakode(behandling.behandlingstema, BEHANDLINGSTEMA);
        switch (behandling.behandlingsType) {
            case BEHANDLING:
                return format(cms.hentTekst(resolveMarkupKey(behandling)), behandlingstema);
            case KVITTERING:
                return format(behandling.ettersending
                        ? cms.hentTekst("hendelse.kvittering.ettersending.tittel")
                        : cms.hentTekst("hendelse.kvittering.tittel"),
                        behandlingstema);
            default:
                throw new ApplicationException("Ukjent behandlingstype: " + behandling.behandlingsType);
        }
    }

    private String resolveMarkupKey(GenerellBehandling behandling) {
        return AVSLUTTET.equals(behandling.behandlingsStatus) ? "hendelse.avsluttet.tittel" : "hendelse.opprettet.tittel";
    }

}
