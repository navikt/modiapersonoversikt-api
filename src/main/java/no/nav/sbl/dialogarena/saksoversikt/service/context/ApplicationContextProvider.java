package no.nav.sbl.dialogarena.saksoversikt.service.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext appContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        setContext(applicationContext);
    }

    private static void setContext(ApplicationContext context){
        appContext = context;
    }

    public static ApplicationContext getContext() {
        return appContext;
    }

}

