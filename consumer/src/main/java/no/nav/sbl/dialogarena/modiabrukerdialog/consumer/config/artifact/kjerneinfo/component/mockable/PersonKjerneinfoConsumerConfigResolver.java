package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.brukerprofil.domain.Bruker;
import no.nav.kjerneinfo.consumer.fim.behandleperson.BehandlePersonServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest;
import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.behandleperson.v1.*;
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.kjerneinfo.consumer.fim.behandleperson.config.BehandlePersonConsumerConfig.TPS_BEHANDLEPERSON_V1_MOCK_KEY;
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

    @Inject
    @Qualifier("behandlePersonServiceDefault")
    private Wrapper<BehandlePersonServiceBi> behandlePersonServiceDefault;

    @Inject
    @Qualifier("behandlePersonServiceMock")
    private Wrapper<BehandlePersonServiceBi> behandlePersonServiceMock;

    @Bean
    public BehandlePersonServiceBi behandlePersonServiceBi() {
        return new BehandlePersonServiceBi() {
            @Override
            public WSEndreNavnResponse endreNavn(WSEndreNavnRequest endreNavnRequest) throws Sikkerhetsbegrensning, PersonIkkeFunnet, UgyldigInput, PersonIkkeUtvandret {
                if (mockErTillattOgSlaattPaaForKey(TPS_BEHANDLEPERSON_V1_MOCK_KEY)) {
                    return behandlePersonServiceMock.wrappedObject.endreNavn(endreNavnRequest);
                }
                return behandlePersonServiceDefault.wrappedObject.endreNavn(endreNavnRequest);
            }
        };
    }

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
            public Bruker hentBrukerprofil(String fodselsnummer) {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return personKjerneinfoServiceMock.wrappedObject.hentBrukerprofil(fodselsnummer);
                }
                return personKjerneinfoServiceDefault.wrappedObject.hentBrukerprofil(fodselsnummer);
            }

        };
    }

}
