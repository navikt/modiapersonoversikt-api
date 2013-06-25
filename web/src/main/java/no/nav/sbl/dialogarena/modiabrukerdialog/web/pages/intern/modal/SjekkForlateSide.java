package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

public class SjekkForlateSide extends Panel {

    public SjekkForlateSide(String id, final ModigModalWindow window, final SjekkForlateSideAnswer answer) {
        super(id);
        add(
                createLink(window, answer, "closeSave", "SAVE"),
                createLink(window, answer, "closeDiscard", "DISCARD"),
                createLink(window, answer, "closeCancel", "CANCEL")
        );
    }

    private AjaxLink<Void> createLink(final ModigModalWindow window, final SjekkForlateSideAnswer answer, String linkName, final String answerString) {
        return new AjaxLink<Void>(linkName) {
            public void onClick(AjaxRequestTarget target){
                answer.setAnswer(answerString);
                window.close(target);
            }
        };
    }

}
