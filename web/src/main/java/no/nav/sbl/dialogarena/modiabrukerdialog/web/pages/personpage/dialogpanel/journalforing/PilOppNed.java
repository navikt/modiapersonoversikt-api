package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class PilOppNed extends WebMarkupContainer {
    public PilOppNed(String id, final IModel<Boolean> open) {
        super(id);

        add(
                hasCssClassIf("opp", open),
                hasCssClassIf("ned", not(open))
        );
    }
}
