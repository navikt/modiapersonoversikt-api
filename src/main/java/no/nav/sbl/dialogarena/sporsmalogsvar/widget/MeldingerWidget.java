package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.skillUtTraader;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.FRA_NAV;

public class MeldingerWidget extends AsyncWidget<WidgetMeldingVM> {

    private final String fnr;

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

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
        return skillUtTraader(henvendelseBehandlingService.hentMeldinger(fnr))
                .values().stream()
                .map(TIL_MELDINGVM)
                .sorted(comparing(WidgetMeldingVM::getVisningsDato).reversed())
                .collect(toList());
    }

    private static final Function<List<Melding>, WidgetMeldingVM> TIL_MELDINGVM = (traad) ->
            new WidgetMeldingVM(traad, traad.stream()
                    .map(melding -> FRA_NAV.contains(melding.meldingstype))
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
