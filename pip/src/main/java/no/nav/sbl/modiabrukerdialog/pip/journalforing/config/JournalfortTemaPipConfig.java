package no.nav.sbl.modiabrukerdialog.pip.journalforing.config;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import net.sf.ehcache.CacheManager;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NAVAnsattEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.AnsattServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.SaksbehandlerInnstillingerServiceImpl;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.DefaultJournalfortTemaAttributeLocatorDelegate;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.JournalfortTemaAttributeLocatorDelegate;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import javax.inject.Inject;

@Configuration
@EnableCaching
@ImportResource("classpath*:*cacheconfig.xml")
@Import({NAVAnsattEndpointConfig.class})
public class JournalfortTemaPipConfig {

    @Inject
    private GOSYSNAVansatt gosysNAVAnsatt;

    @Bean
    public EhCacheCacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(CacheManager.create());
        return cacheManager;
    }

    @Bean
    public JournalfortTemaAttributeLocatorDelegate enhetAttributeLocatorDelegate() {
        return new DefaultJournalfortTemaAttributeLocatorDelegate(gosysNAVAnsatt);
    }

    @Bean
    public AnsattService ansattService() {
        return new AnsattServiceImpl();
    }

    @Bean
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService() {
        return new SaksbehandlerInnstillingerServiceImpl();
    }

}
