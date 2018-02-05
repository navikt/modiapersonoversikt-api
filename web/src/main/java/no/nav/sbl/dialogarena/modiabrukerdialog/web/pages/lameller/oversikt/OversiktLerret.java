package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.kjerneinfo.kontrakter.oppfolging.OppfolgingWidget;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.metrics.TimingMetricsBehaviour;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.sbl.dialogarena.sak.widget.SaksoversiktWidget;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import no.nav.sbl.dialogarena.varsel.lamell.VarslerOversiktLink;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.isA;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;

public class OversiktLerret extends Lerret {

    private List<AsyncWidget> asyncWidgets;

    public OversiktLerret(String id, String fnr) {
        super(id);
        add(new TimingMetricsBehaviour("oversikt").withPrefix("lerret."));

        List<Widget> widgets = new ArrayList<>(asList(
                new OppfolgingWidget("kontrakter", "T", fnr),
                new SykepengerWidget("sykepenger", "Y", new Model<>(fnr)),
                new MeldingerWidget("meldinger", "M", fnr)
        )
        );

        add(new SaksoversiktWidget("saksoversikt"));
        add(new VarslerOversiktLink("varsling-lenke", fnr));
        widgets.add(new UtbetalingWidget("utbetalinger", "U", fnr));

        asyncWidgets = on(widgets).filter(isA(AsyncWidget.class)).map(castTo(AsyncWidget.class)).collect();

        widgets.forEach(this::add);
    }

    @Override
    protected void onRender() {
        super.onRender();
        asyncWidgets.forEach(AsyncWidget::startLoading);
    }
}