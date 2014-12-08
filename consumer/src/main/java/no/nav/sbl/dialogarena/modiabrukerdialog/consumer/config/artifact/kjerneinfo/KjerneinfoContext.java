package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.BrukerprofilPanelContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.KjerneinfoPanelContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.SykmeldingsperioderPanelContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BrukerprofilPanelContext.class,
        KjerneinfoPanelContext.class,
        SykmeldingsperioderPanelContext.class,
        MockableContext.class
})
public class KjerneinfoContext {
}
