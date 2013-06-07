package no.nav.dialogarena.modiabrukerdialog.example.service;

import static no.nav.dialogarena.modiabrukerdialog.example.component.ExamplePanel.EXAMPLE_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.modig.modia.widget.panels.InfoPanelVM;

import org.springframework.beans.factory.annotation.Value;

public class DefaultExampleService implements ExampleService {

    @Value("${example.content}")
    private String content;

    @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public List<InfoPanelVM> getWidgetContent() {
        List<InfoPanelVM> infoPanel = new ArrayList<>();
        infoPanel.add(new InfoPanelVM("noerror", EXAMPLE_TYPE, content, "Fungerende link", Arrays.asList("")));
        infoPanel.add(new InfoPanelVM("renderingerror", EXAMPLE_TYPE, content, "Rendringsfeil", Arrays.asList("")));
        infoPanel.add(new InfoPanelVM("systemerror", EXAMPLE_TYPE, content, "Systemfeil", Arrays.asList("")));
        return infoPanel;
    }

}
