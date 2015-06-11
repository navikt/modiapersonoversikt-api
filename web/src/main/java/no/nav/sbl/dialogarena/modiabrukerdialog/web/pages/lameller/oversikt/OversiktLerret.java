package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.sbl.dialogarena.sak.widget.SaksoversiktWidget;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingerWidget;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.PropertyUtils.visUtbetalinger;

public class OversiktLerret extends Lerret {
    public OversiktLerret(String id, String fnr) {
        super(id);
        add(
                new LenkeWidget("lenker", "E", new ListModel<>(asList("kontrakter"))),
                new SykepengerWidget("sykepenger", "Y", new Model<>(fnr)),
                new MeldingerWidget("meldinger", "M", fnr),
                new SaksoversiktWidget("saksoversikt", "S", fnr)
        );

        if(visUtbetalinger()) {
            add(new UtbetalingWidget("utbetalinger", "U", fnr));
        } else {
            add(new WebMarkupContainer("utbetalinger").setVisibilityAllowed(false));
        }
    }
}