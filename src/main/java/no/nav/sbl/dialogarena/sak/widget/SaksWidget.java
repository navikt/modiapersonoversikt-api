package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.sak.domain.TemaVM;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import java.util.List;

import static java.util.Arrays.asList;

public class SaksWidget extends FeedWidget<TemaVM> {



    public SaksWidget(String id, String initial, String fnr) {
        super(id, initial, true);

        setDefaultModel(new ListModel<>(transformToTemaVM()));
    }

    private List<TemaVM> transformToTemaVM() {
        return asList(
                new TemaVM(),
                new TemaVM(),
                new TemaVM()
        );
    }

    @Override
    public Component newFeedPanel(String id, IModel<TemaVM> model) {
        return new SaksWidgetPanel(id, model);
    }
}
