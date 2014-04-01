package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.modia.lamell.Lerret;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.resource.PackageResourceReference;

public class SaksoversiktLerret extends Lerret {

    public static final PackageResourceReference SAKSOVERSIKT_LESS = new PackageResourceReference(SaksoversiktLerret.class, "saksoversikt.less");

    public SaksoversiktLerret(String id, String fnr) {
        super(id);

        add(new Label("saksoversikt.fnr", fnr));
    }
}
