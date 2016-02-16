package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.widget.async.AsyncWidget;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;

public class SaksoversiktWidget extends AsyncWidget<TemaVM> {


    private final String fnr;

    public SaksoversiktWidget(String id, String initial, String fnr) {
        super(id, initial, new PropertyKeys().withErrorKey("saker.feilet").withOverflowKey("mange.saker").withEmptyKey("ingen.saker"));
        this.fnr = fnr;
    }

    @Override
    public List<TemaVM> getFeedItems() {

        return null;
    }

    @Override
    public Component newFeedPanel(String id, IModel<TemaVM> model) {
        return new SaksoversiktWidgetPanel(id, model).setOutputMarkupId(true);
    }
}
