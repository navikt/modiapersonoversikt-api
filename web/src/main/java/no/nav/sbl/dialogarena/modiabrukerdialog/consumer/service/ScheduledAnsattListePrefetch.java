package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
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
    private OrganisasjonEnhetV2Service organisasjonEnhetService;

    @Autowired
    private GOSYSNAVansatt ansattWS;

    @Autowired
    CacheManager cacheManager;

    @Scheduled(cron = "${"+SCHEDULE_KEY+"}")
    public void prefetchAnsattListe() {
        logger.info("Starter prefetch av alle ansatte i alle enheter");

        cacheManager.getCache(CACHE_NAME).clear();

        List<AnsattEnhet> alleEnheter = organisasjonEnhetService.hentAlleEnheter(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE);
        alleEnheter.forEach(ansattEnhet -> {
            ASBOGOSYSNavEnhet hentNAVAnsattListeRequest = new ASBOGOSYSNavEnhet();
            hentNAVAnsattListeRequest.setEnhetsId(ansattEnhet.enhetId);
            hentNAVAnsattListeRequest.setEnhetsNavn(ansattEnhet.enhetNavn);
            try {
                ansattWS.hentNAVAnsattListe(hentNAVAnsattListeRequest);
            } catch (Exception exception) {
                logger.warn("Prefetch av enhet {}:{} til cache feilet med melding {}", ansattEnhet.enhetId, ansattEnhet.enhetNavn, exception.getMessage());
            }
        });
        logger.info("Ferdig behandlet prefetch av ansatte i {} enheter", alleEnheter.size());
    }
}
