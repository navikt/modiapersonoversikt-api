package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.GsakKodeTema;

import java.io.Serializable;

public class NyOppgave implements Serializable {
    public GsakKodeTema.Tema tema;
    public GsakKodeTema.OppgaveType type;
    public GsakKodeTema.Prioritet prioritet;
    public AnsattEnhet enhet;
    public String beskrivelse, henvendelseId, brukerId;

    public void nullstill() {
        tema = null;
        type = null;
        prioritet = null;
        enhet = null;
        beskrivelse = null;
        henvendelseId = null;
        brukerId = null;
    }
}
