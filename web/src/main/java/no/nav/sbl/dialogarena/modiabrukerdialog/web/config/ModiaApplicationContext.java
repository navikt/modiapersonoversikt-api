package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        WicketApplicationBeans.class,
        ApplicationContextBeans.class
})
public class ModiaApplicationContext {

}
