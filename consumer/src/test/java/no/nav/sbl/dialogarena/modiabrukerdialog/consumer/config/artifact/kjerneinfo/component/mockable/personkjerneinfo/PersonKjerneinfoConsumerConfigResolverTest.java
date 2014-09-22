package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.personkjerneinfo;


import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.PersonKjerneinfoConsumerConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import no.nav.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.ALLOW_MOCK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

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

    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setProperty(KJERNEINFO_KEY, ALLOW_MOCK);
        resolver.personKjerneinfoServiceBi().hentKjerneinformasjon(new HentKjerneinformasjonRequest(""));
		resolver.personKjerneinfoServiceBi().hentSikkerhetstiltak(new HentSikkerhetstiltakRequest(new String("12121212123")));
        resolver.personKjerneinfoServiceBi().ping();
        verifyZeroInteractions(personKjerneinfoServiceDefault.wrappedObject);
    }

    @Test
    public void perDefaultSkalProdkodeEksekveres() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        setProperty(TILLATMOCKSETUP_PROPERTY, "false");
        resolver.personKjerneinfoServiceBi().hentKjerneinformasjon(new HentKjerneinformasjonRequest(""));
		resolver.personKjerneinfoServiceBi().hentSikkerhetstiltak(new HentSikkerhetstiltakRequest(new String("12121212123")));
        resolver.personKjerneinfoServiceBi().ping();
        verify(personKjerneinfoServiceDefault.wrappedObject, times(1)).hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class));
        verify(personKjerneinfoServiceDefault.wrappedObject, times(1)).hentSikkerhetstiltak(any(HentSikkerhetstiltakRequest.class));
        verify(personKjerneinfoServiceDefault.wrappedObject, times(1)).ping();
    }

}
