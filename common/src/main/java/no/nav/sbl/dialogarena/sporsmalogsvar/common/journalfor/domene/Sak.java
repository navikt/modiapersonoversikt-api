package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

import java.io.Serializable;
import org.joda.time.DateTime;

public class Sak implements Serializable {
    public String saksId, sakstype, fagsystem;
    public Arkivtema arkivtema;
    public DateTime opprettetDato;

    public Sak(String saksId, String sakstype, String fagsystem, Arkivtema arkivtema, DateTime opprettetDato) {
        this.saksId = saksId;
        this.sakstype = sakstype;
        this.fagsystem = fagsystem;
        this.arkivtema = arkivtema;
        this.opprettetDato = opprettetDato;
    }
}