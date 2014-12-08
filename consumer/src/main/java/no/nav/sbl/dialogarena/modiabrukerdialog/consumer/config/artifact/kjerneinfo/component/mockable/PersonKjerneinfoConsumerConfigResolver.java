package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest;
import no.nav.modig.modia.ping.PingResult;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration

public class PersonKjerneinfoConsumerConfigResolver {

    @Inject
    @Qualifier("personKjerneinfoServiceDefault")
    private Wrapper<PersonKjerneinfoServiceBi> personKjerneinfoServiceDefault;

    @Inject
    @Qualifier("personKjerneinfoServiceMock")
    private Wrapper<PersonKjerneinfoServiceBi> personKjerneinfoServiceMock;

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return new PersonKjerneinfoServiceBi() {
            @Override
            public HentKjerneinformasjonResponse hentKjerneinformasjon(HentKjerneinformasjonRequest hentKjerneinformasjonRequest) {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return personKjerneinfoServiceMock.wrappedObject.hentKjerneinformasjon(hentKjerneinformasjonRequest);
                }
                return personKjerneinfoServiceDefault.wrappedObject.hentKjerneinformasjon(hentKjerneinformasjonRequest);
            }

			@Override
			public Sikkerhetstiltak hentSikkerhetstiltak(HentSikkerhetstiltakRequest ident) {
				if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
					return personKjerneinfoServiceMock.wrappedObject.hentSikkerhetstiltak(ident);
				}
				return personKjerneinfoServiceDefault.wrappedObject.hentSikkerhetstiltak(ident);

			}

			@Override
            public PingResult ping() {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return personKjerneinfoServiceMock.wrappedObject.ping();
                }
                return personKjerneinfoServiceDefault.wrappedObject.ping();
            }
        };
    }

}
