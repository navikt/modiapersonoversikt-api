package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.CmsSkrivestotte;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.Hjelpetekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndex;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndexImpl;
import org.apache.commons.collections15.Closure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.Hjelpetekst.LOCALE_ANDRE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.Hjelpetekst.LOCALE_DEFAULT;
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
        List<Hjelpetekst> hjelpetekster = CmsSkrivestotte.hentHjelpetekster(appresUrl, LOCALE_DEFAULT);

        final Map<String, List<Hjelpetekst>> parentHjelpetekster = on(hjelpetekster).reduce(indexBy(Hjelpetekst.KEY));

        for (final String locale : LOCALE_ANDRE) {
            on(CmsSkrivestotte.hentHjelpetekster(appresUrl, locale)).forEach(new Closure<Hjelpetekst>() {
                @Override
                public void execute(Hjelpetekst hjelpetekst) {
                    if (parentHjelpetekster.containsKey(hjelpetekst.key)) {
                        parentHjelpetekster.get(hjelpetekst.key).get(0).leggTilLocale(locale, hjelpetekst.innhold);
                    }
                }
            });
        }

        hjelpetekstIndex().indekser(hjelpetekster);
    }
}
