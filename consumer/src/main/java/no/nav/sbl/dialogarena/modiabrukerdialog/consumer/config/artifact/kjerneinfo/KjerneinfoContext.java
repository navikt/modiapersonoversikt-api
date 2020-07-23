package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.SykmeldingsperioderPanelContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SykmeldingsperioderPanelContext.class
})
public class KjerneinfoContext {
}
