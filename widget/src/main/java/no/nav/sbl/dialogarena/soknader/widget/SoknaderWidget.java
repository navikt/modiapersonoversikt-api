package no.nav.sbl.dialogarena.soknader.widget;

import no.nav.modig.modia.widget.InfoFeedWidget;
import no.nav.modig.modia.widget.panels.InfoPanelVM;
import no.nav.sbl.dialogarena.soknader.service.SoknaderWidgetService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SoknaderWidget extends InfoFeedWidget {

    public SoknaderWidget(String id, String initial, final IModel<String> model) {
        super(id, initial, new WidgetModel(model));
    }

    private static final class WidgetModel extends LoadableDetachableModel<List<InfoPanelVM>> {

        private Logger LOG = LoggerFactory.getLogger(WidgetModel.class);

        IModel<String> fnrModel;
        @SpringBean
        private SoknaderWidgetService soknaderWidgetService;

        private WidgetModel() {
            Injector.get().inject(this);
        }

        private WidgetModel(final IModel<String> model) {
            Injector.get().inject(this);
            fnrModel = model;
        }

        @Override
        protected List<InfoPanelVM> load() {
            List<InfoPanelVM> widgetContent = soknaderWidgetService.getWidgetContent(fnrModel.getObject());
            return widgetContent;
        }
    }

}
