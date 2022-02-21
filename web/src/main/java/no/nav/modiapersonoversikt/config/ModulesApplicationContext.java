package no.nav.modiapersonoversikt.config;

import no.nav.modiapersonoversikt.legacy.sak.config.SaksoversiktServiceConfig;
import no.nav.modiapersonoversikt.legacy.utbetaling.lamell.context.UtbetalingLamellContext;
import no.nav.modiapersonoversikt.legacy.varsel.config.VarslingContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({
        UtbetalingLamellContext.class,
        SaksoversiktServiceConfig.class,
        VarslingContext.class
})

public class ModulesApplicationContext {

}
