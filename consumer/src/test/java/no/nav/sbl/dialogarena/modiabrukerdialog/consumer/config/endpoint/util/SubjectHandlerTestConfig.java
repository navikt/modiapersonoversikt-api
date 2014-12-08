package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util;

import no.nav.modig.core.context.SubjectHandler;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.Subject;

@Configuration
public class SubjectHandlerTestConfig extends SubjectHandler {
    static {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", SubjectHandlerTestConfig.class.getName());
    }

    @Override
    protected Subject getSubject() {
        return new Subject();
    }
}
