package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Sak implements Serializable {
    public final String saksId;
    public final String sakstype;
    public final String fagsystem;
    public final String temakode;
    public final DateTime opprettetDato;
    public final String statuskode;

    public Sak(String saksId, String sakstype, String fagsystem, String temakode, DateTime opprettetDato, String statuskode) {
        this.saksId = saksId;
        this.sakstype = sakstype;
        this.fagsystem = fagsystem;
        this.temakode = temakode;
        this.opprettetDato = opprettetDato;
        this.statuskode = statuskode;
    }
}