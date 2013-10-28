package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ContextBeans;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.MockContextBeans;
import org.slf4j.Logger;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.slf4j.LoggerFactory.getLogger;

public class ModiaApplicationContext extends AnnotationConfigWebApplicationContext {

    private static final Logger LOG = getLogger(ModiaApplicationContext.class);

    private boolean mockMe;

    public ModiaApplicationContext() {
        LOG.debug("Start appcontext");
        mockMe = false;
    }

    @Override
    public void refresh() {
        if (mockMe) {
            register(MockContextBeans.class);
        } else {
            register(ContextBeans.class);
        }
        super.refresh();

        String[] beanDefinitionNames = getBeanDefinitionNames();
        int i = 0;
        for (String beanDefinitionName : beanDefinitionNames) {
            LOG.debug(i + ": beanDefinitionName = " + beanDefinitionName);
            i++;
        }
    }

    public void doRefresh(boolean mockAlt) {
        this.mockMe = mockAlt;
        refresh();
    }

}
