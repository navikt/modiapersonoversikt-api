package no.nav.dialogarena.modiabrukerdialog.example.service;

import no.nav.modig.modia.widget.panels.InfoPanelVM;

import java.io.Serializable;
import java.util.List;

public interface ExampleService extends Serializable {

    String getContent();
    boolean isAvailable();
    List<InfoPanelVM> getWidgetContent();

}
