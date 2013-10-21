package no.nav.sbl.dialogarena.mottaksbehandling.lagring;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.types.Pingable;

public interface HenvendelseRepo extends Pingable{
	
	Optional<Record<SporsmalOgSvar>> hentMedBehandlingsId(String behandlingsId);
	Optional<Record<SporsmalOgSvar>> hentMedOppgaveId(String oppgaveId);

	Record<SporsmalOgSvar> opprett(Record<SporsmalOgSvar> henvendelse);
	void oppdater(Record<SporsmalOgSvar> henvendelse);

}
