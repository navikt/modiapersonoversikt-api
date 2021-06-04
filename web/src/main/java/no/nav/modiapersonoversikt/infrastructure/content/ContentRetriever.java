package no.nav.modiapersonoversikt.infrastructure.content;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentRetriever {
    private final static Logger logger = LoggerFactory.getLogger(ContentRetriever.class);
    private Map<String, String> tekster = new HashMap<>();

    public ContentRetriever load(String... resourceBundles) {
        for (String resourceBundleNavn : resourceBundles) {
            Map<String, String> bundleTekster = getAll(ResourceBundle.getBundle(resourceBundleNavn, new Locale("nb")));
            tekster.putAll(bundleTekster);
        }

        return this;
    }

    public ContentRetriever load(Reader reader) {
        Properties properties = new Properties();
        try {
            properties.load(reader);
            properties.forEach((key, value) ->
                    tekster.put((String) key, ParagraphRemover.remove((String) value))
            );
        } catch (IOException e) {
            logger.error("Kunne ikke laste properties", e);
        } finally {
            logger.info("Antall tekster lastet: {}", tekster.size());
        }
        return this;
    }

    public Map<String, String> hentAlleTekster() {
        return tekster;
    }

    public String hentTekst(String key) {
        return tekster.get(key);
    }

    public String hentTekst(String key, String locale) {
        return tekster.get(key);
    }

    private static Map<String, String> getAll(ResourceBundle bundle) {
        Map<String, String> map = new HashMap<>();
        for (String key : Collections.list(bundle.getKeys())) {
            map.put(key, ParagraphRemover.remove(bundle.getString(key)));
        }
        return map;
    }

}
