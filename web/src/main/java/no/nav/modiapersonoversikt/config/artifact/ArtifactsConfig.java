package no.nav.modiapersonoversikt.config.artifact;

import no.nav.modiapersonoversikt.config.artifact.kjerneinfo.KjerneinfoContext;
import no.nav.modiapersonoversikt.config.artifact.personsok.PersonsokContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Gamle applikasjoner definerer egne kontekstklasser som baserer seg på artefakter, ikke tjenester.
 * Disse importeres samlet, her.
 */
@Configuration
@Import(value = {
        PersonsokContext.class,
        KjerneinfoContext.class
})
public class ArtifactsConfig {

}
