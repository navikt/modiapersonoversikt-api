package no.nav.sbl.dialogarena.sporsmalogsvar;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import no.nav.modig.modia.widget.FeedWidget;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class MeldingerWidget extends FeedWidget<MeldingVM> {

    @Inject
    HenvendelsePortType henvendelsePortType;

    public MeldingerWidget(String id, String initial, String fnr) {
        super(id, initial);
        List<MeldingVM> meldinger = new ArrayList<>();
        for (List<WSHenvendelse> traad : new HenvendelseService(henvendelsePortType, fnr).alleTraader()) {
            WSHenvendelse nyesteMelding = traad.get(0);
            MeldingVM melding = new MeldingVM(nyesteMelding.getBehandlingsId());
            melding.setDato(nyesteMelding.getOpprettetDato());
            melding.setAvsender((traad.size() == 1 ? "Melding" : "Svar") + " fra " +
                    ("SPORSMAL".equals(nyesteMelding.getHenvendelseType()) ? "Navn Navnesen" : "NAV"));
            melding.setTema(nyesteMelding.getTema());
            meldinger.add(melding);
        }
        setDefaultModel(new CompoundPropertyModel<>(meldinger));
    }

    @Override
    public MeldingWidgetPanel newFeedPanel(String id, IModel<MeldingVM> model) {
        return new MeldingWidgetPanel(id, model);
    }

}
