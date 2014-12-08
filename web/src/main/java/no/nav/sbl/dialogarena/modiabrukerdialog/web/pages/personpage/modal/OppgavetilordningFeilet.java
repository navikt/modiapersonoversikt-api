package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

public class OppgavetilordningFeilet extends Panel {
    public OppgavetilordningFeilet(String id) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        setVisibilityAllowed(false);

        add(new AjaxLink<Void>("lukk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                skjul(target);
            }
        });
    }

    public void vis(AjaxRequestTarget target) {
        target.appendJavaScript(
                "if (!$('.wicket-mask-dark')[0]) {" +
                        "$('body').append($('<div/>').addClass('wicket-mask-dark'));};");
        setVisibilityAllowed(true);
        target.add(this);
    }

    public void skjul(AjaxRequestTarget target) {
        target.appendJavaScript("$('.wicket-mask-dark').remove();");
        setVisibilityAllowed(false);
        target.add(this);
    }
}
