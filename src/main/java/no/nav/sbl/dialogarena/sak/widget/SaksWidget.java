package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.sak.config.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.List;

public class SaksWidget extends FeedWidget<TemaVM> {

    @Inject
    private SaksoversiktService saksoversiktService;

    public SaksWidget(String id, String initial, String fnr) {
        super(id, initial, true);

        setDefaultModel(lagLDMforTema(saksoversiktService.hentTemaer(fnr)));
    }

    private LoadableDetachableModel<List<TemaVM>> lagLDMforTema(final List<TemaVM> temaer) {
        return new LoadableDetachableModel<List<TemaVM>>() {
            @Override
            protected List<TemaVM> load() {
                return temaer;
            }
        };
    }

    @Override
    public Component newFeedPanel(String id, IModel<TemaVM> model) {
        return new SaksWidgetPanel(id, model);
    }
}
