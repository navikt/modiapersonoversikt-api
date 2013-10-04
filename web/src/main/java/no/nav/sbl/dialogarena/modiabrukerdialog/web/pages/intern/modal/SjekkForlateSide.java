package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.CANCEL;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;

public class SjekkForlateSide extends Panel {

    public SjekkForlateSide(String id, final ModigModalWindow window, final SjekkForlateSideAnswer answer) {
        super(id);
        add(
                createLink(window, answer, "closeDiscard", DISCARD),
                createLink(window, answer, "closeCancel", CANCEL)
        );
    }

    private AjaxLink<?> createLink(final ModigModalWindow modalWindow, final SjekkForlateSideAnswer answer, String linkName, final AnswerType answerType) {
        return new AjaxLink<Void>(linkName) {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                answer.setAnswerType(answerType);
                modalWindow.close(ajaxRequestTarget);
            }
        };
    }

}
