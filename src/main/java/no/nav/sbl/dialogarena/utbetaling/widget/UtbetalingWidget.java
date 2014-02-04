package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.modia.widget.panels.GenericListing;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.widget.hentutbetalinger.HentUtbetalingerFeilmeldingPanel;
import no.nav.sbl.dialogarena.utbetaling.widget.hentutbetalinger.HentUtbetalingerPanel;
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

    private String fnr;

    public UtbetalingWidget(String id, String initial, String fnr) {
        super(id, initial, true);

        this.fnr = fnr;

        setDefaultModel(new ListModel<>(asList(new GenericListing(new HentUtbetalingerPanel(this)))));
    }

    private List<UtbetalingVM> transformUtbetalingToVM(List<Utbetaling> utbetalinger) {
        List<UtbetalingVM> utbetalingVMs = on(utbetalinger).map(TIL_UTBETALINGVM).collect(new UtbetalingVMComparator());
        return on(utbetalingVMs).take(MAX_NUMBER_OF_UTBETALINGER).collect();
    }

    public void hentUtbetalinger() {
        try {
            List<Utbetaling> utbetalinger = utbetalingService.hentUtbetalinger(fnr, defaultStartDato(), defaultSluttDato());
            if (utbetalinger.isEmpty()) {
                setDefaultModel(new ListModel<>(asList(new GenericListing(getString("ingen.utbetalinger")))));
            } else {
                setDefaultModel(new ListModel<>(transformUtbetalingToVM(utbetalinger)));
            }
        } catch (ApplicationException ae) {
            LOG.warn("Feilet ved henting av utbetalingsinformasjon for fnr {}", fnr, ae);
            setDefaultModel(new ListModel<>(asList(new GenericListing(new HentUtbetalingerFeilmeldingPanel(this)))));
        }
    }

    @Override
    public UtbetalingWidgetPanel newFeedPanel(String id, IModel<UtbetalingVM> model) {
        return new UtbetalingWidgetPanel(id, model);
    }

}
