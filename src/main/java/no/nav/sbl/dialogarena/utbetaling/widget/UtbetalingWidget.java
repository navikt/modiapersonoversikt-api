package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.modia.widget.panels.ErrorListing;
import no.nav.modig.modia.widget.panels.GenericListing;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.widget.hentutbetalinger.HentUtbetalingerPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedytelseVM.TIL_HOVEDYTELSEVM;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedytelseVM.UtbetalingVMComparator;

public class UtbetalingWidget extends FeedWidget<HovedytelseVM> {

    private static final Logger LOG = LoggerFactory.getLogger(UtbetalingWidget.class);
    private static final int MAX_NUMBER_OF_UTBETALINGER = 6;

    @Inject
    private UtbetalingService utbetalingService;

    private String fnr;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial, true);

        this.fnr = fnr;

        setDefaultModel(new ListModel<>(asList(new GenericListing(new HentUtbetalingerPanel(this)))));
    }

    private List<HovedytelseVM> transformUtbetalingToVM(List<Record<Hovedytelse>> utbetalinger) {
        List<HovedytelseVM> hovedytelseVMs = on(utbetalinger).map(TIL_HOVEDYTELSEVM).collect(new UtbetalingVMComparator());
        return on(hovedytelseVMs).take(MAX_NUMBER_OF_UTBETALINGER).collect();
    }

    public void hentUtbetalinger() {
        try {
            List<Record<Hovedytelse>> hovedytelser = utbetalingService.hentUtbetalinger(fnr, defaultStartDato(), defaultSluttDato());
            if (hovedytelser.isEmpty()) {
                setDefaultModel(new ListModel<>(asList(new GenericListing(getString("ingen.utbetalinger")))));
            } else {
                setDefaultModel(new ListModel<>(transformUtbetalingToVM(hovedytelser)));
            }
        } catch (ApplicationException | SystemException e) {
            LOG.warn("Feilet ved henting av utbetalingsinformasjon for fnr {}", fnr, e);
            setDefaultModel(new ListModel<>(asList(new ErrorListing(getString("utbetalinger.feilet")))));
        }
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<HovedytelseVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
