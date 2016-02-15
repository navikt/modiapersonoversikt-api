package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.sak.config.SaksServiceConfig;
import no.nav.sbl.dialogarena.sak.config.SaksoversiktServiceConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.context.SporsmalOgSvarContext;
import no.nav.sbl.dialogarena.utbetaling.lamell.context.UtbetalingLamellContext;
import no.nav.sbl.dialogarena.varsel.config.VarslingContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


//Importer alt av modul config her...

@Configuration
@Import({
        UtbetalingLamellContext.class,
        SporsmalOgSvarContext.class,
        SaksoversiktServiceConfig.class,
        VarslingContext.class
})

public class ModulesApplicationContext {

}
