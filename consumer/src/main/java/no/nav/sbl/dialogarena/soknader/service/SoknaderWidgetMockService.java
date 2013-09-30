package no.nav.sbl.dialogarena.soknader.service;

import no.nav.modig.modia.widget.panels.InfoPanelVM;

import java.util.ArrayList;
import java.util.List;

public class SoknaderWidgetMockService implements SoknaderWidgetService {


    public static final String SOKNAD = "SOKNAD";

    @Override
    public List<InfoPanelVM> getWidgetContent(String fnr) {
        List<InfoPanelVM> soknader = new ArrayList<>();

        soknader.add(soknad1());
        soknader.add(soknad2());
        soknader.add(soknad3());
        return soknader;
    }

    private InfoPanelVM soknad1() {
        InfoPanelVM infoPanelVM = new InfoPanelVM("soknad1", SOKNAD, "19.08.2013", "Dagpenger", metaData1(), InfoPanelVM.Status.OK);
        return infoPanelVM;
    }

    private InfoPanelVM soknad2() {
        InfoPanelVM infoPanelVM = new InfoPanelVM("soknad2", SOKNAD, "10.07.2013", "Uføre", metaData2(), InfoPanelVM.Status.OK);
        return infoPanelVM;
    }

    private InfoPanelVM soknad3() {
        InfoPanelVM infoPanelVM = new InfoPanelVM("soknad3", SOKNAD, "05.09.2013", "Sykepenger", metaData3(), InfoPanelVM.Status.ERROR);
        return infoPanelVM;
    }

    private List<String> metaData1() {
        List<String> metadata = new ArrayList<>();
        metadata.add("Behandlingstid : 10 dager");
        return metadata;
    }

    private List<String> metaData2() {
        List<String> metadata = new ArrayList<>();
        metadata.add("15.07.2013");
        metadata.add("Behandlingstid : 200 dager");
        return metadata;
    }

    private List<String> metaData3() {
        List<String> metadata = new ArrayList<>();
        metadata.add("10.09.2013");
        metadata.add("Behandlingstid : 10 dager");
        metadata.add("Manglende dokumentasjon");
        return metadata;
    }
}
