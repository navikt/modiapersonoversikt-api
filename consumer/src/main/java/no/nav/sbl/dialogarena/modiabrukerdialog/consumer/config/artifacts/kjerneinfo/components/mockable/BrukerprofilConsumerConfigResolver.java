package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.brukerprofil.consumer.messages.BrukerprofilResponse;
import no.nav.brukerprofil.consumer.support.mapping.BrukerprofilMapper;
import no.nav.modig.modia.ping.PingResult;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class BrukerprofilConsumerConfigResolver {

    @Inject
    @Qualifier("brukerprofilService")
    private Wrapper<BrukerprofilServiceBi> defaultService;

    @Inject
    @Qualifier("brukerprofilMock")
    private Wrapper<BrukerprofilServiceBi> mockService;

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() {
        return new BrukerprofilServiceBi() {
            @Override
            public BrukerprofilResponse hentKontaktinformasjonOgPreferanser(BrukerprofilRequest request)
                    throws HentKontaktinformasjonOgPreferanserPersonIkkeFunnet, HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return mockService.wrappedObject.hentKontaktinformasjonOgPreferanser(request);
                }
                return defaultService.wrappedObject.hentKontaktinformasjonOgPreferanser(request);
            }

            @Override
            public void setMapper(BrukerprofilMapper mapper) {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    mockService.wrappedObject.setMapper(mapper);
                } else {
                    defaultService.wrappedObject.setMapper(mapper);
                }
            }

            @Override
            public PingResult ping() {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return mockService.wrappedObject.ping();
                }
                return defaultService.wrappedObject.ping();
            }
        };

    }
}