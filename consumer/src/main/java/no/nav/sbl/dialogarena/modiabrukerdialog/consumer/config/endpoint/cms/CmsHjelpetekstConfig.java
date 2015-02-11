package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;

@Configuration
public class CmsHjelpetekstConfig {

    public static final String CMS_SKRIVESTOTTE_KEY = "start.cms.skrivestotte.withmock";

    private static final int EN_TIME_MILLIS = 60 * 60 * 1000;

    @Bean
    public CmsSkrivestotte cmsSkrivestotte() {
        return createSwitcher(new CmsSkrivestotteEnonic(), new CmsSkrivestotteMock(), CMS_SKRIVESTOTTE_KEY, CmsSkrivestotte.class);
    }

    @Bean
    public HjelpetekstIndex hjelpetekstIndex() {
        return new HjelpetekstIndex();
    }

    @Scheduled(fixedRate = EN_TIME_MILLIS, initialDelay = 5000)
    public void reIndekserHjelpetekster() {
        List<Hjelpetekst> hjelpetekster = cmsSkrivestotte().hentHjelpetekster();
        hjelpetekstIndex().indekser(hjelpetekster);
    }
}
