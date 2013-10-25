package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ContextBeans;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.MockContextBeans;
import org.springframework.beans.BeansException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;



public class ApplicationContext  extends AnnotationConfigWebApplicationContext {


    String MOCK_ME = "yes";

    public ApplicationContext() {
        System.out.println("Start appcontext");
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {

        if ("yes".equalsIgnoreCase(MOCK_ME)) {
            register(MockContextBeans.class);
        } else {
            register(ContextBeans.class);
        }

        super.refresh();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
