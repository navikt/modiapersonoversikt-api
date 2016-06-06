package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.brukerprofil.consumer.messages.BrukerprofilResponse;
import no.nav.brukerprofil.consumer.support.mapping.BrukerprofilMapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;

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
                    throws HentKontaktinformasjonOgPreferanserPersonIkkeFunnet, HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt {
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
            public void fjernFraCache(final String ident) {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    mockService.wrappedObject.fjernFraCache(ident);
                } else {
                    defaultService.wrappedObject.fjernFraCache(ident);
                }
            }

        };

    }
}