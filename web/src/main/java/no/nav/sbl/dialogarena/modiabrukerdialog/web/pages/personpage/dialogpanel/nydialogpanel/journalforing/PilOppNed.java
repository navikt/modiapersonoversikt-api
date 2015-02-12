package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.journalforing;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.attributeIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class PilOppNed extends WebMarkupContainer {
    public PilOppNed(String id, AjaxLink link, final IModel<Boolean> open) {
        super(id);

        add(
                hasCssClassIf("opp", open),
                hasCssClassIf("ned", not(open))
        );

        link.add(this);
        link.add(attributeIf("aria-pressed", "true", open, true));
        link.add(attributeIf("aria-pressed", "false", not(open), true));
    }
}
