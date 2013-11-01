package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({
        no.nav.kodeverk.consumer.config.ConsumerConfig.class
})
public class PersonKjerneinfoMapperConfigResolver {

    @Inject
    private KodeverkmanagerBi kodeverkmanagerBean;

    @Bean
    public KjerneinfoMapper kjerneinfoMapperBean() {
        return new KjerneinfoMapper(kodeverkmanagerBean);
    }

}
