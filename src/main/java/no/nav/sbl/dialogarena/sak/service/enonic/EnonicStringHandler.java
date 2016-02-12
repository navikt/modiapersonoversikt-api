package no.nav.sbl.dialogarena.sak.service.enonic;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.*;

public class EnonicStringHandler extends ReloadableResourceBundleMessageSource {

    private Map<String, FileTuple> basenames = new HashMap<>();

    public Properties getBundleFor(String type, Locale locale) {
        if (basenames.containsKey(type)) {
            Properties properties = new Properties();

            properties.putAll(hentRemoteEllerLocal(basenames.get(type), locale));

            return properties;
        } else {
            return getMergedProperties(locale).getProperties();
        }
    }

    private Properties hentRemoteEllerLocal(FileTuple fileTuple, Locale locale) {
        String localFile = calculateFilenameForLocale(fileTuple.localFile, locale);
        String remoteFile = calculateFilenameForLocale(fileTuple.remoteFile, locale);

        Properties properties = getProperties(localFile).getProperties();
        Properties remoteProperties = getProperties(remoteFile).getProperties();
        if (remoteProperties != null) {
            properties.putAll(remoteProperties);
        }

        return properties;
    }

    private String calculateFilenameForLocale(String type, Locale locale) {
        return type + "_" + locale.getLanguage() + "_" + locale.getCountry();
    }

    public void setBasenames(Bundle... soknadBundles) {
        List<String> basenameStrings = new ArrayList<>();

        List<Bundle> bundlesList = Arrays.asList(soknadBundles);

        for (Bundle bundle : bundlesList) {
            basenames.put(bundle.type, bundle.tuple);
            basenameStrings.add(bundle.tuple.remoteFile);
            basenameStrings.add(bundle.tuple.localFile);
        }

        setBasenames(basenameStrings.toArray(new String[basenameStrings.size()]));
    }

    private static class FileTuple {
        private String remoteFile;
        private String localFile;
        FileTuple(String remoteFile, String localFile) {
            this.remoteFile = remoteFile;
            this.localFile = localFile;
        }
    }

    public static class Bundle {
        public String type;
        public FileTuple tuple;
        public Bundle(String type, String remoteFile, String localFile) {
            this.type = type;
            this.tuple = new FileTuple(remoteFile, localFile);
        }
    }
}
