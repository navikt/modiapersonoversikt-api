package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;

public class UtbetalingWidget extends FeedWidget<UtbetalingVM> {

    public static final PackageResourceReference UTBETALING_WIDGET_LESS = new PackageResourceReference(UtbetalingWidget.class, "utbetaling.less");

    private static final int MAX_NUMBER_OF_UTBETALINGER = 6;

    @Inject
    private UtbetalingService utbetalingService;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial);
        setDefaultModel(new CompoundPropertyModel<Object>(transformUtbetalingToVM(utbetalingService.hentUtbetalinger(fnr))));
    }

    private List<UtbetalingVM> transformUtbetalingToVM(List<Utbetaling> utbetalinger) {
        List<UtbetalingVM> utbetalingVMs = transformToVMs(utbetalinger);
        if (utbetalingVMs.size() > MAX_NUMBER_OF_UTBETALINGER) {
            //subList returnerer en ikke-serialiserbar liste, må derfor gjøre en workaround her
            return asList(utbetalingVMs.subList(0, MAX_NUMBER_OF_UTBETALINGER).toArray(new UtbetalingVM[]{}));
        } else {
            return utbetalingVMs;
        }
    }

    private ArrayList<UtbetalingVM> transformToVMs(List<Utbetaling> utbetalinger) {
        ArrayList<UtbetalingVM> utbetalingVMs = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            utbetalingVMs.add(new UtbetalingVM(utbetaling));
        }
        sort(utbetalingVMs);
        return utbetalingVMs;
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<UtbetalingVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
