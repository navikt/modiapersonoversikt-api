package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;

import java.io.Serializable;

public class NyOppgave implements Serializable {
    public GsakKodeTema.Tema tema;
    public GsakKodeTema.OppgaveType type;
    public GsakKodeTema.Prioritet prioritet;
    public AnsattEnhet enhet;
    public String beskrivelse, henvendelseId;
}
