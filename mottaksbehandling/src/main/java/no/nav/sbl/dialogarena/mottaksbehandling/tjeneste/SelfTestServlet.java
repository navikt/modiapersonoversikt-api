package no.nav.sbl.dialogarena.mottaksbehandling.tjeneste;

import no.nav.sbl.dialogarena.common.web.selftest.SelfTestBaseServlet;
import no.nav.sbl.dialogarena.mottaksbehandling.MottaksbehandlingKontekst;
import no.nav.sbl.dialogarena.types.Pingable;

import java.util.Collection;

public class SelfTestServlet extends SelfTestBaseServlet {

    @Override
    protected Collection<? extends Pingable> getPingables() {
    	MottaksbehandlingKontekst context = (MottaksbehandlingKontekst) getServletContext().getAttribute(MottaksbehandlingKontekst.SERVLETCONTEXT_KEY);
        return context.getPingables();
    }

    @Override
    protected String getApplicationName() {
        return "Mottaksbehandling";
    }
    
}
