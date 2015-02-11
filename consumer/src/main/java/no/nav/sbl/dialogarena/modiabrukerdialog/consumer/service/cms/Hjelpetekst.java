package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import no.nav.modig.lang.option.Optional;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;

public class Hjelpetekst {

    public static final String LOCALE_DEFAULT = "nb_NO";

    public final String tittel;
    public List<String> tags;
    public Map<String, String> innhold;

    public Hjelpetekst(String tittel, Map<String, String> innhold, String... tags) {
        this(tittel, innhold, asList(tags));
    }

    public Hjelpetekst(String tittel, Map<String, String> innhold, List<String> tags) {
        this.tittel = tittel;
        this.innhold = innhold;
        this.tags = tags;
    }

    public Optional<String> getDefaultLocaleInnhold() {
        return optional(innhold.get(LOCALE_DEFAULT));
    }

    public Boolean isValid() {
        return getDefaultLocaleInnhold().isSome();
    }
}