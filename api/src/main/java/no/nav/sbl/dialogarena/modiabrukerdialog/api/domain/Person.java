package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

public class Person implements Serializable {
    public final String fornavn, etternavn, navn;

    public Person(String fornavn, String etternavn) {
        this(fornavn, etternavn, true);
    }

    public Person(String fornavn, String etternavn, boolean transformToNameCase) {
        if (transformToNameCase) {
            fornavn = namifyString(fornavn);
            etternavn = namifyString(etternavn);
        }
        this.fornavn = fornavn;
        this.etternavn = etternavn;
        this.navn = String.format("%s %s", this.fornavn, this.etternavn);
    }

    private static String namifyString(String navn) {
        return capitalize(lowerCase(navn));
    }

}
