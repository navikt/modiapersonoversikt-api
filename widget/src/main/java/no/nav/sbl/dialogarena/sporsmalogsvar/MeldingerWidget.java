package no.nav.sbl.dialogarena.sporsmalogsvar;

import java.util.List;
import javax.inject.Inject;
import no.nav.modig.modia.widget.FeedWidget;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.lang.collections.IterUtils.on;

public class MeldingerWidget extends FeedWidget<MeldingVM> {

    @Inject
    HenvendelsePortType henvendelsePortType;

    public MeldingerWidget(String id, String initial, String fnr) {
        super(id, initial);
        HenvendelseService service = new HenvendelseService(henvendelsePortType, fnr);
        List<MeldingVM> meldinger = on(service.alleTraader()).map(TIL_MELDINGVM).collect();
        setDefaultModel(new CompoundPropertyModel<>(meldinger));
    }

    @Override
    public MeldingWidgetPanel newFeedPanel(String id, IModel<MeldingVM> model) {
        return new MeldingWidgetPanel(id, model);
    }

    private static final Transformer<List<WSHenvendelse>, MeldingVM> TIL_MELDINGVM = new Transformer<List<WSHenvendelse>, MeldingVM>() {
        @Override
        public MeldingVM transform(List<WSHenvendelse> traad) {
            return new MeldingVM(traad);
        }
    };

}
