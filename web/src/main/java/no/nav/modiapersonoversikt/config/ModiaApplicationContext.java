package no.nav.modiapersonoversikt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        LoginContext.class,
        ApplicationContextBeans.class,
        ModulesApplicationContext.class,
        SelftestContext.class
})

public class ModiaApplicationContext {
    static {
        System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
    }
}
