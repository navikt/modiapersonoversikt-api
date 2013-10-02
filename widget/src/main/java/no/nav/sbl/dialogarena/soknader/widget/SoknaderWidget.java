package no.nav.sbl.dialogarena.soknader.widget;

import no.nav.modig.modia.widget.InfoFeedWidget;
import no.nav.modig.modia.widget.panels.InfoPanelVM;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.soknader.widget.util.SoknadDateFormatter.printShortDate;

public class SoknaderWidget extends InfoFeedWidget {

    private static String SOKNAD = "soknader";

    public static final int SOKNADER_NUMBER_OF_FEED_ITEMS = 100;

    public SoknaderWidget(String id, String initial, final IModel<String> model) {
        super(id, initial, new WidgetModel(model));
//        setMaxNumberOfFeedItems(SOKNADER_NUMBER_OF_FEED_ITEMS);
    }

    private static final class WidgetModel extends LoadableDetachableModel<List<InfoPanelVM>> {

        IModel<String> fnrModel;

        @SpringBean
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
            List<InfoPanelVM> widgetContent = convertSoknadToInfoPanel(soknaderService.getSoknader(fnrModel.getObject()));
            return widgetContent;
        }

        private List<InfoPanelVM> convertSoknadToInfoPanel(List<Soknad> soknadList) {
            List<InfoPanelVM> infoPanelList = new ArrayList<>();
            int panelId = 0;
            for(Soknad soknad : soknadList) {
                infoPanelList.add(populateInfoPanel(panelId++, soknad));
            }
            return infoPanelList;
        }

        private InfoPanelVM populateInfoPanel(int panelId, Soknad soknad) {
            return new InfoPanelVM("soknad"+panelId, SOKNAD, printShortDate(soknad.getMottattDato()), soknad.getTittel(), populateMetaData(soknad), setInfoPanelStatus(soknad));
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
            List<String> metaData = new ArrayList<>();

            switch (soknad.getSoknadStatus()) {
                case MOTTATT:
                    metaData.add("Normal saksbehandlingstid er " + soknad.getNormertBehandlingsTid());
                    break;
                case UNDER_BEHANDLING:
                    metaData.add("Under behandling siden " + printShortDate(soknad.getUnderBehandlingDato()));
                    metaData.add("Normal saksbehandlingstid er " + soknad.getNormertBehandlingsTid());
                    break;
                case NYLIG_FERDIG:
                case GAMMEL_FERDIG:
                    metaData.add("Ferdig behandlet " + printShortDate(soknad.getFerdigDato()));
                    break;
                case UKJENT:
                default:
                    break;
            }

            return metaData;
        }
    }

}
