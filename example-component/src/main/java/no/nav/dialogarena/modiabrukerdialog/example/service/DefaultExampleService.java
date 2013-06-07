package no.nav.dialogarena.modiabrukerdialog.example.service;

import no.nav.modig.modia.widget.panels.InfoPanelVM;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class DefaultExampleService implements ExampleService {

    public static final String EXAMPLE_TYPE = "example-type";

    @Value("${example.content}")
    private String content;

    @Override
    public String getContent() {
        return new StringBuilder(content).append(" ").append(new Date().toString()).toString();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public List<InfoPanelVM> getWidgetContent() {
        List<InfoPanelVM> infoPanel = new ArrayList<>();
        infoPanel.add(new InfoPanelVM("example1", EXAMPLE_TYPE, content, "heading1", Arrays.asList("metadata1")));
        infoPanel.add(new InfoPanelVM("example2", EXAMPLE_TYPE, content, "heading2", Arrays.asList("metadata2")));
        return infoPanel;
    }


}
