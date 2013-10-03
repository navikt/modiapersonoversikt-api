package no.nav.sbl.dialogarena.soknader.widget;

import no.nav.modig.modia.widget.InfoFeedWidget;
import no.nav.modig.modia.widget.panels.InfoPanelVM;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.soknader.widget.util.SoknadDateFormatter.printShortDate;

public class SoknaderWidget extends InfoFeedWidget {

    private static String SOKNAD = "soknader";

    public SoknaderWidget(String id, String initial, final IModel<String> model) {
        super(id, initial, new WidgetModel(model));
    }

    private static final class WidgetModel extends LoadableDetachableModel<List<InfoPanelVM>> {

        IModel<String> fnrModel;

        @SpringBean
        @Named("soknaderService")
        private SoknaderService soknaderService;

        private WidgetModel() {
            Injector.get().inject(this);
        }

        private WidgetModel(final IModel<String> model) {
            Injector.get().inject(this);
            fnrModel = model;
        }

        @Override
        protected List<InfoPanelVM> load() {
            return convertSoknadToInfoPanel(soknaderService.getSoknader(fnrModel.getObject()));
        }

        private List<InfoPanelVM> convertSoknadToInfoPanel(List<Soknad> soknadList) {
            List<InfoPanelVM> infoPanelList = new ArrayList<>();
            int panelId = 0;
            for(Soknad soknad : soknadList) {
                infoPanelList.add(populateInfoPanel("soknad" + panelId++, soknad));
            }
            return infoPanelList;
        }

        private InfoPanelVM populateInfoPanel(String panelId, Soknad soknad) {
            return new InfoPanelVM(panelId, SOKNAD, printShortDate(soknad.getMottattDato()), soknad.getTittel(), populateMetaData(soknad), setInfoPanelStatus(soknad));
        }

        private InfoPanelVM.Status setInfoPanelStatus(Soknad soknad) {
            switch (soknad.getSoknadStatus()) {
                case MOTTATT:
                case UNDER_BEHANDLING:
                    return InfoPanelVM.Status.OK;
                case NYLIG_FERDIG:
                case GAMMEL_FERDIG:
                    return null;
                case UKJENT:
                default:
                    return InfoPanelVM.Status.ERROR;
            }
        }

        private List<String> populateMetaData(Soknad soknad) {
            switch (soknad.getSoknadStatus()) {
                case MOTTATT:
                    return asList(
                            "Normal saksbehandlingstid er " + soknad.getNormertBehandlingsTid());
                case UNDER_BEHANDLING:
                    return asList(
                            "Under behandling siden " + printShortDate(soknad.getUnderBehandlingDato()),
                            "Normal saksbehandlingstid er " + soknad.getNormertBehandlingsTid());
                case NYLIG_FERDIG:
                case GAMMEL_FERDIG:
                    return asList(
                            "Ferdig behandlet " + printShortDate(soknad.getFerdigDato()));
                case UKJENT:
                default:
                    return new ArrayList<>();
            }
        }
    }

}
