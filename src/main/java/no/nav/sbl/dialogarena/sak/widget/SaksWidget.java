package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.widget.FeedWidget;
import no.nav.modig.modia.widget.panels.GenericListing;
import no.nav.sbl.dialogarena.sak.domain.TemaVM;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import static java.util.Arrays.asList;

public class SaksWidget extends FeedWidget<TemaVM> {

    public SaksWidget(String id, String initial, String fnr) {
        super(id, initial, true);

        setDefaultModel(new ListModel<>(asList(new GenericListing(new HentSakerPanel(this)))));
    }

    public void hentSaker() {

    }

    @Override
    public Component newFeedPanel(String id, IModel<TemaVM> model) {
        return new SaksWidgetPanel(id, model);
    }
}
