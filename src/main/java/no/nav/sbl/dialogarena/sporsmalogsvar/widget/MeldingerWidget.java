package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.modia.model.FeedItemVM;
import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.modia.widget.panels.ErrorListing;
import no.nav.modig.modia.widget.panels.GenericListing;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.widget.WidgetMeldingVM.NYESTE_OVERST;

public class MeldingerWidget extends FeedWidget<WidgetMeldingVM> {

    private static final Logger log = LoggerFactory.getLogger(MeldingerWidget.class);

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    public MeldingerWidget(String id, String initial, final String fnr) {
        super(id, initial, true, "info.mangemeldinger");
        setOutputMarkupId(true);
        setMaxNumberOfFeedItems(5);

        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<List<? extends FeedItemVM>>() {
            @Override
            protected List<? extends FeedItemVM> load() {
                try {
                    List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(fnr);
                    return meldinger.isEmpty() ?
                            asList(new GenericListing(getString("info.ingenmeldinger"))) :
                            on(skillUtTraader(meldinger).values()).map(TIL_MELDINGVM).collect(NYESTE_OVERST);
                } catch (Exception e) {
                    log.warn("Feilet ved henting av henvendelser for fnr {}", fnr, e);
                    return asList(new ErrorListing(getString("info.feil")));
                }
            }
        }));
    }

    @Override
    public MeldingerWidgetPanel newFeedPanel(String id, IModel<WidgetMeldingVM> model) {
        return new MeldingerWidgetPanel(id, model);
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
            target.add(this);
        }
    }

}
