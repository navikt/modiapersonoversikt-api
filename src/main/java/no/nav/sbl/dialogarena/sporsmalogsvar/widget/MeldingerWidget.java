package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingVM.NYESTE_OVERST;

public class MeldingerWidget extends FeedWidget<MeldingVM> {

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    public MeldingerWidget(String id, String initial, final String fnr) {
        super(id, initial, true);
        setOutputMarkupId(true);
        setMaxNumberOfFeedItems(5);

        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<List<MeldingVM>>() {
            @Override
            protected List<MeldingVM> load() {
                return on(skillUtTraader(henvendelseBehandlingService.hentMeldinger(fnr)).values()).map(TIL_MELDINGVM).collect(NYESTE_OVERST);
            }
        }));
    }

    @Override
    public MeldingWidgetPanel newFeedPanel(String id, IModel<MeldingVM> model) {
        return new MeldingWidgetPanel(id, model);
    }

    private static final Transformer<List<Melding>, MeldingVM> TIL_MELDINGVM = new Transformer<List<Melding>, MeldingVM>() {
        @Override
        public MeldingVM transform(List<Melding> traad) {
            return new MeldingVM(traad);
        }
    };

    @RunOnEvents(MELDING_SENDT_TIL_BRUKER)
    public void meldingSendtTilBruker(AjaxRequestTarget target) {
        if (this.isVisibleInHierarchy()) {
            target.add(this);
        }
    }

}
