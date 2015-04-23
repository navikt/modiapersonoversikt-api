package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.widget.WidgetMeldingVM.NYESTE_OVERST;

public class MeldingerWidget extends AsyncWidget<WidgetMeldingVM> {

    private static final Logger log = LoggerFactory.getLogger(MeldingerWidget.class);

    private final String fnr;

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    public MeldingerWidget(String id, String initial, final String fnr) {
        super(id, initial, 5);
        setOutputMarkupId(true);
        this.fnr = fnr;
        this.errorKey = "info.feil";
        this.overflowKey = "info.mangemeldinger";
    }

    @Override
    public MeldingerWidgetPanel newFeedPanel(String id, IModel<WidgetMeldingVM> model) {
        return new MeldingerWidgetPanel(id, model);
    }

    @Override
    public List<WidgetMeldingVM> getFeedItems() {
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(fnr);
        List<WidgetMeldingVM> collect = on(skillUtTraader(meldinger).values()).map(TIL_MELDINGVM).collect(NYESTE_OVERST);
        return collect;
    }

    private static final Transformer<List<Melding>, WidgetMeldingVM> TIL_MELDINGVM = new Transformer<List<Melding>, WidgetMeldingVM>() {
        @Override
        public WidgetMeldingVM transform(List<Melding> traad) {
            return new WidgetMeldingVM(traad);
        }
    };

    @RunOnEvents(MELDING_SENDT_TIL_BRUKER)
    public void meldingSendtTilBruker(AjaxRequestTarget target) {
        if (this.isVisibleInHierarchy()) {
            this.startLoading();
            target.add(this);
        }
    }

}
