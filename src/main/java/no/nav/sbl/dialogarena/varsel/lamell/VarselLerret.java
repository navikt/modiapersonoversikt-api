package no.nav.sbl.dialogarena.varsel.lamell;

import no.nav.modig.frontend.FrontendModule;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.varsel.ResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;


public final class VarselLerret extends Lerret {

    public static final FrontendModule RESOURCES = new FrontendModule.With()
            .scripts(new JavaScriptResourceReference(ResourceReference.class, "build/varsel-module.js"))
            .less(new PackageResourceReference(ResourceReference.class, "build/varsel-module.less"))
            .done();

    public VarselLerret(String id, String fnr) {
        super(id);
        List<Object> s = on(asList()).collect();

        add(new ReactComponentPanel("varselLerret", "VarselLerret"));
    }


}