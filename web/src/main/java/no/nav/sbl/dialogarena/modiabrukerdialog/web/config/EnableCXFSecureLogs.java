package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import static no.nav.common.utils.EnvironmentUtils.Type.PUBLIC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnableCXFSecureLogs {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnableCXFSecureLogs.java);


    public EnableCXFSecureLogs enableCXFSecureLogs() {
        try {
            EnvironmentUtils.setProperty("CXF_SECURE_LOG", "enabled", PUBLIC);
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.reset();
            ContextInitializer ci = new ContextInitializer(context);
            ci.autoConfig();
        } catch (JoranException e) {
            throw new RuntimeException("Failed to enable CXF secure logs", e);
        }

        return this;
    }
}