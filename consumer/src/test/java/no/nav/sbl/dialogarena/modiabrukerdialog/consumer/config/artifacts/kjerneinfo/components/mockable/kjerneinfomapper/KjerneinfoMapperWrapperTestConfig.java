package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.kjerneinfomapper;


import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.KodeverkmanagerBiMock.getKodeverkmanagerBiMock;

@Configuration
public class KjerneinfoMapperWrapperTestConfig {

    @Bean
    public Wrapper<KodeverkmanagerBi> kodeverkManagerService() {
        return new Wrapper<>(getKodeverkmanagerBiMock());
    }

    @Bean
    public Wrapper<KodeverkmanagerBi> kodeverkManagerMock() {
        return new Wrapper<>(getKodeverkmanagerBiMock());
    }

}
