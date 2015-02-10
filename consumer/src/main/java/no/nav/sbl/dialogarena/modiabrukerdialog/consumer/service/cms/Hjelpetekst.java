package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import org.apache.commons.collections15.Transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class Hjelpetekst {

    public static final String LOCALE_DEFAULT = "nb";
    public static final List<String> LOCALE_ANDRE = asList("en");

    public final String key, tittel, innhold;
    public List<String> tags;
    public final Map<String, String> locales = new HashMap<>();

    public Hjelpetekst(String key, String tittel, String innhold, String... tags) {
        this.key = key;
        this.tittel = tittel;
        this.innhold = innhold;
        this.tags = asList(tags);
        this.locales.put(LOCALE_DEFAULT, innhold);
    }

    public static final Transformer<Hjelpetekst, String> KEY = new Transformer<Hjelpetekst, String>() {
        @Override
        public String transform(Hjelpetekst hjelpetekst) {
            return hjelpetekst.key;
        }
    };
}