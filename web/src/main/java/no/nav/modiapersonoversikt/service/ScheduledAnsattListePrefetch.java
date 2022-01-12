package no.nav.modiapersonoversikt.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.modiapersonoversikt.consumer.norg.NorgApi;
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain;
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.OppgaveBehandlerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;


public class ScheduledAnsattListePrefetch {

    public static final String SCHEDULE_KEY= "PREFETCH_NORG_ANSATTLISTE_SCHEDULE";
    public static final String CACHE_NAME = "asbogosysAnsattListe";

    Logger logger = LoggerFactory.getLogger(ScheduledAnsattListePrefetch.class);

    @Autowired
    private NorgApi norgApi;

    @Autowired
    private GOSYSNAVansatt ansattWS;

    @Autowired
    CacheManager cacheManager;

    @Scheduled(cron = "${"+SCHEDULE_KEY+"}")
    public void prefetchAnsattListe() {
        logger.info("Starter prefetch av alle ansatte i alle enheter");

        cacheManager.getCache(CACHE_NAME).clear();

        List<NorgDomain.Enhet> alleEnheter = norgApi.hentEnheter(null, OppgaveBehandlerFilter.KUN_OPPGAVEBEHANDLERE, NorgApi.getIKKE_NEDLAGT());
        alleEnheter.forEach(ansattEnhet -> {
            ASBOGOSYSNavEnhet hentNAVAnsattListeRequest = new ASBOGOSYSNavEnhet();
            hentNAVAnsattListeRequest.setEnhetsId(ansattEnhet.getEnhetId());
            hentNAVAnsattListeRequest.setEnhetsNavn(ansattEnhet.getEnhetNavn());
            try {
                ansattWS.hentNAVAnsattListe(hentNAVAnsattListeRequest);
            } catch (Exception exception) {
                logger.warn("Prefetch av enhet {}:{} til cache feilet med melding {}", ansattEnhet.getEnhetId(), ansattEnhet.getEnhetNavn(), exception.getMessage());
            }
        });
        logger.info("Ferdig behandlet prefetch av ansatte i {} enheter", alleEnheter.size());
    }
}
