package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

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
}