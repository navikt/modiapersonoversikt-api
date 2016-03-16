package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.dkif.config.spring.DkifConsumerConfig;
import no.nav.dkif.consumer.DkifServiceBi;
import no.nav.dkif.consumer.support.DefaultDkifService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.DkifServiceBiMock.getDkifServiceBiMock;

@Import({DkifConsumerConfig.class})
public class DkifWrapper {

    @Inject
    private DigitalKontaktinformasjonV1 dkifV1;

    @Inject
    private DigitalKontaktinformasjonV1 selfTestDigitalKontaktinformasjonV1;

    @Bean
    @Named("dkifDefaultService")
    public Wrapper<DefaultDkifService> dkifDefaultService() {
        return new Wrapper<>(new DefaultDkifService(dkifV1, selfTestDigitalKontaktinformasjonV1));
    }

    @Bean
    @Named("dkifMockService")
    public Wrapper<DkifServiceBi> dkifMockService() {
        return new Wrapper<>(getDkifServiceBiMock());
    }

}
