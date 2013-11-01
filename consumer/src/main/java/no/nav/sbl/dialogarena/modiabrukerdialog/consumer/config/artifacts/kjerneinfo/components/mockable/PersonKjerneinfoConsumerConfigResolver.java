package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.kjerneinfo.consumer.config.KjerneinfoSecurityPolicyConfig;
import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonKjerneinfoConsumerConfig;
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.tjeneste.virksomhet.person.v1.PersonPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

@Configuration
@Import({
        PersonKjerneinfoConsumerConfig.class,
        PersonKjerneinfoMapperConfigResolver.class,
        KjerneinfoSecurityPolicyConfig.class
})
public class PersonKjerneinfoConsumerConfigResolver {

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
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return new DefaultPersonKjerneinfoService(personPortType, selfTestPersonPortType, kjerneinfoMapperBean, kjerneinfoPep);
    }

}
