package no.nav.behandlebrukerprofil.consumer.support;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.behandlebrukerprofil.consumer.support.mapping.BehandleBrukerprofilMapper;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.*;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.meldinger.FimOppdaterKontaktinformasjonOgPreferanserRequest;
import org.springframework.cache.CacheManager;

public class DefaultBehandleBrukerprofilService implements BehandleBrukerprofilServiceBi {

    private BehandleBrukerprofilV2 brukerprofilService;
    private BehandleBrukerprofilMapper mapper;
    private CacheManager cacheManager;

    public DefaultBehandleBrukerprofilService(BehandleBrukerprofilV2 brukerprofilService,
                                              BehandleBrukerprofilV2 selfTestBrukerprofilService,
                                              BehandleBrukerprofilMapper mapper,
                                              CacheManager cacheManager) {
        this.mapper = mapper;
        this.brukerprofilService = brukerprofilService;
        this.cacheManager = cacheManager;
    }

    @Override
    public void oppdaterKontaktinformasjonOgPreferanser(BehandleBrukerprofilRequest request) throws
            OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning,
            OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet,
            OppdaterKontaktinformasjonOgPreferanserUgyldigInput,
            OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt {

        FimOppdaterKontaktinformasjonOgPreferanserRequest rawRequest = new FimOppdaterKontaktinformasjonOgPreferanserRequest();

        mapper.map(request, rawRequest);
        brukerprofilService.oppdaterKontaktinformasjonOgPreferanser(rawRequest);
        evictCaches(request);

    }

    private void evictCaches(BehandleBrukerprofilRequest request) {
        no.nav.common.auth.SubjectHandler.getIdent()
                .map((ident) -> ident + request.getBruker().getIdent())
                .ifPresent((keyRoot) -> {
                    cacheManager.getCache("kjerneinformasjonCache").evict(keyRoot + true);
                    cacheManager.getCache("kjerneinformasjonCache").evict(keyRoot + false);
                });

    }
}
