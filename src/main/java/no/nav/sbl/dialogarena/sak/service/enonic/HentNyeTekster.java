package no.nav.sbl.dialogarena.sak.service.enonic;


import net.sf.ehcache.CacheManager;
import no.nav.modig.content.Content;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.enonic.innholdstekst.Innholdstekst;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static java.lang.Math.random;
import static java.lang.System.getProperty;
import static java.lang.System.lineSeparator;
import static no.nav.modig.content.enonic.innholdstekst.Innholdstekst.KEY;
import static org.apache.commons.io.FileUtils.write;
import static org.slf4j.LoggerFactory.getLogger;

public class HentNyeTekster {

    protected final Logger logger = getLogger(getClass());

    @Inject
    private EnonicStringHandler enonicStringHandler;

    @Inject
    private ContentRetriever enonicContentRetriever;

    @Inject
    private CacheManager cacheManager;

    //Hent innholdstekster på nytt hver time
    @Scheduled(cron = "0 * * * * *")
    public void lastInnNyeInnholdstekster() {
        logger.debug("Leser inn innholdstekster fra enonic");
        clearContentCache();
        try {
            saveLocal("enonic/saksoversikt_nb_NO.properties", new URI(getProperty("appres.cms.url") + "/app/saksoversikt/nb_NO/tekster?" + random()));
            saveLocal("enonic/saksoversikt_en_GB.properties", new URI(getProperty("appres.cms.url") + "/app/saksoversikt/en_GB/tekster?" + random()));
        } catch (Exception e) {
            logger.warn("Feilet under henting av enonic innholdstekster: " + e, e);
        }
        enonicStringHandler.clearCache();
    }

    private void clearContentCache() {
        cacheManager.getCache("cms.content").flush();
    }

    private void saveLocal(String filename, URI uri) throws IOException {
        File file = new File(getProperty("saksoversikt.datadir"), filename);
        logger.debug("Leser inn innholdstekster fra " + uri + " til: " + file.toString());
        Content<Innholdstekst> content = enonicContentRetriever.getContent(uri);
        StringBuilder data = new StringBuilder();
        Map<String, Innholdstekst> innhold = content.toMap(KEY);
        if (!innhold.isEmpty()) {
            for (Map.Entry<String, Innholdstekst> entry : innhold.entrySet()) {
                data.append(entry.getValue().key).append('=').append(removeNewline(entry.getValue().value)).append(lineSeparator());
            }
            write(file, data, "UTF-8");
        }
    }

    private String removeNewline(String value) {
        return value.replaceAll("\n", "");
    }
}
