package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.kjerneinfo.consumer.fim.behandleperson.BehandlePersonServiceBi;
import no.nav.kjerneinfo.consumer.fim.behandleperson.config.BehandlePersonConsumerConfig;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonKjerneinfoConsumerConfig;
import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.KjerneinfoMapperConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl.BehandlePersonConsumerConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl.PersonKjerneinfoConsumerConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.behandleperson.v1.BehandlePersonV1;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.PersonKjerneinfoServiceBiMock.getBehandlePersonServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.PersonKjerneinfoServiceBiMock.getPersonKjerneinfoServiceBiMock;

@Configuration
@Import({
        PersonKjerneinfoConsumerConfig.class,
        KjerneinfoMapperConfigResolver.class,
        BehandlePersonConsumerConfig.class
})
public class PersonKjerneinfoWrapper {

    @Inject
    private PersonV3 personPortType;

    @Inject
    private BehandlePersonV1 behandlePersonV1;

    @Inject
    private KjerneinfoMapper kjerneinfoMapperBean;

    @Resource(name = "pep")
    private EnforcementPoint kjerneinfoPep;

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
        return new Wrapper<>(getBehandlePersonServiceBiMock());
    }


    @Bean
    @Qualifier("personKjerneinfoServiceDefault")
    public Wrapper<PersonKjerneinfoServiceBi> personKjerneinfoServiceDefault() {
        PersonKjerneinfoConsumerConfigImpl kjerneinfoConfig = new PersonKjerneinfoConsumerConfigImpl(
                personPortType,
                kjerneinfoMapperBean,
                kjerneinfoPep,
                organisasjonEnhetV2Service);
        return new Wrapper<>(kjerneinfoConfig.personKjerneinfoServiceBi());
    }

    @Bean
    @Qualifier("personKjerneinfoServiceMock")
    public Wrapper<PersonKjerneinfoServiceBi> personKjerneinfoServiceMock() {
        return new Wrapper<>(getPersonKjerneinfoServiceBiMock());
    }

}
