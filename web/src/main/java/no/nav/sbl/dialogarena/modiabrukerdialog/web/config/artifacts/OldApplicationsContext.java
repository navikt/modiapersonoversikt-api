package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.artifacts;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.artifacts.kjerneinfo.KjerneinfoContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.artifacts.personsok.PersonsokContext;
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
public class OldApplicationsContext {

}
