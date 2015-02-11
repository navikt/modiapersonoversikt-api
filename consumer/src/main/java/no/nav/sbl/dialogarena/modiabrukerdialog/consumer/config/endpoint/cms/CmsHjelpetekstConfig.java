package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.CmsSkrivestotte;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.Hjelpetekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndex;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndexImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndexMock.createHjelpetekstIndexMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;

@Configuration
public class CmsHjelpetekstConfig {

    public static final String CMS_HJELPETEKST_KEY = "start.cms.hjelpetekst.withmock";

    private static final int EN_TIME_MILLIS = 60 * 60 * 1000;

    @Value("${appres.cms.url}")
    private String appresUrl;

    @Bean
    public HjelpetekstIndex hjelpetekstIndex() {
        HjelpetekstIndex hjelpetekstIndex = new HjelpetekstIndexImpl();

        return createSwitcher(hjelpetekstIndex, createHjelpetekstIndexMock(), CMS_HJELPETEKST_KEY, HjelpetekstIndex.class);
    }

    @Scheduled(fixedRate = EN_TIME_MILLIS, initialDelay = 5000)
    public void reIndekserHjelpetekster() {
        List<Hjelpetekst> hjelpetekster = CmsSkrivestotte.hentHjelpetekster(appresUrl);
        hjelpetekstIndex().indekser(hjelpetekster);
    }
}
