package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;

import static java.lang.System.setProperty;

/**
 * Work-around for å garantere at spring.profiles.active settes før gamle kontekstklasser lastes.
 *
 * NB! Sørg for at denne klasser importeres før gamle kontekstklasser.
 */
@Configuration
public class DefaultProfileContext {

    static  {
        setProperty("spring.profiles.active", "test");
    }

}
