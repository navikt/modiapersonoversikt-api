package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.KVITTERING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.WSHenvendelseUtils.skillUtTraader;

public class MeldingerWidget extends FeedWidget<MeldingVM> {

    @Inject
    HenvendelsePortType henvendelsePortType;

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
        List<WSHenvendelse> henvendelseListe = henvendelsePortType.hentHenvendelseListe(fnr, asList("SPORSMAL", "SVAR"));
        return on(skillUtTraader(henvendelseListe).values()).map(TIL_MELDINGVM).collect(NYESTE_OVERST);
    }

    private static final Comparator<MeldingVM> NYESTE_OVERST = new Comparator<MeldingVM>() {
        @Override
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o2.getOpprettetDato().compareTo(o1.getOpprettetDato());
        }
    };

    private static final Transformer<List<WSHenvendelse>, MeldingVM> TIL_MELDINGVM = new Transformer<List<WSHenvendelse>, MeldingVM>() {
        @Override
        public MeldingVM transform(List<WSHenvendelse> traad) {
            return new MeldingVM(on(traad).map(TIL_MELDING).collect());
        }
    };
}
