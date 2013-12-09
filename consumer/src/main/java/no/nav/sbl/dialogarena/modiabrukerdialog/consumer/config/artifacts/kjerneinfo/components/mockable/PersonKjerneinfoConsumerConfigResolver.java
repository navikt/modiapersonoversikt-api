package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.kjerneinfo.consumer.config.KjerneinfoSecurityPolicyConfig;
import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonKjerneinfoConsumerConfig;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.PersonKjerneinfoConsumerConfigImpl;
import no.nav.tjeneste.virksomhet.person.v1.PersonPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.PersonKjerneinfoServiceBiMock.getPersonKjerneinfoServiceBiMock;

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
        PersonKjerneinfoServiceBi defaultBi = new PersonKjerneinfoConsumerConfigImpl(personPortType, selfTestPersonPortType, kjerneinfoMapperBean, kjerneinfoPep).personKjerneinfoServiceBi();
        PersonKjerneinfoServiceBi mockBi =  getPersonKjerneinfoServiceBiMock();
        return createSwitcher(defaultBi, mockBi, KJERNEINFO_KEY, PersonKjerneinfoServiceBi.class);
    }

}
