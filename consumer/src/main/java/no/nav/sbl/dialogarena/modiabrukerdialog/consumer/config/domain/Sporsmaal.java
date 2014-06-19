package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Sporsmaal implements Serializable {
    public final String id;
    public String fritekst, tema, oppgaveId;
    public DateTime opprettetDato;

    public Sporsmaal(String id, DateTime opprettetDato) {
        this.id = id;
        this.opprettetDato = opprettetDato;
    }
}
