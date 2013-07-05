package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import java.io.Serializable;

public class SjekkForlateSideAnswer implements Serializable {

    public static enum AnswerType {DISCARD, CANCEL, SAVE}

    private AnswerType answerType;

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

}
