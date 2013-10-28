package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

public class UtbetalingWidget extends FeedWidget<UtbetalingVM> {

    @Inject
    private UtbetalingService utbetalingService;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial);
        setDefaultModel(new CompoundPropertyModel<Object>(transformUtbetalingToVM(utbetalingService.hentUtbetalinger(fnr))));
    }

    private List<UtbetalingVM> transformUtbetalingToVM(List<Utbetaling> utbetalinger) {
        ArrayList<UtbetalingVM> utbetalingVMs = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            utbetalingVMs.add(new UtbetalingVM(utbetaling));
        }
        sort(utbetalingVMs);

        ArrayList<UtbetalingVM> list = new ArrayList<>();
        list.addAll(utbetalingVMs.subList(0, utbetalingVMs.size() > 6 ? 6 : utbetalingVMs.size()));
        return list;
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<UtbetalingVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
