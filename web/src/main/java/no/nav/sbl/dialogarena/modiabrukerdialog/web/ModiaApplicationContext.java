package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ContextBeans;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.MockContextBeans;
import org.springframework.beans.BeansException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;



public class ModiaApplicationContext extends AnnotationConfigWebApplicationContext {


    private static boolean mockMe = false;

    public ModiaApplicationContext() {
        System.out.println("Start appcontext");
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {

        if (mockMe) {
            register(MockContextBeans.class);
        } else {
            register(ContextBeans.class);
        }
        super.refresh();

        String[] beanDefinitionNames = getBeanDefinitionNames();
        int i = 0;
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(i+ ": beanDefinitionName = " + beanDefinitionName);
            i++;
        }

    }

    public void doRefresh(boolean mockAlt) {
        this.mockMe = mockAlt;
        refresh();
    }

}
