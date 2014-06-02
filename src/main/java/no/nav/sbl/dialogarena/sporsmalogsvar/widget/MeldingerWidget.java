package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.widget.MeldingVM.NYESTE_OVERST;

public class MeldingerWidget extends FeedWidget<MeldingVM> {

    @Inject
    private MeldingService meldingService;

    public MeldingerWidget(String id, String initial, final String fnr) {
        super(id, initial, true);
        setOutputMarkupId(true);

        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<List<MeldingVM>>() {
            @Override
            protected List<MeldingVM> load() {
                return on(skillUtTraader(meldingService.hentMeldinger(fnr)).values()).map(TIL_MELDINGVM).collect(NYESTE_OVERST);
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

}
