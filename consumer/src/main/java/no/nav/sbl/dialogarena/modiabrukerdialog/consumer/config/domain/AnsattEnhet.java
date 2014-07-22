package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain;

import java.io.Serializable;

public class AnsattEnhet implements Serializable {
    public String enhetId;
    public String enhetNavn;

    public AnsattEnhet(String enhetId, String enhetNavn) {
        this.enhetId = enhetId;
        this.enhetNavn = enhetNavn;
    }
}
