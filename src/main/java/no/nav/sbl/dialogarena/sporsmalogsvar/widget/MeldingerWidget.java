package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.model.MeldingBuffer;
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
    MeldingService meldingService;

    private String fnr;

    public MeldingerWidget(String id, String initial, String fnr) {
        super(id, initial, true);
        setOutputMarkupId(true);

        this.fnr = fnr;

        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<List<MeldingVM>>() {
            @Override
            protected List<MeldingVM> load() {
                oppdaterMeldinger();
                return getMeldingerListe();
            }
        }));
    }

    @Override
    public MeldingWidgetPanel newFeedPanel(String id, IModel<MeldingVM> model) {
        return new MeldingWidgetPanel(id, model);
    }

    private void oppdaterMeldinger() {
        MeldingBuffer.oppdaterMeldinger(meldingService.hentMeldinger(fnr));
    }

    private List<MeldingVM> getMeldingerListe() {
        return on(skillUtTraader(MeldingBuffer.getMeldinger()).values()).map(TIL_MELDINGVM).collect(NYESTE_OVERST);
    }

    private static final Transformer<List<Melding>, MeldingVM> TIL_MELDINGVM = new Transformer<List<Melding>, MeldingVM>() {
        @Override
        public MeldingVM transform(List<Melding> traad) {
            return new MeldingVM(traad);
        }
    };

}
