package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.FeedWidget;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

public class UtbetalingWidget extends FeedWidget<UtbetalingVM> {


    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial);
    }

    @Override
    public Component newFeedPanel(String id, IModel<UtbetalingVM> model) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
