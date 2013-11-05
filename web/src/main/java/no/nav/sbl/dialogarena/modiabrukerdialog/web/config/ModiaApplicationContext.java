package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.slf4j.Logger;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.slf4j.LoggerFactory.getLogger;

public class ModiaApplicationContext extends AnnotationConfigWebApplicationContext {

    private static final Logger LOG = getLogger(ModiaApplicationContext.class);

    public ModiaApplicationContext() {
        LOG.debug("Start appcontext");
    }

    @Override
    public void refresh() {
        register(WicketApplicationBeans.class);
        register(ApplicationContextBeans.class);
        super.refresh();
    }

}
