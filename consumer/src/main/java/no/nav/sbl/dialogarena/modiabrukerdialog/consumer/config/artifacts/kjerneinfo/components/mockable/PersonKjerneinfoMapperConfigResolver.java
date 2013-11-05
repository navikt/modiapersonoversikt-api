package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.PersonKjerneinfoMapperConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.KodeverkmanagerBiMock.getKodeverkmanagerBiMock;

@Configuration
@Import({
        no.nav.kodeverk.consumer.config.ConsumerConfig.class
})
public class PersonKjerneinfoMapperConfigResolver {

    @Inject
    private KodeverkmanagerBi kodeverkmanagerBean;
    private KodeverkmanagerBi kodeverkmanagerBeanMock = getKodeverkmanagerBiMock();
    private KjerneinfoMapper mapper = new PersonKjerneinfoMapperConfigImpl(kodeverkmanagerBean).kjerneinfoMapperBean();
    private KjerneinfoMapper mapperMock = new PersonKjerneinfoMapperConfigImpl(kodeverkmanagerBeanMock).kjerneinfoMapperBean();
    private String key = "start.kjerneinfo.withmock";

    @Bean
    public KjerneinfoMapper kjerneinfoMapperBean() {
        return new KjerneinfoMapper(kodeverksManagerBean());
    }

    private KodeverkmanagerBi kodeverksManagerBean() {
        return createSwitcher(kodeverkmanagerBean, kodeverkmanagerBeanMock, key, KodeverkmanagerBi.class);
    }

}
