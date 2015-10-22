package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

public class Oppgave {

    public final String oppgaveId, fnr, henvendelseId;

    public Oppgave(String oppgaveId, String fnr, String henvendelseId) {
        this.oppgaveId = oppgaveId;
        this.fnr = fnr;
        this.henvendelseId = henvendelseId;
    }
}
