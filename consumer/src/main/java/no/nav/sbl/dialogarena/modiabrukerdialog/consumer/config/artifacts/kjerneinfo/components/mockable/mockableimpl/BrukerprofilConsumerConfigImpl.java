package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.brukerprofil.consumer.messages.BrukerprofilResponse;
import no.nav.brukerprofil.consumer.support.DefaultBrukerprofilService;
import no.nav.brukerprofil.consumer.support.mapping.BrukerprofilMapper;
import no.nav.modig.modia.ping.PingResult;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import org.springframework.cache.annotation.Cacheable;

public class BrukerprofilConsumerConfigImpl {

    private BrukerprofilPortType brukerprofilPortType;
    private BrukerprofilPortType selfTestBrukerprofilPortType;

    public BrukerprofilConsumerConfigImpl(BrukerprofilPortType brukerprofilPortType, BrukerprofilPortType selfTestBrukerprofilPortType) {
        this.brukerprofilPortType = brukerprofilPortType;
        this.selfTestBrukerprofilPortType = selfTestBrukerprofilPortType;
    }

    public BrukerprofilServiceBi brukerprofilServiceBi() {
        final DefaultBrukerprofilService defaultBrukerprofilService = new DefaultBrukerprofilService(brukerprofilPortType, selfTestBrukerprofilPortType, new BrukerprofilMapper());

        return new BrukerprofilServiceBi() {

            @Cacheable("endpointCache")
            @Override
            public BrukerprofilResponse hentKontaktinformasjonOgPreferanser(BrukerprofilRequest request) throws HentKontaktinformasjonOgPreferanserPersonIkkeFunnet, HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning {
                return defaultBrukerprofilService.hentKontaktinformasjonOgPreferanser(request);
            }

            @Override
            public void setMapper(BrukerprofilMapper mapper) {
                defaultBrukerprofilService.setMapper(mapper);
            }

            @Override
            public PingResult ping() {
                return defaultBrukerprofilService.ping();
            }
        };
    }

}
