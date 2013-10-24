package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.PriorityOrdered;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@Configuration
@Import({
        ContextBeans.class,
        MockContextBeans.class
})
public class ApplicationContext extends AnnotationConfigWebApplicationContext implements PriorityOrdered {

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        System.out.println("Refresh");

        System.out.println("Setter profile til default");
        getEnvironment().setActiveProfiles("default");

        super.refresh();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
