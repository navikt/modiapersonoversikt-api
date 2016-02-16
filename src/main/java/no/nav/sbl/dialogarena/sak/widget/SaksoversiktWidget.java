package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import java.util.HashMap;

public class SaksoversiktWidget extends Lerret {

    public SaksoversiktWidget(String id, final String fnr) {
        super(id);
        add(new ReactComponentPanel("saksoversiktWidget", "SaksoversiktWidget", new HashMap<String, Object>() {{
            put("fnr", fnr);
        }}));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript("new Modig.Modia.WidgetView('#" + getMarkupId() + "','" + "S" + "');"));
    }


}
