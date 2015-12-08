package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.nav.modig.lang.option.Optional;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;

public class SkrivestotteTekst {

    public static final String LOCALE_DEFAULT = "nb_NO";

    public final String key, tittel;
    public List<String> tags;
    public Map<String, String> innhold;

    public SkrivestotteTekst() {
        key = "";
        tittel = "";
    }

    public SkrivestotteTekst(String key, String tittel, Map<String, String> innhold, String... tags) {
        this(key, tittel, innhold, asList(tags));
    }

    public SkrivestotteTekst(String key, String tittel, Map<String, String> innhold, List<String> tags) {
        this.key = key;
        this.tittel = tittel;
        this.innhold = innhold;
        this.tags = tags;
    }

    @JsonIgnore
    public Optional<String> getDefaultLocaleInnhold() {
        return optional(innhold.get(LOCALE_DEFAULT));
    }

    @JsonIgnore
    public Boolean isValid() {
        return getDefaultLocaleInnhold().isSome();
    }
}