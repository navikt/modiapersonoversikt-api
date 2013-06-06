package no.nav.dialogarena.modiabrukerdialog.example.component;

import java.util.List;

import javax.inject.Inject;

import no.nav.dialogarena.modiabrukerdialog.example.service.ExampleService;
import no.nav.modig.modia.widget.InfoFeedWidget;
import no.nav.modig.modia.widget.panels.InfoPanelVM;

import org.apache.wicket.model.LoadableDetachableModel;

public class ExampleWidget extends InfoFeedWidget {

    public <T> ExampleWidget(String id, String initial) {
        super(id, initial, new LoadableDetachableModel<List<InfoPanelVM>>() {

            @Inject
            private ExampleService exampleService;

            @Override
            protected List<InfoPanelVM> load() {
                return exampleService.getWidgetContent();
            }
        });
    }

}
