package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;

public class Sak implements Serializable {
    public final String saksId;
    public final String sakstype;
    public final String temakode;
    public final DateTime opprettetDato;
    public final String statuskode;
    public final String fagsystem;

    public Sak(String saksId, String sakstype, String fagsystem, String temakode, DateTime opprettetDato, String statuskode) {
        this.saksId = saksId;
        this.fagsystem = fagsystem;
        this.sakstype = sakstype;
        this.temakode = temakode;
        this.opprettetDato = opprettetDato;
        this.statuskode = statuskode;
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
