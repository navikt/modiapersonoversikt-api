package no.nav.modiapersonoversikt.api.domain;

import java.io.Serializable;

public class Oppgave implements Serializable {

    public final String oppgaveId, fnr, henvendelseId;
    public final boolean erSTOOppgave;
    public String svarHenvendelseId;

    public Oppgave(String oppgaveId, String fnr, String henvendelseId, boolean erSTOOppgave) {
        this.oppgaveId = oppgaveId;
        this.fnr = fnr;
        this.henvendelseId = henvendelseId;
        this.erSTOOppgave = erSTOOppgave;
    }

    public Oppgave withSvarHenvendelseId(String svarHenvendelseId) {
        this.svarHenvendelseId = svarHenvendelseId;
        return this;
    }

}
