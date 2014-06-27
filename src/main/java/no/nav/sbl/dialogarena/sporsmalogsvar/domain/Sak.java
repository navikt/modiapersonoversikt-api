package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;

public class Sak implements Serializable {

    public String saksId, fagomrade, fagsak;
    public DateTime opprettetDato;

    public static final Transformer<Sak, String> FAGOMRADE = new Transformer<Sak, String>() {
        @Override
        public String transform(Sak sak) {
            return sak.fagomrade;
        }
    };

}
