package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain;

import org.apache.commons.collections15.Transformer;

import java.io.Serializable;

public class AnsattEnhet implements Serializable {
    public String enhetId;
    public String enhetNavn;

    public AnsattEnhet(String enhetId, String enhetNavn) {
        this.enhetId = enhetId;
        this.enhetNavn = enhetNavn;
    }

    public static final Transformer<AnsattEnhet, String> ENHET_ID = new Transformer<AnsattEnhet, String>() {
        @Override
        public String transform(AnsattEnhet ansattEnhet) {
            return ansattEnhet.enhetId;
        }
    };
}
