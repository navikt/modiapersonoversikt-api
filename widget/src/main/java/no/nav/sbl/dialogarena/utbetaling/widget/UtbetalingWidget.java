package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UtbetalingWidget extends FeedWidget<UtbetalingVM> {

    @Inject
    private UtbetalingService utbetalingService;


    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial);
        List<Utbetaling> utbetalinger = utbetalingService.hentUtbetalinger(fnr);
        setDefaultModel(new CompoundPropertyModel<Object>(transformUtbetalingToVM(utbetalinger)));
    }

    private List<UtbetalingVM> transformUtbetalingToVM(List<Utbetaling> utbetalinger) {
        ArrayList<UtbetalingVM> utbetalingVMs = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            utbetalingVMs.add(new UtbetalingVM(utbetaling));
        }
        return utbetalingVMs;
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<UtbetalingVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }
}
