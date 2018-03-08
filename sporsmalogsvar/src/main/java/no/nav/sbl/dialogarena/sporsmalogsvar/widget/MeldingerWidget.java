package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Traad;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class MeldingerWidget extends AsyncWidget<WidgetMeldingVM> {

    private final String fnr;

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public MeldingerWidget(String id, String initial, final String fnr) {
        super(id, initial, new PropertyKeys().withErrorKey("info.feil").withOverflowKey("info.mangemeldinger").withEmptyKey("info.ingenmeldinger"));
        setOutputMarkupId(true);
        this.fnr = fnr;
    }

    @Override
    public MeldingerWidgetPanel newFeedPanel(String id, IModel<WidgetMeldingVM> model) {
        return new MeldingerWidgetPanel(id, model);
    }

    @Override
    public List<WidgetMeldingVM> getFeedItems() {
        Meldinger meldinger = henvendelseBehandlingService.hentMeldinger(fnr, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
        return meldinger.getTraader()
                .stream()
                .map(Traad::getMeldinger)
                .map(TIL_MELDINGVM)
                .sorted(comparing(WidgetMeldingVM::getDato).reversed())
                .collect(toList());
    }

    private static final Function<List<Melding>, WidgetMeldingVM> TIL_MELDINGVM = (traad) ->
            new WidgetMeldingVM(traad, traad.stream()
                    .map(Melding::erFraSaksbehandler)
                    .distinct()
                    .count()
                    < 2);

    @RunOnEvents(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER)
    public void meldingSendtTilBruker(AjaxRequestTarget target) {
        if (this.isVisibleInHierarchy()) {
            this.startLoading();
            target.add(this);
        }
    }

}
