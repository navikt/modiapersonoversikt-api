package no.nav.sbl.dialogarena.soknader.widget;

import no.nav.modig.modia.widget.InfoFeedWidget;
import no.nav.modig.modia.widget.panels.InfoPanelVM;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public class SoknaderWidget extends InfoFeedWidget {

    public SoknaderWidget(String id, String initial, final IModel<String> model) {
        super(id, initial, new WidgetModel(model));
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
            List<InfoPanelVM> widgetContent = soknaderService.getWidgetContent(fnrModel.getObject());
            return widgetContent;
        }
    }

}
