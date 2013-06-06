package no.nav.dialogarena.modiabrukerdialog.example.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import no.nav.modig.modia.widget.panels.InfoPanelVM;

import org.springframework.beans.factory.annotation.Value;


public class DefaultExampleService implements ExampleService {

    public static final String EXAMPLE_TYPE = "example-type";

    @Value("${content.label:Content}")
    private String contentLabel;

    @Override
    public String getContent() {
        return new StringBuilder(contentLabel).append(" ").append(new Date().toString()).toString();
    }

    public void setContentLabel(String contentLabel) {
        this.contentLabel = contentLabel;
    }

    @Override
    public List<InfoPanelVM> getWidgetContent() {
        List<InfoPanelVM> content = new ArrayList<>();
        content.add(new InfoPanelVM("example1", EXAMPLE_TYPE, "metaheading1", "heading1", Arrays.asList("metadata1")));
        content.add(new InfoPanelVM("example2", EXAMPLE_TYPE, "metaheading2", "heading2", Arrays.asList("metadata2")));
        return content;
    }

}
