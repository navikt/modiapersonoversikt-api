package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.personkjerneinfo;


import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.PersonKjerneinfoConsumerConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.ALLOW_MOCK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PersonKjerneinfoConsumerConfigResolver.class,
        PersonKjerneinfoWrapperTestConfig.class
})
public class PersonKjerneinfoConsumerConfigResolverTest {

    @Inject
    private Wrapper<PersonKjerneinfoServiceBi> personKjerneinfoServiceDefault;

    @Inject
    private PersonKjerneinfoConsumerConfigResolver resolver;

    @Ignore
    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() {
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setProperty(KJERNEINFO_KEY, ALLOW_MOCK);
        resolver.personKjerneinfoServiceBi().hentKjerneinformasjon(new HentKjerneinformasjonRequest(""));
		resolver.personKjerneinfoServiceBi().hentSikkerhetstiltak(new HentSikkerhetstiltakRequest(new String("12121212123")));
        verifyZeroInteractions(personKjerneinfoServiceDefault.wrappedObject);
    }

    @Ignore
    @Test
    public void perDefaultSkalProdkodeEksekveres() {
        setProperty(TILLATMOCKSETUP_PROPERTY, "false");
        resolver.personKjerneinfoServiceBi().hentKjerneinformasjon(new HentKjerneinformasjonRequest(""));
		resolver.personKjerneinfoServiceBi().hentSikkerhetstiltak(new HentSikkerhetstiltakRequest(new String("12121212123")));
        verify(personKjerneinfoServiceDefault.wrappedObject, times(1)).hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class));
        verify(personKjerneinfoServiceDefault.wrappedObject, times(1)).hentSikkerhetstiltak(any(HentSikkerhetstiltakRequest.class));
    }

}
