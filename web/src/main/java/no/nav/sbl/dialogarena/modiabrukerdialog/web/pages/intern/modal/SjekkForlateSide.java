package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.CANCEL;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.SAVE;

public class SjekkForlateSide extends Panel {

    public SjekkForlateSide(String id, final ModigModalWindow window, final SjekkForlateSideAnswer answer) {
        super(id);
        add(
                createLink(window, answer, "closeSave", SAVE),
                createLink(window, answer, "closeDiscard", DISCARD),
                createLink(window, answer, "closeCancel", CANCEL)
        );
    }

    private AjaxLink<Void> createLink(final ModigModalWindow window, final SjekkForlateSideAnswer answer, String linkName, final AnswerType answerType) {
        return new AjaxLink<Void>(linkName) {
            public void onClick(AjaxRequestTarget target) {
                answer.setAnswerType(answerType);
                window.close(target);
            }
        };
    }

}
