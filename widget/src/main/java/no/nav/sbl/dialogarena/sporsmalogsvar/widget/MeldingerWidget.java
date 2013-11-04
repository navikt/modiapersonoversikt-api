package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.meldinger.HentMeldingListe;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.KVITTERING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.WSMeldingUtils.skillUtTraader;

public class MeldingerWidget extends FeedWidget<MeldingVM> {

    @Inject
    HenvendelseMeldingerPortType henvendelseMeldingerPortType;

    private String fnr;

    public MeldingerWidget(String id, String initial, String fnr) {
        super(id, initial);
        setOutputMarkupId(true);

        this.fnr = fnr;

        setDefaultModel(new CompoundPropertyModel<>(getMeldingerListe()));
    }

    @Override
    public MeldingWidgetPanel newFeedPanel(String id, IModel<MeldingVM> model) {
        return new MeldingWidgetPanel(id, model);
    }

    @RunOnEvents(KVITTERING)
    public void meldingBesvart(AjaxRequestTarget target) {
        setDefaultModelObject(getMeldingerListe());
        target.add(this);
    }

    private List<MeldingVM> getMeldingerListe() {
        List<WSMelding> meldinger = henvendelseMeldingerPortType.hentMeldingListe(new HentMeldingListe().withFodselsnummer(fnr)).getMelding();
        return on(skillUtTraader(meldinger).values()).map(TIL_MELDINGVM).collect(NYESTE_OVERST);
    }

    private static final Comparator<MeldingVM> NYESTE_OVERST = new Comparator<MeldingVM>() {
        @Override
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o2.getOpprettetDato().compareTo(o1.getOpprettetDato());
        }
    };

    private static final Transformer<List<WSMelding>, MeldingVM> TIL_MELDINGVM = new Transformer<List<WSMelding>, MeldingVM>() {
        @Override
        public MeldingVM transform(List<WSMelding> traad) {
            return new MeldingVM(on(traad).map(TIL_MELDING).collect());
        }
    };
}
