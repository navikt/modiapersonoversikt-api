package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.modia.widget.panels.EmptyListing;
import no.nav.modig.modia.widget.panels.ErrorListing;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingVM.TIL_UTBETALINGVM;

public class UtbetalingWidget extends FeedWidget<UtbetalingVM> {

    public static final PackageResourceReference UTBETALING_WIDGET_LESS = new PackageResourceReference(UtbetalingWidget.class, "utbetaling.less");

    private static final Logger LOG = LoggerFactory.getLogger(UtbetalingWidget.class);
    private static final int MAX_NUMBER_OF_UTBETALINGER = 6;

    @Inject
    private UtbetalingService utbetalingService;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial);

        try {
            List<Utbetaling> utbetalinger = utbetalingService.hentUtbetalinger(fnr, defaultStartDato(), defaultSluttDato());
            if (utbetalinger.isEmpty()) {
                setDefaultModel(new ListModel<>(asList(new EmptyListing(getString("ingen.utbetalinger")))));
            } else {
                setDefaultModel(new ListModel<>(transformUtbetalingToVM(utbetalinger)));
            }
        } catch (ApplicationException ae) {
            LOG.warn("Feilet ved henting av utbetalingsinformasjon for fnr {}", fnr, ae);
            setDefaultModel(new ListModel<>(asList(new ErrorListing(getString("utbetaling.feilet")))));
        }
    }

    private List<UtbetalingVM> transformUtbetalingToVM(List<Utbetaling> utbetalinger) {
        List<UtbetalingVM> utbetalingVMs = transformToVMs(utbetalinger);
        return on(utbetalingVMs).take(MAX_NUMBER_OF_UTBETALINGER).collect();
    }

    private List<UtbetalingVM> transformToVMs(List<Utbetaling> utbetalinger) {
        return on(utbetalinger).map(TIL_UTBETALINGVM).collect(new UtbetalingVMComparator());
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<UtbetalingVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
