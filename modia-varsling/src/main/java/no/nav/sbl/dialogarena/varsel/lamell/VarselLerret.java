package no.nav.sbl.dialogarena.varsel.lamell;

import no.nav.modig.frontend.FrontendModule;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.varsel.ResourceReference;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.HashMap;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;


public final class VarselLerret extends Lerret {

    public static final FrontendModule RESOURCES = new FrontendModule.With()
            .less(new PackageResourceReference(ResourceReference.class, "build/varsel-module.less"))
            .done();

    public VarselLerret(String id, final String fnr, boolean nyVarsel) {
        super(id);
        if(nyVarsel) {
            add(new ReactComponentPanel("varselLerret", "NyVarsel", new HashMap<String, Object>() {{
                put("fødselsnummer", fnr);
            }}));
            add(hasCssClassIf("ny-frontend", Model.of(nyVarsel)));
        } else {
            add(new ReactComponentPanel("varselLerret", "VarselLerret", new HashMap<String, Object>() {{
                put("fnr", fnr);
            }}));
        }
    }
}