package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.kjerneinfo.consumer.config.KjerneinfoSecurityPolicyConfig;
import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonKjerneinfoConsumerConfig;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.modig.modia.ping.PingResult;
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
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockSetupErTillatt;
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
        final PersonKjerneinfoServiceBi defaultBi = new PersonKjerneinfoConsumerConfigImpl(personPortType, selfTestPersonPortType, kjerneinfoMapperBean, kjerneinfoPep).personKjerneinfoServiceBi();
        final PersonKjerneinfoServiceBi mockBi =  getPersonKjerneinfoServiceBiMock();
        return new PersonKjerneinfoServiceBi() {
            @Override
            public HentKjerneinformasjonResponse hentKjerneinformasjon(HentKjerneinformasjonRequest hentKjerneinformasjonRequest) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return mockBi.hentKjerneinformasjon(hentKjerneinformasjonRequest);
                }
                return defaultBi.hentKjerneinformasjon(hentKjerneinformasjonRequest);
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return mockBi.ping();
                }
                return defaultBi.ping();
            }
        };
    }

}
