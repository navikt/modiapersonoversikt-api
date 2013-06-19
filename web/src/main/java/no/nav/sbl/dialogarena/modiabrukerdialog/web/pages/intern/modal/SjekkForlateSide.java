package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

public class SjekkForlateSide extends Panel {



    public SjekkForlateSide(String id, final ModigModalWindow window, final SjekkForlateSideAnswer answer) {
        super(id);
        add(
                new AjaxLink<Void>("closeSave") {
                    public void onClick(AjaxRequestTarget target){
                        answer.setAnswer("SAVE");
                        window.close(target);
                    }
                }   ,
                new AjaxLink<Void>("closeDiscard") {
                    public void onClick(AjaxRequestTarget target){
                        answer.setAnswer("DISCARD");
                        window.close(target);
                    }
                } ,
                new AjaxLink<Void>("closeCancel") {
                    public void onClick(AjaxRequestTarget target){
                        answer.setAnswer("CANCEL");
                        window.close(target);
                    }
                }
        );
    }

}
