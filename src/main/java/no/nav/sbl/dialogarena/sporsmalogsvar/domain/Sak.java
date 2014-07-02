package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;

public class Sak implements Serializable, Comparable<Sak> {

    public String saksId, tema, fagsystem;
    public DateTime opprettetDato;

    public static final Transformer<Sak, String> TEMA = new Transformer<Sak, String>() {
        @Override
        public String transform(Sak sak) {
            return sak.tema;
        }
    };

    @Override
    public int compareTo(Sak other) {
        return other.opprettetDato.compareTo(opprettetDato);
    }

}
