package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers;

import no.nav.kjerneinfo.consumer.config.KjerneinfoSecurityPolicyConfig;
import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonKjerneinfoConsumerConfig;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.KjerneinfoMapperConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.PersonKjerneinfoConsumerConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.tjeneste.virksomhet.person.v1.PersonPortType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.PersonKjerneinfoServiceBiMock.getPersonKjerneinfoServiceBiMock;

@Configuration
@Import({
        PersonKjerneinfoConsumerConfig.class,
        KjerneinfoMapperConfigResolver.class,
        KjerneinfoSecurityPolicyConfig.class
})
public class PersonKjerneinfoWrapper {

    @Inject
    @Named("hentPersonKjerneinfoJaxWsPortProxyFactoryBean")
    private PersonPortType personPortType;

    @Inject
    @Named("hentSelftestPersonKjerneinfoJaxWsPortProxyFactoryBean")
    private PersonPortType selfTestPersonPortType;

    @Inject
    private KjerneinfoMapper kjerneinfoMapperBean;

    @Resource(name = "personKjerneinfoPep")
    private EnforcementPoint kjerneinfoPep;

    @Bean
    @Qualifier("personKjerneinfoServiceDefault")
    public Wrapper<PersonKjerneinfoServiceBi> personKjerneinfoServiceDefault() {
        return new Wrapper<>(new PersonKjerneinfoConsumerConfigImpl(personPortType, selfTestPersonPortType, kjerneinfoMapperBean, kjerneinfoPep).personKjerneinfoServiceBi());
    }

    @Bean
    @Qualifier("personKjerneinfoServiceMock")
    public Wrapper<PersonKjerneinfoServiceBi> personKjerneinfoServiceMock() {
        return new Wrapper<>(getPersonKjerneinfoServiceBiMock());
    }

}
