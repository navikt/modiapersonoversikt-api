package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import java.io.Serializable;

public class Oppgave implements Serializable {

    public final String oppgaveId, fnr, henvendelseId;
    public String svarHenvendelseId;

    public Oppgave(String oppgaveId, String fnr, String henvendelseId) {
        this.oppgaveId = oppgaveId;
        this.fnr = fnr;
        this.henvendelseId = henvendelseId;
    }

    public void setSvarHenvendelseId(String svarHenvendelseId) {
        this.svarHenvendelseId = svarHenvendelseId;
    }

}
