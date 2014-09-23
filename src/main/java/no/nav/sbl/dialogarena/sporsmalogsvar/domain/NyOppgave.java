package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKode;

import java.io.Serializable;

public class NyOppgave implements Serializable {
    public GsakKode.Tema tema;
    public GsakKode.OppgaveType type;
    public GsakKode.Prioritet prioritet;
    public AnsattEnhet enhet;
    public String beskrivelse, henvendelseId;
}
