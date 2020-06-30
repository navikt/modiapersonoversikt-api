package no.nav.modig.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class ContentRetriever {
    private static Logger logger = LoggerFactory.getLogger(ContentRetriever.class);
    private Map<String, String> tekster = new HashMap<>();

    public ContentRetriever load(String... resourceBundles) {
        for (String resourceBundleNavn : resourceBundles) {
            Map<String, String> bundleTekster = getAll(ResourceBundle.getBundle(resourceBundleNavn, new Locale("nb"), new UTF8Control()));
            tekster.putAll(bundleTekster);
        }

        return this;
    }

    public ContentRetriever load(Map<String, String> tekster) {
        this.tekster = tekster
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> ParagraphRemover.remove(entry.getValue())));

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

    public String getDefaultLocale() {
        return "nb";
    }

    private static Map<String, String> getAll(ResourceBundle bundle) {
        Map<String, String> map = new HashMap<>();
        for (String key : Collections.list(bundle.getKeys())) {
            map.put(key, ParagraphRemover.remove(bundle.getString(key)));
        }
        return map;
    }


    /**
     * A modification of the default implementation ResourceBundle.Control that uses an InputStream with UTF-8 charset. Ripped from
     * http://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle
     */
    private static class UTF8Control extends ResourceBundle.Control {
        public ResourceBundle newBundle
                (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            // The below is a copy of the default implementation.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }
}
