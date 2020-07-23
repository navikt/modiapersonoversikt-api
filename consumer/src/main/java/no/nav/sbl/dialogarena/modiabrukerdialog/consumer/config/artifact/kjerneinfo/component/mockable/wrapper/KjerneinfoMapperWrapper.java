package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({
        no.nav.kodeverk.consumer.config.ConsumerConfig.class
})
public class KjerneinfoMapperWrapper {

    @Inject
    private KodeverkmanagerBi kodeverkmanagerBean;

    @Bean
    public KodeverkmanagerBi kodeverkManagerService() {
        return kodeverkmanagerBean;
    }
}
