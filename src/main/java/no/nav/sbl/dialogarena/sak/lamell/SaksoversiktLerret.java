package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.frontend.FrontendModule;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.reactkomponenter.ResourceReference;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.HashMap;


public class SaksoversiktLerret extends Lerret {
    public static final FrontendModule RESOURCES = new FrontendModule.With()
            .less(new PackageResourceReference(ResourceReference.class, "build/saksoversikt-module.less"))
            .done();

    public SaksoversiktLerret(String id, final String fnr) {
        super(id);

        add(new ReactComponentPanel("saksoversiktLerret", "SaksoversiktLerret", new HashMap<String, Object>() {{
            put("fnr", fnr);
        }}));
    }
}

