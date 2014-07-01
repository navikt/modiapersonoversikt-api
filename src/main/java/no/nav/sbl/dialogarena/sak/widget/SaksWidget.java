package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.sak.config.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.domain.TemaVM;
import no.nav.sbl.dialogarena.sak.viewdomain.oversikt.Tema;
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

    private LoadableDetachableModel<List<? extends Record<Tema>>> lagLDMforTema(final List<Record<Tema>> temaer) {
        return new LoadableDetachableModel<List<? extends Record<Tema>>>() {
            @Override
            protected List<? extends Record<Tema>> load() {
                return temaer;
            }
        };
    }

    @Override
    public Component newFeedPanel(String id, IModel<TemaVM> model) {
        return new SaksWidgetPanel(id, model);
    }
}
