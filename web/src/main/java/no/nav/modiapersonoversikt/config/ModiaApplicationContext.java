package no.nav.modiapersonoversikt.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ModiaApplicationContext {
    static {
        System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
    }
}
