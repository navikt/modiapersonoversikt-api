package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.brukerprofil.consumer.messages.BrukerprofilResponse;
import no.nav.brukerprofil.consumer.support.mapping.BrukerprofilMapper;
import no.nav.modig.modia.ping.PingResult;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockSetupErTillatt;

@Configuration
public class BrukerprofilConsumerConfigResolver {

    @Inject
    @Qualifier("brukerprofilService")
    private BrukerprofilServiceBi defaultService;

    @Inject
    @Qualifier("brukerprofilMock")
    private BrukerprofilServiceBi mockService;

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() {
        return new BrukerprofilServiceBi() {
            @Override
            public BrukerprofilResponse hentKontaktinformasjonOgPreferanser(BrukerprofilRequest request)
                    throws HentKontaktinformasjonOgPreferanserPersonIkkeFunnet, HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return mockService.hentKontaktinformasjonOgPreferanser(request);
                }
                return defaultService.hentKontaktinformasjonOgPreferanser(request);
            }

            @Override
            public void setMapper(BrukerprofilMapper mapper) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    mockService.setMapper(mapper);
                } else {
                    defaultService.setMapper(mapper);
                }
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return mockService.ping();
                }
                return defaultService.ping();
            }
        };

    }
}