package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.CmsSkrivestotte;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.CmsSkrivestotteEnonic;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteSok;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteTekst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;


@Configuration
public class CmsSkrivestotteConfig {

    private static final int EN_TIME_MILLIS = 60 * 60 * 1000;

    @Bean
    public CmsSkrivestotte cmsSkrivestotte() {
        return createTimerProxyForWebService("CmsSkrivestotte", new CmsSkrivestotteEnonic(), CmsSkrivestotte.class);
    }

    @Bean
    public SkrivestotteSok skrivestotteSok() {
        return new SkrivestotteSok();
    }

    @Scheduled(fixedRate = EN_TIME_MILLIS, initialDelay = 5000)
    public void reIndekserSkrivestotteTekster() {
        List<SkrivestotteTekst> skrivestotteTekster = cmsSkrivestotte().hentSkrivestotteTekster();
        skrivestotteSok().indekser(skrivestotteTekster);
    }
}
