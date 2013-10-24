package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.BrukerprofilPanelContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.KjerneinfoPanelContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.KontrakterPanelContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.SykmeldingsperioderPanelContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BrukerprofilPanelContext.class,
        KjerneinfoPanelContext.class,
        KontrakterPanelContext.class,
        SykmeldingsperioderPanelContext.class
})
public class KjerneinfoContext {
}
