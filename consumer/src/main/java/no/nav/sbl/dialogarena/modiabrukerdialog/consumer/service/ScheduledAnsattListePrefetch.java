package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.EnhetService;
import org.apache.commons.collections15.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;

public class ScheduledAnsattListePrefetch {

    public static final String KL_7_OG_KL_19_MANDAG_TIL_FERDAG = "0 0 7,19 * * MON-FRI";
    public static final String CACHE_NAME = "asbogosysAnsattListe";

    Logger logger = LoggerFactory.getLogger(ScheduledAnsattListePrefetch.class);

    @Inject
    private EnhetService enhetService;

    @Inject
    private GOSYSNAVansatt ansattWS;

    @Inject
    CacheManager cacheManager;

    @Scheduled(cron = KL_7_OG_KL_19_MANDAG_TIL_FERDAG)
    public void prefetchAnsattListe() {
        logger.info("Starter prefetch av alle ansatte i alle enheter");

        cacheManager.getCache(CACHE_NAME).clear();

        List<AnsattEnhet> alleEnheter = enhetService.hentAlleEnheter();
        on(alleEnheter).forEach(new Closure<AnsattEnhet>() {
            @Override
            public void execute(AnsattEnhet ansattEnhet) {
                ASBOGOSYSNavEnhet hentNAVAnsattListeRequest = new ASBOGOSYSNavEnhet();
                hentNAVAnsattListeRequest.setEnhetsId(ansattEnhet.enhetId);
                hentNAVAnsattListeRequest.setEnhetsNavn(ansattEnhet.enhetNavn);
                try {
                    ansattWS.hentNAVAnsattListe(hentNAVAnsattListeRequest);
                } catch (Exception exception) {
                    logger.warn("Prefetch av enhet {}:{} til cache feilet med melding {}", ansattEnhet.enhetId, ansattEnhet.enhetNavn, exception.getMessage());
                }
            }
        });
        logger.info("Ferdig behandlet prefetch av ansatte i {} enheter", alleEnheter.size());
    }
}
