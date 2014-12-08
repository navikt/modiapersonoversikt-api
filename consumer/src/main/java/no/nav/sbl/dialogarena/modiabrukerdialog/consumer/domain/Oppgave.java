package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain;

public class Oppgave {

    public final String oppgaveId, fnr, henvendelseId;

    public Oppgave(String oppgaveId, String fnr, String henvendelseId) {
        this.oppgaveId = oppgaveId;
        this.fnr = fnr;
        this.henvendelseId = henvendelseId;
    }
}
