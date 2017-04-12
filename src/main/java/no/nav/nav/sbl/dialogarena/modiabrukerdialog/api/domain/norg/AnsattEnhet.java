package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg;

import org.apache.commons.collections15.Transformer;

import java.io.Serializable;

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

    public static final Transformer<AnsattEnhet, String> ENHET_ID = ansattEnhet -> ansattEnhet.enhetId;
}
