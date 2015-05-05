package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Ansatt;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GsakKodeTema;

import java.io.Serializable;
import java.util.List;

public class NyOppgave implements Serializable {
    public GsakKodeTema.Tema tema;
    public GsakKodeTema.OppgaveType type;
    public GsakKodeTema.Prioritet prioritet;
    public GsakKodeTema.Underkategori underkategori;
    public AnsattEnhet enhet;
    public String beskrivelse, henvendelseId, brukerId;
    public List<Ansatt> ansatteTilknyttetEnhet;
    public Ansatt valgtAnsatt;
}
