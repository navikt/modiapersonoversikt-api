package no.nav.sbl.dialogarena.mottaksbehandling.tjeneste;

import no.nav.sbl.dialogarena.common.web.selftest.SelfTestBaseServlet;
import no.nav.sbl.dialogarena.mottaksbehandling.context.AppContext;
import no.nav.sbl.dialogarena.types.Pingable;

import java.util.Collection;

public class SelfTestServlet extends SelfTestBaseServlet {

    @Override
    protected Collection<? extends Pingable> getPingables() {
    	AppContext context = (AppContext) getServletContext().getAttribute(AppContext.SERVLETCONTEXT_KEY);
        return context.getPingables();
    }

    @Override
    protected String getApplicationName() {
        return "Mottaksbehandling";
    }
    
}
