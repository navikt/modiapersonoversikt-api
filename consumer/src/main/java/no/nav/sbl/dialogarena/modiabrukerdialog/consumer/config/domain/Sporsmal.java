package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Sporsmal implements Serializable {
    public final String id;
    public String fritekst, temagruppe, oppgaveId, konorsperretEnhet;
    public DateTime opprettetDato;

    public Sporsmal(String id, DateTime opprettetDato) {
        this.id = id;
        this.opprettetDato = opprettetDato;
    }
}
