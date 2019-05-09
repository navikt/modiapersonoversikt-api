package no.nav.sbl.dialogarena.varsel.lamell;

import no.nav.modig.frontend.FrontendModule;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.varsel.ResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.HashMap;


public final class VarselLerret extends Lerret {

    public static final FrontendModule RESOURCES = new FrontendModule.With()
            .less(new PackageResourceReference(ResourceReference.class, "build/varsel-module.less"))
            .done();

    public VarselLerret(String id, final String fnr) {
        super(id);

        add(new ReactComponentPanel("varselLerret", "NyVarsel", new HashMap<String, Object>() {{
            put("fnr", fnr);
        }}));
    }


}