package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.kjerneinfo.consumer.fim.behandleperson.BehandlePersonServiceBi;
import no.nav.kjerneinfo.consumer.fim.behandleperson.config.BehandlePersonEndpointConfig;
import no.nav.kjerneinfo.consumer.fim.behandleperson.mock.BehandlePersonServiceBiMock;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonV3EndpointConfig;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.KjerneinfoMapperConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl.BehandlePersonConsumerConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.behandleperson.v1.BehandlePersonV1;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({
        PersonV3EndpointConfig.class,
        KjerneinfoMapperConfigResolver.class,
        BehandlePersonEndpointConfig.class
})
public class PersonKjerneinfoWrapper {

    @Inject
    private BehandlePersonV1 behandlePersonV1;

    @Inject
    private OrganisasjonEnhetV2Service organisasjonEnhetV2Service;

    @Bean
    @Qualifier("behandlePersonServiceDefault")
    public Wrapper<BehandlePersonServiceBi> behandlePersonServiceDefault() {
        BehandlePersonConsumerConfigImpl config = new BehandlePersonConsumerConfigImpl(behandlePersonV1);
        return new Wrapper<>(config.behandlePersonV1());
    }

    @Bean
    @Qualifier("behandlePersonServiceMock")
    public Wrapper<BehandlePersonServiceBi> behandlePersonServiceMock() {
        return new Wrapper<>(new BehandlePersonServiceBiMock());
    }

}
