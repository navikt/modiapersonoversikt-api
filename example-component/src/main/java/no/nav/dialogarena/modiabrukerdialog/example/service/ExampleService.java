package no.nav.dialogarena.modiabrukerdialog.example.service;

import java.io.Serializable;
import java.util.List;

import no.nav.modig.modia.widget.panels.InfoPanelVM;

public interface ExampleService extends Serializable {

    String getContent();

    List<InfoPanelVM> getWidgetContent();

}
