package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonKjerneinfoConsumerConfig;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.KjerneinfoMapperConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl.PersonKjerneinfoConsumerConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.PersonKjerneinfoServiceBiMock.getPersonKjerneinfoServiceBiMock;

@Configuration
@Import({
        PersonKjerneinfoConsumerConfig.class,
        KjerneinfoMapperConfigResolver.class
})
public class PersonKjerneinfoWrapper {

    @Inject
    private PersonV3 personPortType;

    @Inject
    private KjerneinfoMapper kjerneinfoMapperBean;

    @Resource(name = "pep")
    private EnforcementPoint kjerneinfoPep;

    @Inject
    private OrganisasjonEnhetV2Service organisasjonEnhetV2Service;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Bean
    @Qualifier("personKjerneinfoServiceDefault")
    public Wrapper<PersonKjerneinfoServiceBi> personKjerneinfoServiceDefault() {
        PersonKjerneinfoConsumerConfigImpl kjerneinfoConfig = new PersonKjerneinfoConsumerConfigImpl(
                personPortType,
                kjerneinfoMapperBean,
                kjerneinfoPep,
                organisasjonEnhetV2Service,
                saksbehandlerInnstillingerService);
        return new Wrapper<>(kjerneinfoConfig.personKjerneinfoServiceBi());
    }

    @Bean
    @Qualifier("personKjerneinfoServiceMock")
    public Wrapper<PersonKjerneinfoServiceBi> personKjerneinfoServiceMock() {
        return new Wrapper<>(getPersonKjerneinfoServiceBiMock());
    }

}
