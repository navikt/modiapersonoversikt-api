package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;

public class Sak implements Serializable {
    public final String saksId;
    public final String sakstype;
    public final String temakode;
    public final DateTime opprettetDato;

    public Sak(String saksId, String sakstype, String temakode, DateTime opprettetDato) {
        this.saksId = saksId;
        this.sakstype = sakstype;
        this.temakode = temakode;
        this.opprettetDato = opprettetDato;
    }

    public static final Transformer<Sak, DateTime> OPPRETTET_DATO = new Transformer<Sak, DateTime>() {
        @Override
        public DateTime transform(Sak sak) {
            return sak.opprettetDato;
        }
    };

    public static final Transformer<Sak, String> TEMAKODE = new Transformer<Sak, String>() {
        @Override
        public String transform(Sak sak) {
            return sak.temakode;
        }
    };
}