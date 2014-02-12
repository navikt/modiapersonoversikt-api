package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers;

import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.KodeverkmanagerBiMock.getKodeverkmanagerBiMock;

@Configuration
@Import({
        no.nav.kodeverk.consumer.config.ConsumerConfig.class
})
public class KjerneinfoMapperWrapper {

    @Inject
    private KodeverkmanagerBi kodeverkmanagerBean;

    @Bean
    @Qualifier("kodeverkManagerService")
    public Wrapper<KodeverkmanagerBi> kodeverkManagerService() {
        return new Wrapper<>(kodeverkmanagerBean);
    }

    @Bean
    @Qualifier("kodeverkManagerMock")
    public Wrapper<KodeverkmanagerBi> kodeverkManagerMock() {
        return new Wrapper<>(getKodeverkmanagerBiMock());
    }



}
