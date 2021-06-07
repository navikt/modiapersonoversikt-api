package no.nav.modiapersonoversikt.legacy.api.domain.norg;

import java.io.Serializable;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.upperCase;

public class AnsattEnhet implements Serializable {

    public String enhetId;
    public String enhetNavn;
    public String status;

    public AnsattEnhet(String enhetId, String enhetNavn) {
        this.enhetId = enhetId;
        this.enhetNavn = enhetNavn;
    }

    public AnsattEnhet(String enhetId, String enhetNavn, String status) {
        this.enhetId = enhetId;
        this.enhetNavn = enhetNavn;
        this.status = status;
    }

    public boolean erAktiv() {
        return "AKTIV".equals(upperCase(status));
    }

    public static final Function<AnsattEnhet, String> TIL_ENHET_ID = ansattEnhet -> ansattEnhet.enhetId;
}
