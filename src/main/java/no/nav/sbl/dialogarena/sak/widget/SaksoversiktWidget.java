package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.modia.model.FeedItemVM;
import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.modia.widget.panels.ErrorListing;
import no.nav.modig.modia.widget.panels.GenericListing;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;

public class SaksoversiktWidget extends FeedWidget<TemaVM> {

    @Inject
    private SaksoversiktService saksoversiktService;

    private static final Logger log = LoggerFactory.getLogger(SaksoversiktWidget.class);

    public SaksoversiktWidget(String id, String initial, String fnr) {
        super(id, initial, true);

        setDefaultModel(lagLDMforTema(fnr));
    }

    private LoadableDetachableModel<List<? extends FeedItemVM>> lagLDMforTema(final String fnr) {
        return new LoadableDetachableModel<List<? extends FeedItemVM>>() {
            @Override
            protected List<? extends FeedItemVM> load() {
                try {
                    List<? extends FeedItemVM> temaVMList = saksoversiktService.hentTemaer(fnr);
                    return temaVMList.isEmpty() ? asList(new GenericListing(getString("ingen.saker"))) : temaVMList;
                } catch (ApplicationException | SystemException e) {
                    log.warn("Feilet ved henting av saksbehandlingsinformasjon for fnr {}", fnr, e);
                    return asList(new ErrorListing(getString("saker.feilet")));
                }
            }
        };
    }

    @Override
    public Component newFeedPanel(String id, IModel<TemaVM> model) {
        return new SaksoversiktWidgetPanel(id, model);
    }
}
