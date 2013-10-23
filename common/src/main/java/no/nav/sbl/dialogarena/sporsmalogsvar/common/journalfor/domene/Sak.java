package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

import java.io.Serializable;
import org.joda.time.DateTime;

public class Sak implements Serializable {
    private final String saksId;
    private final String sakstype;
    private final String fagsystem;
    private final String temakode;
    private final DateTime opprettetDato;

    public Sak(String saksId, String sakstype, String fagsystem, String temakode, DateTime opprettetDato) {
        this.saksId = saksId;
        this.sakstype = sakstype;
        this.fagsystem = fagsystem;
        this.temakode = temakode;
        this.opprettetDato = opprettetDato;
    }

    public DateTime getOpprettetDato() {
        return opprettetDato;
    }

    public String getTemakode() {
        return temakode;
    }

    public String getFagsystem() {
        return fagsystem;
    }

    public String getSakstype() {
        return sakstype;
    }

    public String getSaksId() {
        return saksId;
    }
}