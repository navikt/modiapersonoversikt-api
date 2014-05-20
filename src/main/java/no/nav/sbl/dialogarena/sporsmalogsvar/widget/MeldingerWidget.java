package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.KVITTERING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.skillUtTraader;

public class MeldingerWidget extends FeedWidget<MeldingVM> {

    @Inject
    MeldingService meldingService;

    private String fnr;

    public MeldingerWidget(String id, String initial, String fnr) {
        super(id, initial, true);
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
        List<Melding> meldinger = meldingService.hentMeldinger(fnr);
        return on(skillUtTraader(meldinger).values()).map(TIL_MELDINGVM).collect(NYESTE_OVERST);
    }

    private static final Comparator<MeldingVM> NYESTE_OVERST = new Comparator<MeldingVM>() {
        @Override
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o2.getOpprettetDato().compareTo(o1.getOpprettetDato());
        }
    };

    private static final Transformer<List<Melding>, MeldingVM> TIL_MELDINGVM = new Transformer<List<Melding>, MeldingVM>() {
        @Override
        public MeldingVM transform(List<Melding> traad) {
            return new MeldingVM(traad);
        }
    };
}
