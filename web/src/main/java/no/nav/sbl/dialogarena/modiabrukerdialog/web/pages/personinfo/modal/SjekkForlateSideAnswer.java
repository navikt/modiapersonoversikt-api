package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personinfo.modal;

import java.io.Serializable;

public class SjekkForlateSideAnswer implements Serializable {

    public static enum AnswerType {DISCARD, CANCEL}

    private AnswerType answerType;

    public boolean is(AnswerType type) {
        return answerType == type;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

}
