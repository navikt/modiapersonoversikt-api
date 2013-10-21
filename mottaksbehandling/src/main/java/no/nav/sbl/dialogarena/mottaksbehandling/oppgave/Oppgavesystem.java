package no.nav.sbl.dialogarena.mottaksbehandling.oppgave;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.types.Pingable;

public interface Oppgavesystem extends Pingable {

	String lagOppgave(String behandlingsId, String fodselsnummer, Tema tema);
    Record<Oppgave> hentOppgave(String oppgaveId);
    Optional<Record<Oppgave>> plukkOppgave(Tema tema);
    void fristill(String oppgaveId, String begrunnelse);
	void ferdigstill(String oppgaveId);

}
